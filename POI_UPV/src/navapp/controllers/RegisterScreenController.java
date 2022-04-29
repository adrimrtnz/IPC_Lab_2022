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
import java.io.File;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
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
    @FXML private Button changeAvatarBtn;
    @FXML private Button registerBtn;
    
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
    @FXML private ImageView birthDateCheck;
    
    private double xOffset;
    private double yOffset;
    
    private BooleanProperty validUserName;
    private BooleanProperty validPassword;
    private BooleanProperty validEmail;
    private BooleanProperty equalPasswords;
    private BooleanProperty validBirthDate;
    private BooleanProperty validFields;
    
    private Image avatar;
    private LocalDate birthdate;
    private LocalDate currentDate;
    
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
        birthDateCheck.setVisible(false);
        
        avatar = userAvatar.getImage();
        currentDate = LocalDate.now();
        
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
        validBirthDate = new SimpleBooleanProperty();
        
        validUserName.setValue(Boolean.FALSE);
        validPassword.setValue(Boolean.FALSE);
        validEmail.setValue(Boolean.FALSE);
        equalPasswords.setValue(Boolean.FALSE);
        validBirthDate.setValue(Boolean.FALSE);
              
        
        // Binding de los diferentes booleanos con los checks de los diferentes campos
        validUserName.bind(userNameCheck.visibleProperty());
        validEmail.bind(userEmailCheck.visibleProperty());
        validPassword.bind(userPassCheck.visibleProperty());
        equalPasswords.bind(userPassRepCheck.visibleProperty());
        validBirthDate.bind(birthDateCheck.visibleProperty());
        registerBtn.disableProperty().bind((validUserName.and(validEmail).and(validPassword).and(equalPasswords).and(validBirthDate)).not());
        
        
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
                    checkUserName(userName.focusedProperty().get());
                } else if(newValue) {
                    userNameToolTip.hide();
                }               
        });
        
        userName.textProperty().addListener((ob,oldValue,newValue) -> {
                checkUserName(userName.focusedProperty().get());              
        });
        
        userEmail.focusedProperty().addListener((ob,oldValue,newValue) -> {
                if(!newValue) {
                    checkUserPassword(userName.focusedProperty().get());
                } else if(newValue) {
                    userNameToolTip.hide();
                }               
        });
                
        userEmail.textProperty().addListener((ob,oldValue,newValue) -> {
                checkUserEmail(userEmail.focusedProperty().get());           
        });
        
        userPassword.focusedProperty().addListener((ob,oldValue,newValue) -> {
                if(!newValue) {
                    checkUserPassword(userPassword.focusedProperty().get());
                } else if(newValue) {
                    passToolTip.hide();
                }                
        });
        
        userPassword.textProperty().addListener((ob,oldValue,newValue) -> {
                checkUserPassword(userPassword.focusedProperty().get());                
        });
                
        userPasswordRep.focusedProperty().addListener((ob,oldValue,newValue) -> {
                if(!newValue) {
                    checkRepeatedPassword(userPasswordRep.focusedProperty().get());
                }                
        });
        
        userPasswordRep.textProperty().addListener((ob,oldValue,newValue) -> {
                checkRepeatedPassword(userPasswordRep.focusedProperty().get());              
        });
        
        userBirthDate.valueProperty().addListener((ob) -> {
                checkBirthDate();             
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
        BooleanBinding validFields = validUserName.and(validEmail.and(validPassword.and(equalPasswords.and(validBirthDate))));        
        
        if(!validFields.get()){
            propmtErrorMsg("Todos los campos deben ser correctos");
        }
        else {
            try {
                // Versión comentada es sin imagen
                baseDatos.registerUser(userName.getText(), userEmail.getText(), userPassword.getText(), avatar, birthdate);
                //baseDatos.registerUser(userName.getText(), userEmail.getText(), userPassword.getText(), birthdate);
                
                exitoTxtField.setVisible(true);
                errorTxtField.setVisible(false);
                //Añadir un mensaje de confirmacion!!!!
                Stage stage = (Stage) toolBar.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                propmtErrorMsg("Error en el registro del nuevo usuario.");
            }
        }

    }
    
    private void propmtErrorMsg(String errorMsg) {
        exitoTxtField.setVisible(false);
        errorTxtField.textProperty().setValue(errorMsg);
        errorTxtField.setVisible(true);
    }
    
    public void checkUserName(boolean focused) {
        userNameCheck.setVisible(false);
        
        Stage stage = (Stage) userName.getScene().getWindow();
        double offsetX = 500;
        double offsetY = 40;
        
        if (baseDatos.exitsNickName(userName.getText())) {
            propmtErrorMsg("Nombre de usuario en uso");
        }
        else if (!User.checkNickName(userName.getText()) && !focused) {
            propmtErrorMsg("Debes introducir un nombre de usuario válido.");
            userNameToolTip.show((Node)userName, stage.getX() + offsetX ,stage.getY() + offsetY);
        }
        else if (User.checkNickName(userName.getText())) {
            errorTxtField.setVisible(false);
            userNameCheck.setVisible(true);
        }
    }
    
    public void checkUserEmail(boolean focused) {
        userEmailCheck.setVisible(false);
        
        if (!User.checkEmail(userEmail.getText()) && !focused) {
            propmtErrorMsg("Debes introducir un email válido.");
        }
        else if (User.checkEmail(userEmail.getText())){
           errorTxtField.setVisible(false);
           userEmailCheck.setVisible(true);
        }
    }
    
    public void checkUserPassword(boolean focused) {
        userPassCheck.setVisible(false);
        
        Stage stage = (Stage) userPassword.getScene().getWindow();
        double offsetX = 500;
        double offsetY = 140;
        
        if (!User.checkPassword(userPassword.getText()) && !focused) {
            propmtErrorMsg("Debes introducir una contraseña válida.");
            passToolTip.show((Node)userPassword, stage.getX() + offsetX ,stage.getY() + offsetY);
        }
        else if (User.checkPassword(userPassword.getText())) {
           errorTxtField.setVisible(false);
           userPassCheck.setVisible(true);
        }
    }
    
    public void checkRepeatedPassword(boolean focused) {
        userPassRepCheck.setVisible(false);
        if (userPasswordRep.getText().isEmpty() || !validPassword.get()) { return; }
        
        if (!userPasswordRep.getText().equals(userPassword.getText()) && !focused) {
            propmtErrorMsg("Las contraseñas deben coincidir.");
        }
        else if (userPasswordRep.getText().equals(userPassword.getText())) {
           errorTxtField.setVisible(false);
           userPassRepCheck.setVisible(true);
        }
    }
    
    public void checkBirthDate() {
        
        try {
            LocalDate userBirthDay = userBirthDate.getValue();

            LocalDate timeLived = currentDate.minusYears(userBirthDay.getYear());
                      timeLived = timeLived.minusMonths(userBirthDay.getMonthValue() - 1);
                      timeLived = timeLived.minusDays(userBirthDay.getDayOfMonth() - 1);

            int yearsOld = timeLived.getYear();

            if (yearsOld < 16) {
                propmtErrorMsg("Debes ser mayor de 16 años.");
            }
            else {
                birthdate = userBirthDay;
                errorTxtField.setVisible(false);
                birthDateCheck.setVisible(true);
            }
        }
        catch (Exception e) {
            System.out.println("Ha ocurrido una excepción: " + e.getMessage());
            propmtErrorMsg("Seleccione una fecha de nacimiento.");
        }
    }
    
    @FXML
    private void selectAvatarImage(MouseEvent event) throws Exception {
        FXMLLoader avatarWindow = new FXMLLoader(getClass().getResource("/navapp/views/AvatarSelectionView.fxml"));
        Parent root = avatarWindow.load();
        
        AvatarSelectionController controlador = avatarWindow.getController();
        
        Stage stage = new Stage();
        stage.setTitle("Selección de avatar");
        stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        
        try {
            Image selectedImage = controlador.getImage();
            userAvatar.imageProperty().set(selectedImage);
            avatar = userAvatar.imageProperty().get();
        }
        catch(Exception e) {
            System.out.println("No se ha seleccionado ninguna imagen");
        }
    }
}
