/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.collabhangman.model.game.entities;

import java.util.Date;
import org.springframework.data.annotation.Id;

/**
 *
 * @author 2105534
 */
public class Score {
    private int puntaje;
    private String fechaObtPuntaje;

    public Score(int puntaje, String fechaObtPuntaje) {
        this.puntaje = puntaje;
        this.fechaObtPuntaje = fechaObtPuntaje;
    }

    
    
    /**
     * @return the puntaje
     */
    public int getPuntaje() {
        return puntaje;
    }

    /**
     * @param puntaje the puntaje to set
     */
    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    /**
     * @return the fechaObtPuntaje
     */
    public String getFechaObtPuntaje() {
        return fechaObtPuntaje;
    }

    /**
     * @param fechaObtPuntaje the fechaObtPuntaje to set
     */
    public void setFechaObtPuntaje(String fechaObtPuntaje) {
        this.fechaObtPuntaje = fechaObtPuntaje;
    }
            
    
}
