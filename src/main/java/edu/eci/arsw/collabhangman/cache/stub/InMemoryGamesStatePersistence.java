/*
 * Copyright (C) 2016 Pivotal Software, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.arsw.collabhangman.cache.stub;

import edu.eci.arsw.collabhangman.model.game.HangmanGame;
import edu.eci.arsw.collabhangman.services.GameCreationException;
import edu.eci.arsw.collabhangman.services.GameServicesException;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import edu.eci.arsw.collabhangman.cache.GameStateCache;
import edu.eci.arsw.collabhangman.cache.GameStateCache;

/**
 *
 * @author hcadavid
 */
public class InMemoryGamesStatePersistence implements GameStateCache{
    
    private final ConcurrentHashMap<Integer,HangmanGame> gamesState;
    
    public InMemoryGamesStatePersistence() throws GameServicesException{
        gamesState=new ConcurrentHashMap<>();
        preloadGames();
    }
    
    @Override
    public void createGame(int id,String word) throws GameCreationException{
        if (gamesState.containsKey(id)){
            throw new GameCreationException("The game "+id+" already exist.");
        }
        else{
            gamesState.put(id, new HangmanGame(word));
        }
        
    }
    
    @Override
    public HangmanGame getGame(int gameid) throws GameServicesException{
        if (!gamesState.containsKey(gameid)){
            throw new GameServicesException("The game "+gameid+" doesnt exist.");
        }
        else{
            return gamesState.get(gameid);
        }
        
    }
    
    private void preloadGames() throws GameServicesException{
        HangmanGame hg=new HangmanGame("happiness");
        hg.addLetter('h');
        hg.addLetter('e');
        gamesState.put(1, hg);
        
        gamesState.put(2, new HangmanGame("foot"));
        gamesState.put(3, new HangmanGame("player"));
        gamesState.put(4, new HangmanGame("winner"));        
    }
    
}
