/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.collabhangman.cache.stub;

import edu.eci.arsw.collabhangman.model.game.HangmanGame;
import edu.eci.arsw.collabhangman.services.GameServicesException;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 *
 * @author 2105534
 */
public class HangmanRedisGame extends HangmanGame {

    private int idPartida;
    private StringRedisTemplate template;

    public HangmanRedisGame(String word, int idPartida, StringRedisTemplate template) {
        super(word);
        this.idPartida = idPartida;
        this.template = template;
    }

    /**
     * @pre gameFinished==false
     * @param l new letter
     * @return the secret word with all the characters 'l' revealed
     */
    public String addLetter(char l) throws GameServicesException {
        try{
            String word = (String) template.opsForHash().get("game:" + idPartida, "completeWord");
            System.out.println(word);
            if(word==null){
                throw new GameServicesException("la sala no existe");
            }
            String guessedWord = (String) template.opsForHash().get("game:" + idPartida, "discoverWord");
            char[] charGuessedWord = guessedWord.toCharArray();
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == l) {
                    charGuessedWord[i] = l;
                }
            }
            System.out.println(new String(charGuessedWord));
            template.opsForHash().put("game:" + idPartida,"discoverWord", new String (charGuessedWord));
            return new String (charGuessedWord);
        }catch(JedisConnectionException e){
            throw new GameServicesException("La sesion con la base de datos  de regit se ha terminado");
        }
    }

    public synchronized boolean tryWord(String playerName, String s) throws GameServicesException {
        String word = (String) template.opsForHash().get("game:" + idPartida, "completeWord");
        if(word==null){
                throw new GameServicesException("la sala no existe");
            }
        if (s.toLowerCase().equals(word)) {
            template.opsForHash().put("game:" + idPartida,"winner",playerName);
            template.opsForHash().put("game:" + idPartida,"state", "Finalizado");
            template.opsForHash().put("game:" + idPartida,"discoverWord", word);
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
