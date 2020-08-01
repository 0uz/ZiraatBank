package com.ziraatbank.util;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnection {
    Connection connection=null;
    String url="jdbc:sqlite:./src/database/database.db";
    public Connection connect(){
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;
    }

    public void createDB(){
        try (Connection connection = DriverManager.getConnection(url)){
            if (connection != null){
                DatabaseMetaData metaData = connection.getMetaData();
                System.out.println("Driver name is "+metaData.getDriverName());
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void createTable(){
        String sql = "CREATE TABLE IF NOT EXISTS database (" +
                "Sehir varchar," +
                "AtmAdı varchar," +
                "SubeNum varchar," +
                "AtmID varchar," +
                "SubnetMask varchar(15)," +
                "SubnetIP varchar(15)," +
                "SubnetBroadcast varchar(15)," +
                "RouterIP varchar(15)," +
                "AtmIP varchar(15)," +
                "ADSLTunnel varchar(15)," +
                "TGTunnel varchar(15)," +
                "DVRMask varchar(15)," +
                "DVRGateway varchar(15));";

        String sql2 = "CREATE TABLE IF NOT EXISTS IPBlock (" +
                "Sehir varchar," +
                "IP varchar(15));";

        System.out.println("Tables are created if not exitst");
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            stmt.execute(sql2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertIPBlock(String sehir,String IP){
        String sql ="insert into IPBlock (Sehir, IP) values (?,?)";
            try (Connection connection=this.connect();
                PreparedStatement pst = connection.prepareStatement(sql)){
                pst.setString(1,sehir);
                pst.setString(2,IP);
                pst.executeUpdate();
            }catch(SQLException e){
                e.printStackTrace();
            }

    }

    public String getIPBlock(String sehir){
        String newSehir = sehir.toUpperCase();
        String sql ="select IP from IPBlock where Sehir= ?";
        try (Connection connection=this.connect();
             PreparedStatement pst = connection.prepareStatement(sql)){
            pst.setString(1,newSehir);
            ResultSet rs = pst.executeQuery();
            return rs.getString("IP");
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }

    }

    public boolean setInfos(String Sehir,String atmAdı, String subeNum, String atmID, String subnetMask, String subnetIP, String subnetBroadcast,
                            String routerIP, String atmIP, String ADSLTunnel, String TGTunnel, String DVRMask, String DVRGateway){
            String sql ="insert into database (Sehir, AtmAdı, SubeNum, AtmID, SubnetMask, " +
                        "SubnetIP, SubnetBroadcast, RouterIP, AtmIP, ADSLTunnel, TGTunnel, DVRMask, DVRGateway) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    try (Connection connection=this.connect();
                    PreparedStatement pst = connection.prepareStatement(sql)){
                        pst.setString(1,Sehir);
                        pst.setString(2,atmAdı);
                        pst.setString(3,subeNum);
                        pst.setString(4,atmID);
                        pst.setString(5,subnetMask);
                        pst.setString(6,subnetIP);
                        pst.setString(7,subnetBroadcast);
                        pst.setString(8,routerIP);
                        pst.setString(9,atmIP);
                        pst.setString(10,ADSLTunnel);
                        pst.setString(11,TGTunnel);
                        pst.setString(12,DVRMask);
                        pst.setString(13,DVRGateway);
                        pst.executeUpdate();
                        return true;
                    }catch(SQLException e){
                        e.printStackTrace();
                        return false;
                    }
    }

    public boolean searchSubnetIP(String IP){
        String sql ="select * from database where SubnetIP ='"+IP+"'";
        try (Connection connection=this.connect();
             Statement pst = connection.createStatement();
            ResultSet rs = pst.executeQuery(sql)){
            while (rs.next()){
                String subnetIP = rs.getString("SubnetIP");
                if (subnetIP.length()==0){
                    return false;
                }else{
                    return true;
                }
            }
            return false;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean searchDVRGateway(String IP){
        String sql ="select * from database where DVRGateway ='"+IP+"'";
        try (Connection connection=this.connect();
             Statement pst = connection.createStatement();
             ResultSet rs = pst.executeQuery(sql)){
            while (rs.next()){
                String subnetIP = rs.getString("DVRGateway");
                if (subnetIP.length()==0){
                    return false;
                }else{
                    return true;
                }
            }
            return false;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean searchADSLTunnel(String IP){
        String sql ="select * from database where ADSLTunnel ='"+IP+"'";
        try (Connection connection=this.connect();
             Statement pst = connection.createStatement();
             ResultSet rs = pst.executeQuery(sql)){
            while (rs.next()){
                String subnetIP = rs.getString("ADSLTunnel");
                if (subnetIP.length()==0){
                    return false;
                }else{
                    return true;
                }
            }
            return false;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<String> getSehir(){
        String sql ="select Sehir from IPBlock";
        try (Connection connection=this.connect();
             Statement pst = connection.createStatement();
             ResultSet rs = pst.executeQuery(sql)){
            ArrayList<String> iller= new ArrayList();
            while (rs.next()){
               iller.add(rs.getString("Sehir"));
            }
            return iller;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getInfos(String searchName, String key){
        String sql ="select AtmAdı,AtmID,SubeNum,SubnetIP,RouterIP," +
                "SubnetBroadcast,AtmIP,ADSLTunnel,TGTunnel,DVRGateway," +
                "DVRMask, SubnetMask from database where "+searchName+"='"+key+"'";
        try (Connection connection=this.connect();
             Statement pst = connection.createStatement();
             ResultSet rs = pst.executeQuery(sql)){
            ArrayList<String> result= new ArrayList();
            while (rs.next()){
                result.add(rs.getString("AtmAdı"));
                result.add(rs.getString("AtmID"));
                result.add(rs.getString("SubeNum"));
                result.add(rs.getString("SubnetIP"));
                result.add(rs.getString("RouterIP"));
                result.add(rs.getString("SubnetBroadcast"));
                result.add(rs.getString("AtmIP"));
                result.add(rs.getString("ADSLTunnel"));
                result.add(rs.getString("TGTunnel"));
                result.add(rs.getString("DVRGateway"));
                result.add(rs.getString("DVRMask"));
                result.add(rs.getString("SubnetMask"));
            }
            return result;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }

    }

    public boolean checkConflict(String check,String IP){
        String sql ="select * from database where "+check+"='"+IP+"'";
        System.out.println(sql);
        try (Connection connection=this.connect();
             Statement pst = connection.createStatement();
             ResultSet rs = pst.executeQuery(sql)){
                while (rs.next()){
                    if (rs.getString(1).isEmpty()){
                        return false;
                    }else{
                        return true;
                    }
                }
                return false;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateInfos(String search,String key, String atmAdi, String atmID,
                            String subeNum, String subnetIP, String routerIP,
                            String subnetBroadcastIp, String atmIP,
                            String adslTunelIP, String TGTunnelI,
                            String DVRGateway){

        String sql = "update database set " +
                "AtmAdı = ? ," +
                "AtmID = ? ," +
                "SubeNum = ? ," +
                "SubnetIP = ? ," +
                "RouterIP = ? ," +
                "SubnetBroadcast = ? ," +
                "AtmIP = ?," +
                "ADSLTunnel = ? ," +
                "TGTunnel = ? ," +
                "DVRGateway = ? " +
                "where "+search+"= ?;";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,atmAdi);
            pstmt.setString(2,atmID);
            pstmt.setString(3,subeNum);
            pstmt.setString(4,subnetIP);
            pstmt.setString(5,routerIP);
            pstmt.setString(6,subnetBroadcastIp);
            pstmt.setString(7,atmIP);
            pstmt.setString(8,adslTunelIP);
            pstmt.setString(9,TGTunnelI);
            pstmt.setString(10,DVRGateway);
            pstmt.setString(11,key);

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public ArrayList<String> getAra(String search){
        String sql ="select "+search+" from database;";
        try (Connection connection=this.connect();
             Statement pst = connection.createStatement();
             ResultSet rs = pst.executeQuery(sql)){
            ArrayList<String> result= new ArrayList();
            while (rs.next()){
                result.add(rs.getString(search));
            }
            return result;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }

    }



}
