/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.*;


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
    @FXML private Button registerBtn;
    
    @FXML private TextField userName;
    @FXML private PasswordField userPassword;
    
    //properties to control valid fieds values. 
    private BooleanProperty validPassword;
    private BooleanProperty validUserName; 
    
    private double xOffset;
    private double yOffset;
    private String errorTxtMsg;
    
    private Navegacion baseDatos;
    private User loggedUser;
    
    private int oldUsersLogged;
    private boolean newUser;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Cambiamos el contenido del campo de mensaje de error y lo ocultamos
        errorTxtMsg = "";
        errorTxtField.textProperty().setValue(errorTxtMsg);
<<<<<<< Updated upstream
        errorTxtField.setVisible(false);
=======
        errorTxtField.setDisable(true);
        errorTxtField.setFill(Color.RED);
        
        // Obtener size de la base de datos ANTES de registrar a un nuevo usuario
            //oldUsersLogged = baseDatos.;
        newUser = false;
>>>>>>> Stashed changes
        
        //Bindeamos el boton a los fields
        loginBtn.disableProperty().bind((userName.textProperty().isNotEmpty().and(userPassword.textProperty().isNotEmpty())).not());
        
        // TODO : Da null pointer exception al cargar la BD, no se crea si no existe.
        // CARGA la Base de Datos.
        try {
            baseDatos = Navegacion.getSingletonNavegacion();
            System.out.println("Base de datos cargada correctamente.");
            loggedUser = null;
            
            if (baseDatos == null) {
                errorTxtMsg = "Error al cargar la Base de Datos.";
                errorTxtField.textProperty().setValue(errorTxtMsg);
                errorTxtField.setVisible(true);
            }
            
        }
        catch(Exception e) {
            errorTxtMsg = "Ha ocurrido una excepción al cargar la Base de Datos.";
            errorTxtField.textProperty().setValue(errorTxtMsg);
            errorTxtField.setVisible(false);
        }
        
        validPassword = new SimpleBooleanProperty();
        validUserName = new SimpleBooleanProperty();
        
        validPassword.setValue(Boolean.FALSE);
        validUserName.setValue(Boolean.FALSE);
             
        
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
        
        userName.textProperty().addListener((ob,oldValue,newValue) -> {
            if(!newValue.equals(oldValue)) {   
                errorTxtField.setVisible(false);
            }
        });
        
        userPassword.textProperty().addListener((ob,oldValue,newValue) -> {
            if(!newValue.equals(oldValue)) {    
                errorTxtField.setVisible(false); 
            }  
        });
    }    

    @FXML
    private void iniciarSesion(ActionEvent event) {
        //validPassword.setValue(checkPassword());
        
        
        // TODO cambiar los booleanos por booleanos con listeners sobre el estado de los TextFields
        if (userName.textProperty().isEmpty().get() || userPassword.textProperty().isEmpty().get()) {
            errorTxtMsg = "Error: Se deben rellenar ambos campos.";
            errorTxtField.textProperty().setValue(errorTxtMsg);
            errorTxtField.setVisible(true);
        }
        else {
            
            validUserName.setValue(baseDatos.exitsNickName(userName.getText()));
            loggedUser = baseDatos.loginUser(userName.getText(), userPassword.getText());
            
            if (!validUserName.getValue() || loggedUser == null) {
                errorTxtMsg = "Credenciales Incorrectas.";
                errorTxtField.textProperty().setValue(errorTxtMsg);
                errorTxtField.setVisible(true);
            }
            else {
                //errorTxtMsg = "Usuario encontrado pero no hay más ventanas. Vuelve más tarde.";
                //errorTxtField.textProperty().setValue(errorTxtMsg);
                errorTxtField.setVisible(true);
                try {
                    loadExercisesScreen(loggedUser);
                } catch(Exception e) {
                    System.out.println("No se ha podido cargar la pantalla de ejercicios.");
                    errorTxtMsg = "No se ha podido cargar la pantalla de ejercicios.";
                    errorTxtField.textProperty().setValue(errorTxtMsg);
                }
            }
        }
        
    }
    
    private boolean checkPassword() {
        return User.checkPassword(userPassword.textProperty().getValueSafe());
    }

    @FXML
    private void loadRegisterForm(ActionEvent event) throws Exception {
        FXMLLoader cargadorRegistro = new FXMLLoader(getClass().getResource("/navapp/views/RegisterScreenView.fxml"));
        Parent root = cargadorRegistro.load();
        
        // Acceso al controlador de la Vista de Registro
        // RegisterScreenController controlador = cargadorRegistro.getController();
        
        Stage stage = new Stage();
        stage.setTitle("Registro Nuevo Usuario");
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        
    }

    
    
    private void loadExercisesScreen(User loggedUser) throws Exception {
        FXMLLoader cargadorEjercicios = new FXMLLoader(getClass().getResource("/navapp/views/ExercisesScreenView.fxml"));
        Parent root = cargadorEjercicios.load();
        
        // Acceso al controlador de la Vista de Registro
        ExercisesScreenController controlador = cargadorEjercicios.getController();
        controlador.initializeUser(loggedUser);
        
        Stage stage = new Stage();
        stage.setTitle("Merak Trainer - IPC 2021-2022");
        //stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        closeLoginScreen();
        stage.getIcons().add(new Image("resources/icons/icon.png"));
        stage.show();
    }
    
    private void closeLoginScreen() {
        Stage stage = (Stage) toolBar.getScene().getWindow();
        stage.close();
    }
    
    public void newUser(){
        errorTxtField.setFill(Color.BLACK);
        errorTxtField.setText("Nuevo usuario creado correctamente");
        errorTxtField.setVisible(true);
        
    }
}
