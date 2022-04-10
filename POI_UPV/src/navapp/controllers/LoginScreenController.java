/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Adrián Martínez
 */
public class LoginScreenController implements Initializable {

    @FXML private Button closeBtn;
    @FXML private Button minimizeBtn;
    @FXML private Button maximizeButton;
    @FXML private Text errorTxtField;
    @FXML private ToolBar toolBar;
    
    @FXML private Button loginBtn;
    
    @FXML private TextField userName;
    @FXML private PasswordField userPassword;
    
    private double xOffset;
    private double yOffset;
    private String errorTxtMsg;
    
 
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Cambiamos el contenido del campo de mensaje de error y lo ocultamos
        errorTxtMsg = "";
        errorTxtField.textProperty().setValue(errorTxtMsg);
        errorTxtField.setDisable(true);
        
        toolBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) toolBar.getScene().getWindow();
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        
        });
        
        
        toolBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Stage stage = (Stage) toolBar.getScene().getWindow();
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        
        });
        
        closeBtn.setOnAction((ActionEvent event) -> {
            Platform.exit();
        });
        
        minimizeBtn.setOnAction((ActionEvent event) -> {
            ((Stage) minimizeBtn.getScene().getWindow()).setIconified(true);
        });
    }    

    @FXML
    private void iniciarSesion(ActionEvent event) {
        
        // TODO cambiar los booleanos por booleanos con listeners sobre el estado de los TextFields
        if (userName.textProperty().isEmpty().get() && userPassword.textProperty().isEmpty().get()) {
            errorTxtMsg = "Debes introducir un Nombre de Usuario y Contraseña";
            errorTxtField.textProperty().setValue(errorTxtMsg);
            errorTxtField.setDisable(false);
        } else if (userName.textProperty().isEmpty().get()) {
            errorTxtMsg = "Debes introducir un Nombre de Usuario";
            errorTxtField.textProperty().setValue(errorTxtMsg);
            errorTxtField.setDisable(false);
        } else if (userPassword.textProperty().isEmpty().get()) {
            errorTxtMsg = "Debes introducir una Contraseña de Usuario";
            errorTxtField.textProperty().setValue(errorTxtMsg);
            errorTxtField.setDisable(false);
        }
        else {
            errorTxtField.setDisable(true);
        }
        
    }

    
}
