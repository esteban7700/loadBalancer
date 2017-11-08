/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.collabhangman.cache.stub;

import edu.eci.arsw.collabhangman.model.game.HangmanGame;
import org.springframework.data.redis.core.StringRedisTemplate;

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
    public String addLetter(char l) {
        String word = (String) template.opsForHash().get("game:" + idPartida, "completeWord");
        String guessedWord = (String) template.opsForHash().get("game:" + idPartida, "discoverWord");
        String[] charGuessedWord = guessedWord.split("");
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == l) {
                charGuessedWord[i] = Character.toString(l);
            }
        }
        String val = "";
        for (String charGuessedWord1 : charGuessedWord) {
            val = val + charGuessedWord1;
        }
        template.opsForHash().put("game:" + idPartida,"discoverWord", val);
        return val;
    }

    public synchronized boolean tryWord(String playerName, String s) {
        String word = (String) template.opsForHash().get("game:" + idPartida, "completeWord");
        if (s.toLowerCase().equals(word)) {
            template.opsForHash().put("game:" + idPartida, "winner",playerName);
            template.opsForHash().put("game:" + idPartida,"state", "finalizado");
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
