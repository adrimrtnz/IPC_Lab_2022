/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp;

import java.util.HashSet;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

/**
 *
 * @author Adrián Martínez
 */
public class AvatarListCell extends ListCell<Avatar>{
    
    private ImageView view = new ImageView();
    
    
    @Override
    protected void updateItem(Avatar avatar, boolean empty) {
        super.updateItem(avatar, empty);
        
        if (avatar == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            view.setImage(avatar.getImage());  
            view.setPreserveRatio(true);
            view.setFitWidth(120);
            setGraphic(view);
            setText(avatar.getName());     
        }
    }
}
