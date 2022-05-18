/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp;

import javafx.scene.image.Image;

/**
 *
 * @author Adrián Martínez
 */
public class Avatar {
    private Image avatar;
    private String name;
    
    public Avatar(Image avatar, String name) {
        this.avatar = avatar;
        this.name = name;
    }
    
    public Image getImage() {
        return avatar;
    }
    
    public String getName() {
        return name;
    }
}
