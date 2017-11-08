/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.collabhangman.cache.stub;

import edu.eci.arsw.collabhangman.cache.GameStateCache;
import edu.eci.arsw.collabhangman.model.game.HangmanGame;
import edu.eci.arsw.collabhangman.services.GameCreationException;
import edu.eci.arsw.collabhangman.services.GameServicesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author 2105534
 */
@Service
public class GameStateRedisCache implements GameStateCache{
    
    @Autowired
    private StringRedisTemplate template;
    
    @Override
    public void createGame(int id, String word) throws GameCreationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HangmanGame getGame(int gameid) throws GameServicesException {
        
        return new HangmanRedisGame("", gameid, template);
    }
}
