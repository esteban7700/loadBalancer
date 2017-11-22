/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.collabhangman.model.game.entities;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author hcadavid
 */
@Document(collection = "usuarios")
public class User {
    @Id
    private int id;
    
    private String name;
    
    private String photoUrl;
    
    private List<Score> scores = new ArrayList();

    public User(int id, String name, String photoUrl,List<Score> scores) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
        this.scores= scores;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * @return the scores
     */
    public List<Score> getScores() {
        return scores;
    }

    /**
     * @param scores the scores to set
     */
    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name=" + name + ", photoUrl=" + photoUrl + ", scores=" + scores + '}';
    }
    
    
    
}
