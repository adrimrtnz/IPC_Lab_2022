/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navapp.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.scene.control.Button;
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
        
        XYChart.Data sessionHits = null;
        XYChart.Data sessionFaults = null;
        
        for(Session session : histSessions) {
            if (sessionHits == null) { 
                sessionHits = new XYChart.Data(session.getLocalDate().toString(), session.getHits());
                sessionFaults = new XYChart.Data(session.getLocalDate().toString(), session.getFaults());
                hitsSession.getData().add(sessionHits);
                faultsSession.getData().add(sessionFaults);
            } else {
                if (sessionHits.getXValue().toString().equals(session.getLocalDate().toString())) {
                    sessionHits.setYValue((int) sessionHits.getYValue() + session.getHits());
                    sessionFaults.setYValue((int) sessionFaults.getYValue() + session.getFaults());
                } else {
                    sessionHits = new XYChart.Data(session.getLocalDate().toString(), session.getHits());
                    sessionFaults = new XYChart.Data(session.getLocalDate().toString(), session.getFaults());
                    hitsSession.getData().add(sessionHits);
                    faultsSession.getData().add(sessionFaults);
                }
            }
            
            histHits += session.getHits();
            histFaults += session.getFaults();
        }
        
        // ADD current day to the XYChart
        if (sessionHits == null) {
            sessionHits = new XYChart.Data("Hoy", this.hits);
            sessionFaults = new XYChart.Data("Hoy", this.faults);
            hitsSession.getData().add(sessionHits);
            faultsSession.getData().add(sessionFaults);
        } else if (LocalDate.now().toString().equals(sessionHits.getXValue().toString())) {
            sessionHits.setYValue((int) sessionHits.getYValue() + this.hits);
            sessionFaults.setYValue((int) sessionFaults.getYValue() + this.faults);
            sessionHits.setXValue("Hoy");
            sessionFaults.setXValue("Hoy");
        } else {
            sessionHits = new XYChart.Data("Hoy", this.hits);
            sessionFaults = new XYChart.Data("Hoy", this.faults);
            hitsSession.getData().add(sessionHits);
            faultsSession.getData().add(sessionFaults);
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
        
        LocalDate userSince;
        
        if (histSessions.size() > 0) {
            userSince = histSessions.get(0).getLocalDate();
        } else { 
            userSince = LocalDate.now();
        }
        
        userInfoString += "Usuario desde:    " + userSince.toString() + "\n";
        userInfoString += "Sesiones realizadas: " + histSessions.size() + "\n";
        
        int hitsFaultsSum = this.hits + this.faults;
            hitsFaultsSum = (hitsFaultsSum > 0) ? hitsFaultsSum : 1;
        int histHitsFaultsSum = histHits + histFaults;
            histHitsFaultsSum = (histHitsFaultsSum > 0) ? histHitsFaultsSum : 1;
        
        userInfoString += "Tasa de aciertos SESIÓN: "
                + String.format("%.2f",(double)this.hits / (hitsFaultsSum)* 100) + "%\n";
        userInfoString += "Tasa de aciertos TOTAL: " 
                + String.format("%.2f",(double)histHits / (histHitsFaultsSum) * 100) + "%\n";
        
        userInfo.textProperty().set(userInfoString);
    }
            
}
