package com.ziraatbank;

import com.ziraatbank.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class UpdateScreenController {

    public Button minimizeButton;
    public Button exitButton;
    public Button kaydetButton;
    public Label routerIP;
    public Button pingAtButton;
    public TextField atmAdiTextField;
    public TextField atmIDTextField;
    public TextField subeNumTextField;
    public TextField subnetIPTextField;
    public TextField adslTunnelTextField;
    public TextField dvrGatewayTextField;
    public TextField terminalNoTextField;
    public Label subnetBroadcastIP;
    public Label atmIP;
    public Label TGTunnelIP;
    public Label warningLabel;
    public Label warningLabel1;
    public Label warningLabel2;
    public ComboBox<String> subnetMaskComboBox;
    public Button iptalButton;
    private final String subnet252 = "255.255.255.252";
    private final String subnet248 = "255.255.255.248";
    DatabaseConnection connection = new DatabaseConnection();
    private boolean subnetIPcheck = true;
    private String search;
    private String key;



    @FXML
    void initialize(){
        subnetMaskComboBox.getItems().add(subnet252);
        subnetMaskComboBox.getItems().add(subnet248);

        subnetIPTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.length()==0){
            }else if(newValue.length()<=15&&newValue.length()>=7){
                if (com.ziraatbank.MainScreenController.ipValidation(subnetIPTextField.getText())){
                    calculateIP();
                    subnetIPcheck=true;
                    subnetIPTextField.setStyle("-fx-border-color: transparent");
                    warningLabel.setVisible(false);

                        if (connection.checkConflict("SubnetIP",subnetIPTextField.getText())){
                            subnetIPTextField.setStyle("-fx-border-color: yellow");
                            warningLabel.setText("Subnet IP Çakışıyor!");
                            kaydetButton.setDisable(true);
                            warningLabel.setVisible(true);
                        }else{
                            subnetIPTextField.setStyle("-fx-border-color: transparent");
                            warningLabel.setVisible(false);
                            kaydetButton.setDisable(false);
                        }



                }else{
                    warningLabel.setText("Subnet IP hatalı!");
                    warningLabel.setVisible(true);
                    kaydetButton.setDisable(true);
                    subnetIPTextField.setStyle("-fx-border-color: red");
                    subnetIPcheck=false;
                }
            }else{
                warningLabel.setText("Subnet IP uzunluğu hatalı!");
                warningLabel.setVisible(true);
                kaydetButton.setDisable(true);
                subnetIPTextField.setStyle("-fx-border-color: red");
                subnetIPcheck=false;
            }

        });

        adslTunnelTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.length()==0){
            }else if(newValue.length()<=15&&newValue.length()>=7){
                if (com.ziraatbank.MainScreenController.ipValidation(adslTunnelTextField.getText())){
                    warningLabel1.setVisible(false);
                    adslTunnelTextField.setStyle("-fx-border-color: transparent");
                    TGTunnelIP.setText(com.ziraatbank.MainScreenController.TGTunnelCalc(adslTunnelTextField.getText()));
                    if (connection.checkConflict("ADSLTunnel",adslTunnelTextField.getText())){
                        adslTunnelTextField.setStyle("-fx-border-color: yellow");
                        warningLabel.setText("ADSL Tunnel Çakışıyor!");
                        warningLabel.setVisible(true);
                        kaydetButton.setDisable(true);
                    }else{
                        adslTunnelTextField.setStyle("-fx-border-color: transparent");
                        warningLabel.setVisible(false);
                        kaydetButton.setDisable(false);
                    }
                }else{
                    warningLabel1.setText("ADSL Tunnel hatalı!");
                    warningLabel1.setVisible(true);
                    kaydetButton.setDisable(true);
                    adslTunnelTextField.setStyle("-fx-border-color: red");

                }
            }else{
                warningLabel1.setText("ADSL Tunnel uzunluğu hatalı!");
                warningLabel1.setVisible(true);
                kaydetButton.setDisable(true);
                adslTunnelTextField.setStyle("-fx-border-color: red");

            }

        });

        dvrGatewayTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.length()==0){
            }else if(newValue.length()<=15&&newValue.length()>=7){
                if (com.ziraatbank.MainScreenController.ipValidation(dvrGatewayTextField.getText())){
                    warningLabel2.setVisible(false);
                    dvrGatewayTextField.setStyle("-fx-border-color: transparent");
                    if (connection.checkConflict("DVRGateway",dvrGatewayTextField.getText())){
                        dvrGatewayTextField.setStyle("-fx-border-color: yellow");
                        warningLabel.setText("DVR Gateway Çakışıyor!");
                        warningLabel.setVisible(true);
                        kaydetButton.setDisable(true);
                    }else{
                        dvrGatewayTextField.setStyle("-fx-border-color: transparent");
                        warningLabel.setVisible(false);
                        kaydetButton.setDisable(false);
                    }
                }else{
                    warningLabel2.setText("DVR Gateway hatalı!");
                    warningLabel2.setVisible(true);
                    kaydetButton.setDisable(true);
                    dvrGatewayTextField.setStyle("-fx-border-color: red");

                }
            }else{
                warningLabel2.setText("DVR Gateway uzunluğu hatalı!");
                warningLabel2.setVisible(true);
                kaydetButton.setDisable(true);
                dvrGatewayTextField.setStyle("-fx-border-color: red");

            }

        });

        subnetMaskComboBox.valueProperty().addListener(observable -> {
            if (subnetIPcheck){
                calculateIP();
            }
        });

        atmAdiTextField.textProperty().addListener((observableValue, s, t1) -> {
            if (t1.length()<2){
                atmAdiTextField.setStyle("-fx-border-color: red");
                kaydetButton.setDisable(true);
            }else{
                atmAdiTextField.setStyle("-fx-border-color: transparent");
                kaydetButton.setDisable(false);
            }
        });

        atmIDTextField.textProperty().addListener((observableValue, s, t1) -> {
            if (!com.ziraatbank.MainScreenController.integerValid(t1,4,6)){
                atmIDTextField.setStyle("-fx-border-color: red");
                kaydetButton.setDisable(true);
            }else{
                atmIDTextField.setStyle("-fx-border-color: transparent");
                kaydetButton.setDisable(false);
            }

        });

        subeNumTextField.textProperty().addListener((observableValue, s, t1) -> {
            if (!com.ziraatbank.MainScreenController.integerValid(t1,1,5)){
                subeNumTextField.setStyle("-fx-border-color: red");
                kaydetButton.setDisable(true);
            }else{
                subeNumTextField.setStyle("-fx-border-color: transparent");
                kaydetButton.setDisable(false);
            }
        });

        terminalNoTextField.textProperty().addListener((observableValue, s, newValue) -> {
            String value = newValue;
            if (newValue.length()>2){
                if(value.charAt(0)=='Z')
                    value=value.substring(1);
            }

            if (MainScreenController.integerValid(value,4,6)){
                terminalNoTextField.setText("Z"+value);
                terminalNoTextField.setStyle("-fx-border-color: transparent");
                kaydetButton.setDisable(false);
            }else{
                terminalNoTextField.setStyle("-fx-border-color: red");
                kaydetButton.setDisable(true);
            }

        });




    }

    private void calculateIP(){
        if (subnetMaskComboBox.getValue().equals(subnet248)){
           subnetBroadcastIP.setText(com.ziraatbank.MainScreenController.parseIP(subnetIPTextField.getText(),8));
           routerIP.setText(com.ziraatbank.MainScreenController.parseIP(subnetIPTextField.getText(),1));
           atmIP.setText(com.ziraatbank.MainScreenController.parseIP(subnetIPTextField.getText(),2));
        }else{
            subnetBroadcastIP.setText(com.ziraatbank.MainScreenController.parseIP(subnetIPTextField.getText(),4));
            routerIP.setText(com.ziraatbank.MainScreenController.parseIP(subnetIPTextField.getText(),1));
            atmIP.setText(com.ziraatbank.MainScreenController.parseIP(subnetIPTextField.getText(),2));
        }
    }


    @FXML
    void exitButtonAction(){
        System.exit(0);
    }
    @FXML
    void minimizeButtonAction(){
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    public void setInfosToFields(String searchName, String key){
        ArrayList<String> list = connection.getInfos(searchName,key);
        atmAdiTextField.setText(list.get(0));
        atmIDTextField.setText(list.get(1));
        subeNumTextField.setText(list.get(2));
        subnetIPTextField.setText(list.get(3));
        routerIP.setText(list.get(4));
        subnetBroadcastIP.setText(list.get(5));
        atmIP.setText(list.get(6));
        adslTunnelTextField.setText(list.get(7));
        TGTunnelIP.setText(list.get(8));
        dvrGatewayTextField.setText(list.get(9));
        if (list.get(11).equals(subnet248)){
            subnetMaskComboBox.getSelectionModel().select(subnet248);
        }else{
            subnetMaskComboBox.getSelectionModel().select(subnet252);
        }
        terminalNoTextField.setText(list.get(12));

    }


    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    void iptalButtonAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/MainScreen.fxml"));
        Parent mainScreen = loader.load();
        Scene mainScreenScene = new Scene(mainScreen);

        com.ziraatbank.MainScreenController controller = loader.getController();
        Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        window.setScene(mainScreenScene);
        window.show();

        mainScreen.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        mainScreen.setOnMouseDragged(event -> {
            window.setX(event.getScreenX() - xOffset);
            window.setY(event.getScreenY() - yOffset);
        });
    }

    @FXML
    void kaydetButton (){
            connection.updateInfos(search,key, atmAdiTextField.getText(),atmIDTextField.getText()
            ,subeNumTextField.getText(), subnetIPTextField.getText(),routerIP.getText(),
                    subnetBroadcastIP.getText(),atmIP.getText(),adslTunnelTextField.getText(),
                    TGTunnelIP.getText(),dvrGatewayTextField.getText(),terminalNoTextField.getText());
            System.out.println(terminalNoTextField.getText());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Başarıyla kaydedildi!");
            alert.showAndWait();
            iptalButton.fire();

    }

    @FXML
    void pingAtButtonAction(){
        if (MainScreenController.sendPing(routerIP.getText())){
            routerIP.setStyle("-fx-background-color: lawngreen");
            warningLabel1.setText("Router IP'ye ulaşıldı");
            warningLabel1.setVisible(true);
        }else{
            routerIP.setStyle("-fx-background-color: red");
            warningLabel1.setText("Router IP'ye ulaşılamadı");
            warningLabel1.setVisible(true);
        }

        if (MainScreenController.sendPing(dvrGatewayTextField.getText())){
            dvrGatewayTextField.setStyle("-fx-background-color: lawngreen");
            warningLabel.setText("DVR Gateway'e ulaşıldı");
            warningLabel.setVisible(true);
        }else{
            dvrGatewayTextField.setStyle("-fx-background-color: red");
            warningLabel.setText("DVR Gateway'e ulaşılamadı");
            warningLabel.setVisible(true);
        }

    }


    void setSearch(String search, String key){
        if (search.equals("ATM ID")){
            this.search="AtmID";
        }
        if (search.equals("ATM Adı")){
            this.search="AtmAdı";
        }
        if (search.equals("Sube Numarası")){
            this.search="SubeNum";
        }
        if (search.equals("Subnet IP")){
            this.search="SubnetIP";
        }
        if (search.equals("ADSL Tunnel")){
            this.search="ADSLTunnel";
        }
        if (search.equals("Terminal No")){
            this.search="TerminalNo";
        }
        this.key=key;
    }



}
