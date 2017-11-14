/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.collabhangman.cache.stub;

import edu.eci.arsw.collabhangman.model.game.HangmanGame;
import edu.eci.arsw.collabhangman.services.GameServicesException;
import java.util.Collections;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 *
 * @author 2105534
 */
public class HangmanRedisGame extends HangmanGame {

    private int idPartida;
    private StringRedisTemplate template;
    private RedisScript<String> script;

    public HangmanRedisGame(String word, int idPartida, StringRedisTemplate template) {
        super(word);
        script = script();
        this.idPartida = idPartida;
        this.template = template;
        
    }
    
    public RedisScript<String> script() {
        DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script.lua")));
        redisScript.setResultType(String.class);
        return redisScript;
    }

    /**
     * @pre gameFinished==false
     * @param l new letter
     * @return the secret word with all the characters 'l' revealed
     */
    @Override
    public String addLetter(char l) throws GameServicesException {
        try {
            String word = (String) template.opsForHash().get("game:" + idPartida, "completeWord");
            String guessedWord = (String) template.opsForHash().get("game:" + idPartida, "discoverWord");
            
            if (word == null) {
                throw new GameServicesException("la sala no existe");
            }
            
            Object[] params = new Object[3];
            params[0]=l;
            
            
            template.execute(new SessionCallback< List< Object>>() {
                @SuppressWarnings("unchecked")
                @Override
                public < K, V> List<Object> execute(final RedisOperations< K, V> operations) throws DataAccessException {
                    operations.watch((K) ("game:" + idPartida + " discoverWord"));
                    operations.multi();
                    operations.execute(script, Collections.singletonList("game:" + idPartida),params);
                    return operations.exec();
                }
            });
            
            return getCurrentGuessedWord();

        } catch (JedisConnectionException e) {
            throw new GameServicesException("La sesion con la base de datos  de regit se ha terminado");
        }
    }

    @Override
    public synchronized boolean tryWord(String playerName, String s) throws GameServicesException {
        String word = (String) template.opsForHash().get("game:" + idPartida, "completeWord");
        if (word == null) {
            throw new GameServicesException("la sala no existe");
        }
        if (s.toLowerCase().equals(word)) {

            template.execute(new SessionCallback< List< Object>>() {
                @SuppressWarnings("unchecked")
                @Override
                public < K, V> List<Object> execute(final RedisOperations< K, V> operations) throws DataAccessException {
                    operations.watch((K) ("game:" + idPartida + " discoverWord"));
                    operations.multi();

                    operations.opsForHash().put((K) ("game:" + idPartida), "discoverWord", word);
                    operations.opsForHash().put((K) ("game:" + idPartida), "winner", playerName);
                    operations.opsForHash().put((K) ("game:" + idPartida), "state", "Finalizado");

                    return operations.exec();
                }
            });

            return true;
        }
        return false;
    }

    @Override
    public boolean gameFinished() throws GameServicesException{
        String gameFinished = (String) template.opsForHash().get("game:" + idPartida, "state");
         if (gameFinished == null) {
            throw new GameServicesException("ID inconrecto");
        }
        return "finalizado".equals(gameFinished);
    }

    /**
     * @pre gameFinished=true;
     * @return winner's name
     */
    @Override
    public String getWinnerName() throws GameServicesException{
        
        String winner = (String) template.opsForHash().get("game:" + idPartida, "winner");
        if (winner == null) {
            throw new GameServicesException("ID inconrecto");
        }
        return winner;
    }

    @Override
    public String getCurrentGuessedWord() throws GameServicesException{
        String cgw = (String) template.opsForHash().get("game:" + idPartida, "discoverWord");
        if (cgw == null) {
            throw new GameServicesException("ID inconrecto");
        }
        return cgw;
    }

}
