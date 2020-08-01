package com.ziraatbank;

import com.ziraatbank.util.AutoCompleteComboBoxListener;
import com.ziraatbank.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainScreenController {
    public Label subnetBroadcast;
    public Label DVRGateway;
    public Label ADSLTunnel;
    public Label TGTunnel;
    public Label DVRMask;
    public Label subnetIPLabel;
    public Label subnetBroadcastText;
    public Label routerIPText;
    public Label router;
    public Label ATM;
    public Label warningLabel;
    public Label warningLabel1;
    public Button kaydetButton;
    public Button hesaplaButton;
    public Button minimizeButton;
    public Button exitButton;
    public Button araButton;
    public Button pingAtButton;
    public TextField ATMIDTextField;
    public TextField atmNameTextField;
    public TextField subeNumTextField;
    public TextField terminalNoTextField;
    public ComboBox<String> subnetMaskComboBox;
    public ComboBox<String> iller;
    public ComboBox<String> araSecComboBox;
    public ComboBox<String> araSonucComboBox;
    public ComboBox<String> sendPingComboBox;
    public ImageView ZBimageView;

    private final String subnet252 = "255.255.255.252";
    private final String subnet248 = "255.255.255.248";
    private String DVRGatewayIP = "10.240.1.1";
    private String DVRMaskIP = "255.255.255.248";
    private String ADSLTunnelIP = "10.86.0.1";
    private String TGTunnelIP = "10.86.0.1";
    private String routerIP = "";
    private String subnetBroadcastIP = "";
    private String ATMIP = "";
    private String subnetIP = "";
    private String selectedCity = "";
    private String selectedSubmask = "";
    private String search="";
    private String key="";

    public void setATMName(boolean ATMName) {
        this.ATMName = ATMName;
    }

    public void setATMID(boolean ATMID) {
        this.ATMID = ATMID;
    }

    public void setSubeNum(boolean subeNum) {
        this.subeNum = subeNum;
    }

    public void setTerminalNo(boolean terminalNo) {
        this.terminalNo = terminalNo;
    }

    boolean ATMName=false;
    boolean ATMID=false;
    boolean subeNum=false;
    boolean terminalNo=false;

    DatabaseConnection connection= new DatabaseConnection();

    @FXML
    void initialize(){
        File file = new File("src/main/resources/images/zb_logo.png");
        Image image = new Image(file.toURI().toString());
        ZBimageView.setImage(image);
        ZBimageView.setPreserveRatio(true);

       // getExcel();

        araSecComboBox.getItems().add("ATM ID");
        araSecComboBox.getItems().add("ATM Adı");
        araSecComboBox.getItems().add("Sube Numarası");
        araSecComboBox.getItems().add("Subnet IP");
        araSecComboBox.getItems().add("ADSL Tunnel");
        araSecComboBox.getItems().add("Terminal No");

        sendPingComboBox.getItems().addAll("Router IP","DVR Gateway","ATM IP","ADSL Tunnel","3G Tunnel","Subnet IP","Subnet Broadcast");
        for (String s : connection.getSehir()) {
            iller.getItems().add(s);
        }

        new AutoCompleteComboBoxListener<>(iller);
        new AutoCompleteComboBoxListener<>(araSonucComboBox);
        subnetMaskComboBox.getItems().add(subnet248);
        subnetMaskComboBox.getItems().add(subnet252);


        araSecComboBox.valueProperty().addListener((observableValue, s, t1) -> {
            ArrayList<String> result= new ArrayList();

            if (t1.equals("ATM ID")){
                result=connection.getAra("AtmID");
            }
            if (t1.equals("ATM Adı")){
                result=connection.getAra("AtmAdı");
            }
            if (t1.equals("Sube Numarası")){
                result=connection.getAra("SubeNum");
            }
            if (t1.equals("Subnet IP")){
                result=connection.getAra("SubnetIP");
            }
            if (t1.equals("Subnet Broadcast")){
                result=connection.getAra("SubnetBroadcast");
            }
            if (t1.equals("ADSL Tunnel")){
                result=connection.getAra("ADSLTunnel");
            }
            if (t1.equals("Terminal No")){
                result=connection.getAra("TerminalNo");
            }
            araSonucComboBox.getItems().clear();
            for(String s1 : result){
                araSonucComboBox.getItems().add(s1);
            }

        } );

        terminalNoTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String value = newValue;
            if (newValue.length()>2){
                if(value.charAt(0)=='Z')
                value=value.substring(1);
            }

            if (integerValid(value,4,6)){
                terminalNoTextField.setText("Z"+value);
                terminalNoTextField.setStyle("-fx-border-color: lawngreen");
                setTerminalNo(true);
            }else{
                terminalNoTextField.setStyle("-fx-border-color: red");
                setTerminalNo(false);
            }
            disableKaydetButton();
        });

        atmNameTextField.textProperty().addListener((observableValue, s, t1) -> {

            if(t1.length()>=2){
                warningLabel.setVisible(false);
                atmNameTextField.setStyle("-fx-border-color: lawngreen");
                setATMName(true);
            }else{
                atmNameTextField.setStyle("-fx-border-color: red");
                warningLabel.setText("ATM adı çok kısa");
                warningLabel.setVisible(true);
                setATMName(false);
            }
            disableKaydetButton();
        });

        ATMIDTextField.textProperty().addListener((observableValue, s, t1) -> {

            if (integerValid(t1,4,6)){
                ATMIDTextField.setStyle("-fx-border-color: lawngreen");
                setATMID(true);
            }else{
                ATMIDTextField.setStyle("-fx-border-color: red");
                setATMID(false);
            }
            disableKaydetButton();
        });

        subeNumTextField.textProperty().addListener((observableValue, s, t1) -> {

            if (integerValid(t1,1,5)){
                subeNumTextField.setStyle("-fx-border-color: lawngreen");
                setSubeNum(true);
            }else{
                subeNumTextField.setStyle("-fx-border-color: red");
                setSubeNum(false);
            }
            disableKaydetButton();
        });

        subnetMaskComboBox.valueProperty().addListener(observable -> {
            IPHesapla();
            sendPingComboBox.setDisable(false);
        } );

        araSecComboBox.valueProperty().addListener(observable -> {
            araSonucComboBox.setDisable(false);
        });
        araSonucComboBox.valueProperty().addListener(observable -> {
            araButton.setDisable(false);
        });

        sendPingComboBox.valueProperty().addListener(observable -> {
            pingAtButton.setDisable(false);
        });

        iller.valueProperty().addListener(observable -> {
            subnetMaskComboBox.setDisable(false);

            if (subnetMaskComboBox.getSelectionModel().getSelectedItem()!=null){
                IPHesapla();
            }
        });



    }

    void disableKaydetButton(){
        if (ATMID&&ATMName&&subeNum&&terminalNo){
            kaydetButton.setDisable(false);
            warningLabel.setText("Kaydederken ping kontrolu yapılacak lütfen bekleyin!");
            warningLabel.setVisible(true);
        }else{
            kaydetButton.setDisable(true);
            warningLabel.setVisible(false);
        }
    }
    @FXML
    void getExcel() {
        try {
            FileInputStream file = new FileInputStream(new File("C:\\Users\\Seda\\Desktop\\ZiraatBankApp\\resources\\İP.XLSX"));
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (int rowIndex = 0; rowIndex<=sheet.getLastRowNum();rowIndex++){
                Row row = sheet.getRow(rowIndex);
                if (row != null){
                    Cell cell = row.getCell(0);
                    Cell cell1 = row.getCell(1);
                    if (cell != null){
                        connection.insertIPBlock(cell.getStringCellValue(),cell1.getStringCellValue());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
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
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    void araButtonAction(ActionEvent actionEvent) throws IOException {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("view/UpdateScreen.fxml"));
            Parent updateScreen = loader.load();
            Scene updateScreenScene = new Scene(updateScreen);
            UpdateScreenController controller = loader.getController();


            controller.setSearch(araSecComboBox.getSelectionModel().getSelectedItem(), araSonucComboBox.getSelectionModel().getSelectedItem());
            setSearch(araSecComboBox.getSelectionModel().getSelectedItem(), araSonucComboBox.getSelectionModel().getSelectedItem());
            controller.setInfosToFields(search, key);

            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(updateScreenScene);
            window.show();

            updateScreen.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            updateScreen.setOnMouseDragged(event -> {
                window.setX(event.getScreenX() - xOffset);
                window.setY(event.getScreenY() - yOffset);
            });





    }


    void IPHesapla(){
        selectedCity=iller.getValue();
        selectedSubmask=subnetMaskComboBox.getValue();
        searchEmptyIP();
        searchEmptyADSLTunnel();
        searchEmptyDVRGateway();
        TGTunnelCalc(ADSLTunnelIP);
        IPCalc();
        setInfos();
    }

    @FXML
    void pingButtonAction(){
        try {
            if(sendPingComboBox.getValue().equals("Router IP")){
                sendPing(router.getText(),router);
            }
            if(sendPingComboBox.getValue().equals("DVR Gateway")){
                sendPing(DVRGateway.getText(),DVRGateway);
            }
            if(sendPingComboBox.getValue().equals("ATM IP")){
                sendPing(ATM.getText(),ATM);
            }
            if(sendPingComboBox.getValue().equals("ADSL Tunnel")){
                sendPing(ADSLTunnel.getText(),ADSLTunnel);
            }
            if(sendPingComboBox.getValue().equals("3G Tunnel")){
                sendPing(TGTunnel.getText(),TGTunnel);
            }
            if(sendPingComboBox.getValue().equals("Subnet IP")){
                sendPing(subnetIPLabel.getText(),subnetIPLabel);
            }
            if(sendPingComboBox.getValue().equals("Subnet Broadcast")){
                sendPing(subnetBroadcast.getText(),subnetBroadcast);
            }
            warningLabel1.setVisible(false);
        }catch (NullPointerException e){
            warningLabel1.setText("Seçim yapınız!");
            warningLabel.setVisible(true);
        }

    }

    void sendPing(String IP, Label label){
        InetAddress routerPing = null;
        try {
            routerPing = InetAddress.getByName(IP);
            if (routerPing.isReachable(3000)){
                label.setStyle("-fx-background-color: lawngreen;");
            }else{
                label.setStyle("-fx-background-color: red;");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    boolean sendPing(String IP){
        try {
            InetAddress routerPing = InetAddress.getByName(IP);
            if (routerPing.isReachable(3000)){
                return true;
            }else{
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    void kaydetButtonAction() {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setHeaderText(null);
            if (!(sendPing(routerIP)&&sendPing(DVRGatewayIP))) {
                alert.setTitle("");
                alert.setHeaderText("LÜTFEN BİLGİLERİ KONTROL EDİNİZ!");
                alert.setContentText(
                        "İl:                             "+selectedCity+
                                "\nATM Adı:                 "+atmNameTextField.getText()+
                                "\nATM ID:                  "+ATMIDTextField.getText()+
                                "\nŞube Numarası:       "+subeNumTextField.getText()+
                                "\nSubnet Mask:          "+selectedSubmask+
                                "\nSubnet IP :             "+subnetIP+
                                "\nSubnet Broadcast:   "+subnetBroadcastIP+
                                "\nRouter IP:                "+ routerIP+
                                "\nATM IP:                   "+ATMIP+
                                "\nADSL Tunnel:           "+ADSLTunnelIP+
                                "\nDVR Gateway:         "+DVRGatewayIP+
                                "\n3G Tunnel:              "+ TGTunnelIP+
                                "\nDVR Mask:              "+ DVRMaskIP);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get()== ButtonType.OK){
                    boolean checkConn=connection.setInfos(
                            selectedCity,
                            atmNameTextField.getText(),
                            subeNumTextField.getText(),
                            ATMIDTextField.getText(),
                            selectedSubmask,
                            subnetIP,
                            subnetBroadcastIP,
                            routerIP,
                            ATMIP,
                            ADSLTunnelIP,
                            TGTunnelIP,
                            DVRMaskIP,
                            DVRGatewayIP,
                            terminalNoTextField.getText());
                    if (checkConn){
                        alert1.setTitle("Başarılı!");
                        alert1.setContentText("Başarılı bir şekilde kaydedildi!");
                        alert1.showAndWait();
                        reset();;
                        kaydetButton.setDisable(true);
                    }else{
                        alert1.setTitle("Başarısız!");
                        alert1.setContentText("Bilgileriniz kaydedilemedi!");
                        alert1.showAndWait();
                    }
                }
            }else{
                alert1.setContentText("Ping ulaşıldı, Kayıt Başarısız!");
                alert1.showAndWait();
                resetDoldur();
            }


    }

    void IPCalc(){
        if(selectedSubmask.equals(subnet248)){
            subnetBroadcastIP=parseIP(subnetIP,8);
            routerIP=parseIP(subnetIP,1);
            ATMIP=parseIP(subnetIP,2);
        }else{
            routerIP=parseIP(subnetIP,1);
            subnetBroadcastIP=parseIP(subnetIP,4);
            ATMIP=parseIP(subnetIP,2);
        }
    }

    void setInfos(){
        try {
            if (ipValidation(subnetIP)){
                if (selectedSubmask.equals(subnet248)){
                    ATM.setText(ATMIP);
                    subnetBroadcast.setText(subnetBroadcastIP);
                    router.setText(routerIP);
                    subnetIPLabel.setText(subnetIP);
                    DVRMask.setText(DVRMaskIP);
                }
                else if(selectedSubmask.equals(subnet252)){
                    ATM.setText(ATMIP);
                    subnetBroadcast.setText(subnetBroadcastIP);
                    router.setText(routerIP);
                    subnetIPLabel.setText(subnetIP);
                    DVRMask.setText(DVRMaskIP);
                }
                warningLabel.setVisible(false);
                ADSLTunnel.setText(ADSLTunnelIP);
                DVRGateway.setText(DVRGatewayIP);
                TGTunnel.setText(TGTunnelIP);
                DVRMask.setVisible(true);
            }else{
                warningLabel.setText("IP adresi yanlış!");
                warningLabel.setVisible(true);
            }
        }catch (NullPointerException e){
            warningLabel.setText("Subnet Mask seçiniz!");
            warningLabel.setVisible(true);
        }

    }

    static boolean ipValidation(String IP){
        String zeroTo255Regex = "(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])";
        String regex = zeroTo255Regex+"\\."+zeroTo255Regex+"\\."+zeroTo255Regex+"\\."+zeroTo255Regex;
        Pattern pattern = Pattern.compile(regex);
        if (IP==null){
            return false;
        }
        Matcher matcher = pattern.matcher(IP);
        return matcher.matches();
    }

    static boolean integerValid(String integer,int min,int maks){
        if (integer.length()<=maks && integer.length()>=min){
            try {
                Integer.parseInt(integer);
            }catch (NumberFormatException | NullPointerException e){
                return false;
            }
            return true;
        }else{
            return false;
        }
    }

    static String parseIP(String IP,int add){
        String firstTreePart = IP.substring(0,IP.lastIndexOf("."));
        String lastPart = IP.substring(IP.lastIndexOf(".")+1);
        int parsedLast = Integer.parseInt(lastPart)+add;
        return firstTreePart +"."+parsedLast;
    }

    void DVRGatewayCalc(){
        String firstTreePart = DVRGatewayIP.substring(0, DVRGatewayIP.lastIndexOf("."));
        String lastPart = DVRGatewayIP.substring(DVRGatewayIP.lastIndexOf(".")+1);
        int parsedLast = Integer.parseInt(lastPart);
       if (parsedLast<=241){
            DVRGatewayIP =firstTreePart+"."+(parsedLast+8);
        }else{
            String firstTwo = firstTreePart.substring(0,firstTreePart.lastIndexOf("."));
            String lastTwo = firstTreePart.substring(firstTreePart.lastIndexOf(".")+1);
            int parsedLastTwo = Integer.parseInt(lastTwo)+1;
            DVRGatewayIP = firstTwo+"."+parsedLastTwo+".1";
        }

    }

    void ADSLTunnelCalc(){
        String firstTreePart = ADSLTunnelIP.substring(0, ADSLTunnelIP.lastIndexOf("."));
        String lastPart = ADSLTunnelIP.substring(ADSLTunnelIP.lastIndexOf(".")+1);
        int parsedLast = Integer.parseInt(lastPart);
        if(parsedLast<=253){
            ADSLTunnelIP = firstTreePart+"."+(parsedLast+1);
        }else{
            String firstTwo = firstTreePart.substring(0,firstTreePart.lastIndexOf("."));
            String lastTwo = firstTreePart.substring(firstTreePart.lastIndexOf(".")+1);
            int parsedLastTwo = Integer.parseInt(lastTwo)+1;
            ADSLTunnelIP = firstTwo+"."+parsedLastTwo+".1";
        }
    }

    static String TGTunnelCalc(String ADSLTunnelIP){
        int firstDotIndex=ADSLTunnelIP.indexOf(".");
        int secondDotIndex=ADSLTunnelIP.indexOf(".",firstDotIndex+1);
        String firstPart=ADSLTunnelIP.substring(0,firstDotIndex+1);
        String thirdPart= ADSLTunnelIP.substring(secondDotIndex);
        String secondPart=ADSLTunnelIP.substring(firstDotIndex+1,secondDotIndex);
        int parseSecondPart = Integer.parseInt(secondPart)-3;
        return firstPart+parseSecondPart+thirdPart;
    }

    String subnetIPCalc(String IP){
        String firstTreePart = IP.substring(0, IP.lastIndexOf("."));
        String lastPart = IP.substring(IP.lastIndexOf(".")+1);

        if (selectedSubmask.equals(subnet248)){
            int parsedLast = Integer.parseInt(lastPart);
            if (parsedLast<=241){
                return firstTreePart+"."+(parsedLast+8);
            }else{
                String firstTwo = firstTreePart.substring(0,firstTreePart.lastIndexOf("."));
                String lastTwo = firstTreePart.substring(firstTreePart.lastIndexOf(".")+1);
                int parsedLastTwo = Integer.parseInt(lastTwo)+1;
                return firstTwo+"."+parsedLastTwo+".0";
            }
        }else{
            int parsedLast = Integer.parseInt(lastPart);
            if (parsedLast<=241){
                return firstTreePart+"."+(parsedLast+4);
            }else{
                String firstTwo = firstTreePart.substring(0,firstTreePart.lastIndexOf("."));
                String lastTwo = firstTreePart.substring(firstTreePart.lastIndexOf(".")+1);
                int parsedLastTwo = Integer.parseInt(lastTwo)+1;
                return firstTwo+"."+parsedLastTwo+".0";
            }
        }

    }


    void searchEmptyADSLTunnel(){
        while (connection.searchADSLTunnel(ADSLTunnelIP)){
            ADSLTunnelCalc();
        }

    }

    void searchEmptyIP(){
        String searchSubnetIP = connection.getIPBlock(selectedCity);
        while (connection.searchSubnetIP(searchSubnetIP)){
           searchSubnetIP = subnetIPCalc(searchSubnetIP);
        }
        subnetIP= searchSubnetIP;
    }

    void searchEmptyDVRGateway(){
        while (connection.searchDVRGateway(DVRGatewayIP)){
            DVRGatewayCalc();
        }
    }

    void reset(){
        atmNameTextField.setText("");
        atmNameTextField.setStyle("-fx-border-color: transparent");
        subeNumTextField.setText("");
        subeNumTextField.setStyle("-fx-border-color: transparent");
        ATMIDTextField.setText("");
        ATMIDTextField.setStyle("-fx-border-color: transparent");
        terminalNoTextField.setText("");
        terminalNoTextField.setStyle("-fx-border-color: transparent");
        subnetIPLabel.setText("");
        subnetBroadcast.setText("");
        router.setText("");
        ATM.setText("");
        ADSLTunnel.setText("");
        TGTunnel.setText("");
        DVRGateway.setText("");
        DVRMask.setText("");


    }

    void resetDoldur(){
        atmNameTextField.setText("");
        subeNumTextField.setText("");
        ATMIDTextField.setText("");
        terminalNoTextField.setText("");
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
