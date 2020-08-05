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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;

import java.io.*;
import java.net.InetAddress;
import java.util.*;
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
    public Label ipBlockWarningLabel;
    public Button kaydetButton;
    public Button minimizeButton;
    public Button exitButton;
    public Button araButton;
    public Button pingAtButton;
    public Button ipBlockButton;
    public Button databaseAktarButton;
    public Button exceldenAktarButton;
    public TextField ATMIDTextField;
    public TextField atmNameTextField;
    public TextField subeNumTextField;
    public TextField terminalNoTextField;
    public TextField subeAdıTextField;
    public ComboBox<String> subnetMaskComboBox;
    public ComboBox<String> iller;
    public ComboBox<String> araSecComboBox;
    public ComboBox<String> araSonucComboBox;
    public ComboBox<String> sendPingComboBox;
    public ImageView ZBimageView;

    private final String subnet252 = "255.255.255.252";
    private final String subnet248 = "255.255.255.248";
    private String DVRGatewayIP = "10.240.1.1";
    private final String DVRMaskIP = "255.255.255.248";
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

    public void setSubnetMask(boolean subnetMask) {
        this.subnetMask = subnetMask;
    }

    public void setSubeAdı(boolean subeAdı) {
        this.subeAdı = subeAdı;
    }

    boolean ATMName=false;
    boolean ATMID=false;
    boolean subeNum=false;
    boolean terminalNo=false;
    boolean subnetMask=false;
    boolean subeAdı=false;
    DatabaseConnection connection= new DatabaseConnection();

    @FXML
    void initialize(){
        File file = new File("./images/zb_logo.png");
        Image image = new Image(file.toURI().toString());
        ZBimageView.setImage(image);
        ZBimageView.setPreserveRatio(true);



        araSecComboBox.getItems().add("ATM ID");
        araSecComboBox.getItems().add("ATM Adı");
        araSecComboBox.getItems().add("Sube Numarası");
        araSecComboBox.getItems().add("Subnet IP");
        araSecComboBox.getItems().add("ADSL Tunnel");
        araSecComboBox.getItems().add("Terminal No");

        sendPingComboBox.getItems().addAll("Router IP","DVR Gateway","ATM IP","ADSL Tunnel","3G Tunnel","Subnet IP","Subnet Broadcast");
        illerDoldur();

        new AutoCompleteComboBoxListener<>(iller);
        new AutoCompleteComboBoxListener<>(araSonucComboBox);
        subnetMaskComboBox.getItems().add(subnet248);
        subnetMaskComboBox.getItems().add(subnet252);


        if (iller.getItems().isEmpty()) {
            ipBlockButton.setDisable(false);
            ipBlockWarningLabel.setVisible(true);
        }

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
                if (s1 != null){
                    araSonucComboBox.getItems().add(s1);
                }
            }


        } );
        subeAdıTextField.textProperty().addListener((observableValue, s, t1) -> {
            if(t1.length()>=2){
                warningLabel.setVisible(false);
                subeAdıTextField.setStyle("-fx-border-color: lawngreen");
                setSubeAdı(true);
            }else{
                subeAdıTextField.setStyle("-fx-border-color: red");
                warningLabel.setText("Şube Adı adı çok kısa");
                warningLabel.setVisible(true);
                setSubeAdı(false);
            }
            disableKaydetButton();
        });

        terminalNoTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String value = newValue;
            if (newValue.length()>2){
                if(value.charAt(0)=='Z')
                value=value.substring(1);
            }

            if (integerValid(value,4,6)){
                terminalNoTextField.setText("Z"+value);
                if (!connection.checkConflict("TerminalNo",terminalNoTextField.getText())){
                    terminalNoTextField.setStyle("-fx-border-color: lawngreen");
                    setTerminalNo(true);
                    warningLabel.setVisible(false);
                }else{
                    warningLabel.setText("Terminal No çakışıyor");
                    warningLabel.setVisible(true);
                }
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
                if (!connection.checkConflict("AtmID",t1)){
                    ATMIDTextField.setStyle("-fx-border-color: lawngreen");
                    setATMID(true);
                    warningLabel.setVisible(false);
                }else{
                    warningLabel.setText("Atm ID çakışıyor");
                    warningLabel.setVisible(true);
                }

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
            setSubnetMask(true);
            disableKaydetButton();
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

    void illerDoldur(){
        for (String s : connection.getSehir()) {
            iller.getItems().add(s);
        }
    }

    void disableKaydetButton(){
        if (ATMID&&ATMName&&subeNum&&terminalNo&&subnetMask&&subeAdı){
            kaydetButton.setDisable(false);
            warningLabel.setText("Kaydederken ping kontrolu yapılacak lütfen bekleyin!");
            warningLabel.setVisible(true);
        }else{
            kaydetButton.setDisable(true);
        }
    }

    @FXML
    void ipBlockAktarButton(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("IP Block Seçiniz!");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("EXCEL File (*.XLSX)","*.XLSX");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File IPBlockFile = fileChooser.showOpenDialog(null);
        if (IPBlockFile!=null) {
            getExcel(IPBlockFile);
            illerDoldur();
            ipBlockButton.setDisable(true);
            ipBlockWarningLabel.setVisible(false);
        }
    }

    @FXML
    void databaseAktarButton(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Konum Seçiniz!");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("EXCEL File (*.XLSX)","*.XLSX");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showSaveDialog(null);
        if (file!=null){
            setExcel(file.getAbsolutePath());
        }
    }


    void getExcel(File IPBLockFile) {
        try {
            FileInputStream file = new FileInputStream(IPBLockFile);
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

            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getExcelToDatabase(File file){
        try {
            FileInputStream inputStream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex<=sheet.getLastRowNum();rowIndex++){
                Row row = sheet.getRow(rowIndex);
                if (row != null){
                    String[] arr = new String[15];
                    for (int i=0;i<15;i++){
                        Cell cell = row.getCell(i);
                        if (cell == null){
                            arr[i]=null;
                        }else{
                            arr[i]=cell.getStringCellValue();
                        }
                    }
                    connection.setDataToDatabase(arr[0],arr[1],arr[2],arr[3],arr[4],
                            arr[5],arr[6],arr[7],arr[8],arr[9],
                            arr[10],arr[11],arr[12],arr[13],arr[14]);
                }
            }

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void excelToDatabaseButtonAction(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Konum Seçiniz!");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("EXCEL File (*.XLSX)","*.XLSX");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file!=null){
            getExcelToDatabase(file);
        }

    }

    void setExcel(String URL){
        Set<Integer> keySet = connection.exportExcelFile().keySet();
        Map<Integer, Object[]> map = connection.exportExcelFile();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Tablo");

        XSSFTable table = sheet.createTable();
        CTTable cttable = table.getCTTable();

        cttable.setDisplayName("Tablo");
        cttable.setId(1);
        cttable.setName("Tablo");
        cttable.setRef("A1:O"+keySet.size());
        cttable.setTotalsRowShown(true);

        CTTableStyleInfo styleInfo = cttable.addNewTableStyleInfo();
        styleInfo.setName("TableStyleMedium10");
        styleInfo.setShowColumnStripes(false);
        styleInfo.setShowRowStripes(true);

        CTTableColumns columns = cttable.addNewTableColumns();
        columns.setCount(15);
        for (int i = 1; i <= 15; i++) {
            CTTableColumn column = columns.addNewTableColumn();
            column.setId(i);
            column.setName("IP Tablosu");
        }
        for (int r = 0; r < keySet.size(); r++) {
            XSSFRow row = sheet.createRow(r);
            Object[] objArr = map.get(r);
            for(int c = 0; c < objArr.length; c++) {
                XSSFCell cell = row.createCell(c);
                if (objArr[c] == null){
                    cell.setCellValue("");
                }else{
                    cell.setCellValue(objArr[c].toString());
                }
            }
        }

        for (int i =0;i<15; i++){
            sheet.autoSizeColumn(i);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        try {
            FileOutputStream out = new FileOutputStream(new File(URL));
            alert.setTitle("Başarılı");
            alert.setContentText("Başarılı bir şekilde yazdırıldı.\n\n"+URL);
            alert.setHeaderText(null);
            alert.showAndWait();
            workbook.write(out);
            out.close();

        } catch (IOException e) {
            alert.setTitle("Başarısız");
            alert.setContentText("Yazdırılamadı lütfen tekrar deneyin!");
            alert.setHeaderText(null);
            alert.showAndWait();
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
            controller.setDataToFields(search, key);

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
        TGTunnelIP = TGTunnelCalc(ADSLTunnelIP);
        IPCalc();
        setInfos();
    }

    @FXML
    void pingButtonAction(){
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

    }

    void sendPing(String IP, Label label){
        try {
            InetAddress routerPing = InetAddress.getByName(IP);
            if (routerPing.isReachable(3000)){
                label.setStyle("-fx-background-color: lawngreen;");
            }else{
                label.setStyle("-fx-background-color: red;");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static boolean sendPing(String IP){
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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            if (!(sendPing(routerIP)&&sendPing(DVRGatewayIP))) {
                setDatabase();
            }else{
                alert.setAlertType(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Ping'e ulaşıldı, yine de kaydedilsin mi ?");
                Optional<ButtonType> button = alert.showAndWait();
                if (button.get() == ButtonType.OK){
                    setDatabase();
                }else{
                    resetDoldur();
                }
            }

    }


    void setDatabase() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("");
        alert.setHeaderText("LÜTFEN BİLGİLERİ KONTROL EDİNİZ!");
        alert.setContentText(
                "İl:                             " + selectedCity +
                        "\nATM Adı:                 " + atmNameTextField.getText() +
                        "\nATM ID:                  " + ATMIDTextField.getText() +
                        "\nŞube Numarası:       " + subeNumTextField.getText() +
                        "\nSubnet Mask:          " + selectedSubmask +
                        "\nSubnet IP :             " + subnetIP +
                        "\nSubnet Broadcast:   " + subnetBroadcastIP +
                        "\nRouter IP:                " + routerIP +
                        "\nATM IP:                   " + ATMIP +
                        "\nADSL Tunnel:           " + ADSLTunnelIP +
                        "\nDVR Gateway:         " + DVRGatewayIP +
                        "\n3G Tunnel:              " + TGTunnelIP +
                        "\nDVR Mask:              " + DVRMaskIP +
                        "\n Terminal No:          " + terminalNoTextField.getText());
        boolean checkConn = connection.setDataToDatabase(
                selectedCity,
                subeNumTextField.getText(),
                subeAdıTextField.getText(),
                atmNameTextField.getText(),
                subnetIP,
                subnetBroadcastIP,
                selectedSubmask,
                routerIP,
                ATMIP,
                ADSLTunnelIP,
                TGTunnelIP,
                ATMIDTextField.getText(),
                DVRMaskIP,
                DVRGatewayIP,
                terminalNoTextField.getText());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (checkConn) {
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setTitle("Başarılı!");
                alert.setHeaderText(null);
                alert.setContentText("Başarılı bir şekilde kaydedildi!");
                alert.showAndWait();
                reset();
                ;
                kaydetButton.setDisable(true);
            } else {
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setTitle("Başarısız!");
                alert.setContentText("Bilgileriniz kaydedilemedi!");
                alert.showAndWait();
            }
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
        subeAdıTextField.setText("");
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
