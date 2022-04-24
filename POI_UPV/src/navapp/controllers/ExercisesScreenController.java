/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.User;

/**
 * FXML Controller class
 *
 * @author Adrián Martínez
 */
public class ExercisesScreenController implements Initializable {
    
    @FXML private ToggleGroup tools;
    @FXML private ToggleGroup opciones;
    @FXML private ImageView mapImg;
    
    private User loggedUser;
    
    private double initialXTrans;
    private double initialYTrans;
    private double baseX;
    private double baseY;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setDragTool();
    }


    public void initializeUser(User user) {
        loggedUser = user;
        System.out.println("Usuario: " + loggedUser.getNickName());
    }
    
    public void setDragTool() {
        
        mapImg.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                initialXTrans = event.getSceneX();
                initialYTrans = event.getSceneY();
                baseX = mapImg.getTranslateX();
                baseY = mapImg.getTranslateY();
            }
        });
        
        
        mapImg.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double despX = event.getSceneX() - initialXTrans;
                double despY = event.getSceneY() - initialYTrans;
                mapImg.setTranslateX(baseX + despX);
                mapImg.setTranslateY(baseY + despY);
                event.consume();
            }
        });
    }
}
