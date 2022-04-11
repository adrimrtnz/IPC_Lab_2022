/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Navegacion;
import navapp.models.Utils;

/**
 * FXML Controller class
 *
 * @author Adrián Martínez
 */
public class RegisterScreenController implements Initializable {

    @FXML private ToolBar toolBar;
    @FXML private Button maximizeButton;
    @FXML private Button minimizeBtn;
    
    @FXML private Text exitoTxtField;
    @FXML private Text errorTxtField;
    
    @FXML private ImageView userAvatar;
    @FXML private TextField userName;
    @FXML private TextField userEmail;
    @FXML private PasswordField userPassword;
    @FXML private PasswordField userPasswordRep;
    @FXML private DatePicker userBirthDate;
    
    @FXML private ImageView userNameCheck;
    @FXML private ImageView userEmailCheck;
    @FXML private ImageView userPassCheck;
    @FXML private ImageView userPassRepCheck;
    
    private double xOffset;
    private double yOffset;
    
    private BooleanProperty validUserName;
    private BooleanProperty validPassword;
    private BooleanProperty validEmail;
    private BooleanProperty equalPasswords;
    private BooleanProperty validFields;
    private String mensaje;
    
    private Navegacion baseDatos;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        exitoTxtField.setVisible(false);
        errorTxtField.setVisible(false);
        userNameCheck.setVisible(false);
        userEmailCheck.setVisible(false);
        userPassCheck.setVisible(false);
        userPassRepCheck.setVisible(false);
        
        
        try {
            baseDatos = Navegacion.getSingletonNavegacion();
            System.out.println("Base de datos cargada correctamente.");
            
            if (baseDatos == null) {
                mensaje = "Error al cargar la Base de Datos.";
                errorTxtField.textProperty().setValue(mensaje);
                errorTxtField.setVisible(false);
            }
            
        } catch(Exception e) {
            mensaje = "Ha ocurrido una excepción al cargar la Base de Datos.";
            errorTxtField.textProperty().setValue(mensaje);
            errorTxtField.setVisible(false);
        }
        
        validUserName = new SimpleBooleanProperty();
        validEmail = new SimpleBooleanProperty();
        validPassword = new SimpleBooleanProperty();   
        equalPasswords = new SimpleBooleanProperty();
        
        validUserName.setValue(Boolean.FALSE);
        validPassword.setValue(Boolean.FALSE);
        validEmail.setValue(Boolean.FALSE);
        equalPasswords.setValue(Boolean.FALSE);
               
         
        userName.focusedProperty().addListener((ob,oldValue,newValue) -> {
                if(!newValue) {
                    checkUserName();
                }                
        });
        
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
                
        minimizeBtn.setOnAction((ActionEvent event) -> {
            ((Stage) minimizeBtn.getScene().getWindow()).setIconified(true);
        });
    }    

    @FXML
    private void closeScene(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void registrarUsuario(ActionEvent event) {
        BooleanBinding validFields = validUserName.and(validEmail.and(validPassword.and(equalPasswords)));        
        
        if(!validFields.get()){
            errorTxtField.setVisible(true);
        }
        else {
            exitoTxtField.setVisible(true);
            errorTxtField.setVisible(false);
        }

        System.out.println("Botón de ACEPTAR pulsado.");
    }
    
    public void checkUserName() {
        if (baseDatos.exitsNickName(userName.getText())) {
            mensaje = "Nombre de usuario en uso.";
            errorTxtField.textProperty().setValue(mensaje);
            errorTxtField.setVisible(true);
            userNameCheck.setVisible(false);
        }
        else if (userName.getText().isEmpty()) {
            mensaje = "Debes introducir un nombre de usuario válido.";
            errorTxtField.textProperty().setValue(mensaje);
            errorTxtField.setVisible(true);
        }
        else {
           errorTxtField.setVisible(false); 
           userNameCheck.setVisible(true);
        }
    }
    
}
