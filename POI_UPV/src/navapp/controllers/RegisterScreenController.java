/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp.controllers;

import java.awt.Window;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
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
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Navegacion;
import model.User;

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
    
    private Image avatar;
    private LocalDate birthdate;
    
    private Navegacion baseDatos;
   
    private Tooltip passToolTip;
    private Tooltip userNameToolTip;
    
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
        
        avatar = userAvatar.getImage();
        birthdate = LocalDate.now();
        
        try {
            baseDatos = Navegacion.getSingletonNavegacion();
            System.out.println("Base de datos cargada correctamente.");
            
            if (baseDatos == null) {
                propmtErrorMsg("Error al cargar la Base de Datos");
            }
            
        } catch(Exception e) {
            propmtErrorMsg("Ha ocurrido una excepción al cargar la Base de Datos");
        }
        
        validUserName = new SimpleBooleanProperty();
        validEmail = new SimpleBooleanProperty();
        validPassword = new SimpleBooleanProperty();   
        equalPasswords = new SimpleBooleanProperty();
        
        validUserName.setValue(Boolean.FALSE);
        validPassword.setValue(Boolean.FALSE);
        validEmail.setValue(Boolean.FALSE);
        equalPasswords.setValue(Boolean.FALSE);
              
        
        // Binding de los diferentes booleanos con los checks de los diferentes campos
        validUserName.bind(userNameCheck.visibleProperty());
        validEmail.bind(userEmailCheck.visibleProperty());
        validPassword.bind(userPassCheck.visibleProperty());
        equalPasswords.bind(userPassRepCheck.visibleProperty());
        
        
        // ToolTips para los diferentes campos
        userNameToolTip = new Tooltip();
        userNameToolTip.setText("El nombre de usuario debe tener entre 6 y 15\n"
                + "caracteres o dígitos, y sin espacios\n");
        userName.setTooltip(userNameToolTip);
        
        passToolTip = new Tooltip();
        passToolTip.setText("Tu contraseña debe tener entre 8 y 20 caracteres,\n"
                + "al menos una mayúscula y minúscula, así como algún dígito\n"
                + "y algún caracter especial(!@#$%&*()-+=)");
        userPassword.setTooltip(passToolTip);
        
        
        
        // Se comprueba si los datos introducidos son válidos cada vez que se pierde el foco del campo
        userName.focusedProperty().addListener((ob,oldValue,newValue) -> {
                if(!newValue) {
                    checkUserName();
                }                
        });
        
        userName.focusedProperty().addListener((ob,oldValue,newValue) -> {
                if(newValue) {
                    userNameToolTip.hide();
                }                
        });
        
        userEmail.focusedProperty().addListener((ob,oldValue,newValue) -> {
                if(!newValue) {
                    checkUserEmail();
                }                
        });
        
        userPassword.focusedProperty().addListener((ob,oldValue,newValue) -> {
                if(!newValue) {
                    checkUserPassword();
                }                
        });
        
        userPassword.focusedProperty().addListener((ob,oldValue,newValue) -> {
                if(newValue) {
                    passToolTip.hide();
                }                
        });
        
        userPasswordRep.focusedProperty().addListener((ob,oldValue,newValue) -> {
                if(!newValue) {
                    checkRepeatedPassword();
                }                
        });
        
        userBirthDate.focusedProperty().addListener((ob,oldValue,newValue) -> {
                if(!newValue) {
                    birthdate = userBirthDate.getValue();
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
            propmtErrorMsg("Todos los campos deben ser correctos");
        }
        else {
            try {
                // Versión comentada es con imagen, por ahora da == null
                baseDatos.registerUser(userName.getText(), userEmail.getText(), userPassword.getText(), avatar, birthdate);
                //baseDatos.registerUser(userName.getText(), userEmail.getText(), userPassword.getText(), birthdate);
                
                exitoTxtField.setVisible(true);
                errorTxtField.setVisible(false);
                deleteTextFieldsContent();
                resetFieldChecks();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                propmtErrorMsg("Error en el registro del nuevo usuario.");
            }
        }

        System.out.println("Botón de ACEPTAR pulsado.");
    }
    
    private void propmtErrorMsg(String errorMsg) {
        exitoTxtField.setVisible(false);
        errorTxtField.textProperty().setValue(errorMsg);
        errorTxtField.setVisible(true);
    }
    
    public void checkUserName() {
        userNameCheck.setVisible(false);
        
        Stage stage = (Stage) userName.getScene().getWindow();
        double offsetX = 500;
        double offsetY = 40;
        
        if (baseDatos.exitsNickName(userName.getText())) {
            propmtErrorMsg("Nombre de usuario en uso");
        }
        else if (!User.checkNickName(userName.getText())) {
            propmtErrorMsg("Debes introducir un nombre de usuario válido.");
            userNameToolTip.show((Node)userName, stage.getX() + offsetX ,stage.getY() + offsetY);
        }
        else {
           errorTxtField.setVisible(false);
           userNameCheck.setVisible(true);
        }
    }
    
    public void checkUserEmail() {
        userEmailCheck.setVisible(false);
        
        if (!User.checkEmail(userEmail.getText())) {
            propmtErrorMsg("Debes introducir un email válido.");
        }
        else {
           errorTxtField.setVisible(false);
           userEmailCheck.setVisible(true);
        }
    }
    
    public void checkUserPassword() {
        userPassCheck.setVisible(false);
        
        Stage stage = (Stage) userPassword.getScene().getWindow();
        double offsetX = 500;
        double offsetY = 140;
        
        if (!User.checkPassword(userPassword.getText())) {
            propmtErrorMsg("Debes introducir una contraseña válida.");
            passToolTip.show((Node)userPassword, stage.getX() + offsetX ,stage.getY() + offsetY);
        }
        else {
           errorTxtField.setVisible(false);
           userPassCheck.setVisible(true);
        }
    }
    
    public void checkRepeatedPassword() {
        userPassRepCheck.setVisible(false);
        if (userPasswordRep.getText().isEmpty() || !validPassword.get()) { return; }
        
        if (!userPasswordRep.getText().equals(userPassword.getText())) {
            propmtErrorMsg("Las contraseñas deben coincidir.");
        }
        else {
           errorTxtField.setVisible(false);
           userPassRepCheck.setVisible(true);
        }
    }
    
    private void deleteTextFieldsContent() {
        userName.setText("");
        userEmail.setText("");
        userPassword.setText("");
        userPasswordRep.setText("");
    }
    
    private void resetFieldChecks() {
        errorTxtField.setVisible(false);
        userNameCheck.setVisible(false);
        userEmailCheck.setVisible(false);
        userPassCheck.setVisible(false);
        userPassRepCheck.setVisible(false);
    }
    
}
