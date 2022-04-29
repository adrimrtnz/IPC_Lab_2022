/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Session;
import model.User;

/**
 * FXML Controller class
 *
 * @author Adrián Martínez
 */
public class StatsScreenController implements Initializable {

    @FXML private ToolBar toolBar;
    @FXML private Button closeBtn;
    @FXML private Button minimizeBtn;
    @FXML private Button returnBtn;
    @FXML private PieChart actualSessionHitsPie;
    @FXML private PieChart historicalHitsPie;
    @FXML private ImageView userAvatar;
    @FXML private Text userNameTag;
    @FXML private Text userInfo;
    @FXML private LineChart<?, ?> lineChart;
    @FXML private NumberAxis yAxis;
    @FXML private CategoryAxis xAxis;
    
    private User loggedUser;
    private int hits, faults;
    private int histHits, histFaults;
    private double xOffset;
    private double yOffset;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        closeBtn.setOnAction((ActionEvent event) -> {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        });
        
        returnBtn.setOnAction((ActionEvent event) -> {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
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
    }    
    
    public void initializeStats(User user, int hits, int fails) {
        loggedUser = user;
        userAvatar.imageProperty().set(loggedUser.getAvatar());
        userNameTag.textProperty().set(loggedUser.getNickName());
        this.hits = hits;
        this.faults = fails;
    }
    
    public void initializeCharts() {
        histHits = this.hits;
        histFaults = this.faults;
               
        ObservableList<PieChart.Data> actualData = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> histHitsFaults = FXCollections.observableArrayList();

        // Recuperación de los datos históricos
        List<Session> histSessions = loggedUser.getSessions();
       
        XYChart.Series hitsSession = new XYChart.Series();
        XYChart.Series faultsSession = new XYChart.Series();
        
        hitsSession.setName("Aciertos");
        faultsSession.setName("Fallos");
        
        for(Session session : histSessions) {
            hitsSession.getData().add(new XYChart.Data(session.getLocalDate().toString(), session.getHits()));
            faultsSession.getData().add(new XYChart.Data(session.getLocalDate().toString(), session.getFaults()));
            
            histHits += session.getHits();
            histFaults += session.getFaults();
        }
        
        lineChart.getData().add(hitsSession);
        lineChart.getData().add(faultsSession);
        
        String actualDataHitsLabel = "Aciertos: " + this.hits;
        String actualDataFaultsLabel = "Fallos: " + this.faults;
        
        actualData.add(new PieChart.Data(actualDataHitsLabel, this.hits));
        actualData.add(new PieChart.Data(actualDataFaultsLabel, this.faults));
        actualSessionHitsPie.setData(actualData);
        
        //-----------------------------------------------------------//
        String histDataHitsLabel = "Aciertos: " + histHits;
        String histDataFaultsLabel = "Fallos: " + histFaults;
        
        histHitsFaults.add(new PieChart.Data(histDataHitsLabel, histHits));
        histHitsFaults.add(new PieChart.Data(histDataFaultsLabel, histFaults));
        historicalHitsPie.setData(histHitsFaults);
        
    }
    
    public void initializeUserInfo() {
        String userInfoString = "";
        List<Session> histSessions = loggedUser.getSessions();
        
        userInfoString += "Usuario desde:    " + histSessions.get(0).getLocalDate().toString() + "\n";
        userInfoString += "Sesiones realizadas: " + histSessions.size() + "\n";
        
        userInfoString += "Tasa de aciertos SESIÓN: "
                + String.format("%.2f",(double)this.hits / (this.hits + this.faults)* 100) + "%\n";
        userInfoString += "Tasa de aciertos TOTAL: " 
                + String.format("%.2f",(double)histHits / (histHits + histFaults) * 100) + "%\n";
        
        userInfo.textProperty().set(userInfoString);
    }
            
}
