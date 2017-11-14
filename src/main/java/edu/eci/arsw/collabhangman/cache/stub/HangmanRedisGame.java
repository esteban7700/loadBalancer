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
    public String addLetter(char l) throws GameServicesException {
        try {
            String word = (String) template.opsForHash().get("game:" + idPartida, "completeWord");
            String guessedWord = (String) template.opsForHash().get("game:" + idPartida, "discoverWord");
            
            if (word == null) {
                throw new GameServicesException("la sala no existe");
            }
            
            Object[] params = new Object[]{l,word,guessedWord};   
            
            template.execute(new SessionCallback< List< Object>>() {
                @SuppressWarnings("unchecked")
                @Override
                public < K, V> List<Object> execute(final RedisOperations< K, V> operations) throws DataAccessException {
                    operations.watch((K) ("game:" + idPartida + " discoverWord"));
                    operations.multi();
                    String operacion = operations.execute(script, Collections.singletonList("game:" + idPartida), params);
                    operations.opsForHash().put((K) ("game:" + idPartida), "currentWord", operacion);
                    return operations.exec();
                }
            });
            
            
//          for (int i = 0; i < word.length(); i++) {
//                if (word.charAt(i) == l) {
//                    charGuessedWord[i] = l;
//                }
//            }
            
            return new String(charGuessedWord);

        } catch (JedisConnectionException e) {
            throw new GameServicesException("La sesion con la base de datos  de regit se ha terminado");
        }
    }

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

    public boolean gameFinished() {
        String gameFinished = (String) template.opsForHash().get("game:" + idPartida, "state");
        return "finalizado".equals(gameFinished);
    }

    /**
     * @pre gameFinished=true;
     * @return winner's name
     */
    public String getWinnerName() {
        return (String) template.opsForHash().get("game:" + idPartida, "winner");
    }

    public String getCurrentGuessedWord() {
        return (String) template.opsForHash().get("game:" + idPartida, "discoverWord");
    }

}
