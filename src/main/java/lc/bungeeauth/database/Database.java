package lc.bungeeauth.database;

import lc.bungeeauth.LCBungeeAuth;
import lc.bungeeauth.managers.AuthManager;
import lc.bungeeauth.managers.AuthPlayer;
import lc.bungeeauth.managers.EncriptadorAES;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Database {
    static LCBungeeAuth plugin = LCBungeeAuth.getInstance();

    public static Boolean playerexits(String name) {
        Connection thiscon = plugin.getConnection();
        try {
            PreparedStatement statement = thiscon.prepareStatement("SELECT * FROM `" + plugin.authtable + "` WHERE `Player` = ?;");
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(thiscon != null)
                try {
                    thiscon.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
        }
        return false;
    }

    public static boolean isPremium(String player) {
        Connection thiscon = plugin.getConnection();
        try {
            PreparedStatement statement = thiscon.prepareStatement("SELECT * FROM `" + plugin.authtable + "` WHERE `Player` = ?;");
            statement.setString(1, player);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getInt("Premium") == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            if(thiscon != null)
                try {
                    thiscon.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
        }
    }

    public static boolean hasPassword(String name){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection thiscon = plugin.getConnection();
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT * ");
            queryBuilder.append("FROM `").append(LCBungeeAuth.getInstance().authtable).append("` ");
            queryBuilder.append("WHERE `Player` = ?;");
            preparedStatement = thiscon.prepareStatement(queryBuilder.toString());
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getString("Password") != null;
            }
            return false;
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        } finally {
            if (resultSet != null)
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                    ignored.printStackTrace();
                }
            if (preparedStatement != null)
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                    ignored.printStackTrace();
                }
            if(thiscon != null)
                try {
                    thiscon.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
        }
        return false;
    }

    public static void createAuthPlayer(String name) {
        Connection thiscon = plugin.getConnection();
        try {
            if (!playerexits(name)) {
                PreparedStatement insert =thiscon.prepareStatement("INSERT INTO `" + plugin.authtable + "` (Player, Password, IP, Time, Premium) VALUES (?,?,?,?,?);");
                AuthPlayer pp = AuthPlayer.getAuthPlayer(name);
                insert.setString(1, name);
                insert.setString(2, null);
                if(AuthManager.Encrypt()){
                    EncriptadorAES encriptador = new EncriptadorAES();
                    insert.setString(3, encriptador.encriptar(pp.getIp(), AuthManager.getClave()));
                }else{
                    insert.setString(3,pp.getIp());
                }

                Date date = new Date();
                DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                insert.setString(4, hourdateFormat.format(date));
                insert.setInt(5, 0);
                insert.executeUpdate();
            }
        } catch (SQLException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            AuthPlayer pp = AuthPlayer.getAuthPlayer(name);
            pp.getProxyPlayer().disconnect(ChatColor.RED + "Ocurrió un error al crear tus datos.");
        }finally {
            if(thiscon != null)
                try {
                    thiscon.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
        }
    }

    public static ArrayList<String> getPlayerbyIP(AuthPlayer ap) {
        ArrayList<String> list = new ArrayList<String>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        long now = System.currentTimeMillis();
        Connection thiscon = plugin.getConnection();
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT * ");
            queryBuilder.append("FROM `" +plugin.authtable + "` ");
            queryBuilder.append("WHERE `IP` = ? ;");
            preparedStatement = thiscon.prepareStatement(queryBuilder.toString());
            preparedStatement.setString(1, ap.getLastip());
            resultSet = preparedStatement.executeQuery();
            while (resultSet != null && resultSet.next()) {
                String player = resultSet.getString("Player");
                list.add(player);
            }
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        } finally {
            if (resultSet != null)
                try {
                    thiscon.close();
                    resultSet.close();
                } catch (SQLException ignored) {
                    ignored.printStackTrace();
                }
            if (preparedStatement != null)
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                    ignored.printStackTrace();
                }
            if(thiscon != null)
                try {
                    thiscon.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
        }
        return list;
    }

    public static void loadAuthPlayerInfo(String jugname) {
        createAuthPlayer(jugname);
        AuthPlayer jug = AuthPlayer.getAuthPlayer(jugname);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection thiscon = plugin.getConnection();
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT * ");
            queryBuilder.append("FROM `" + plugin.authtable + "` ");
            queryBuilder.append("WHERE `Player` = ? ");
            queryBuilder.append("LIMIT 1;");
            preparedStatement = thiscon.prepareStatement(queryBuilder.toString());
            preparedStatement.setString(1, jugname);
            resultSet = preparedStatement.executeQuery();
            if (resultSet != null && resultSet.next()) {
                jug.setPremium(resultSet.getInt("Premium") == 1);
                Date date = new Date();
                DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                jug.setLastconnection(hourdateFormat.format(date));
                if(AuthManager.Encrypt()) {
                    EncriptadorAES encriptador = new EncriptadorAES();
                    jug.setLastip(encriptador.desencriptar(resultSet.getString("IP"), AuthManager.getClave()));
                } else {
                    jug.setLastip(resultSet.getString("IP"));
                }
                jug.setRegister(resultSet.getString("Password") != null);
                if(jug.isRegister()){
                    jug.setPassword(resultSet.getString("Password"));
                } else {
                    jug.setPassword(null);
                }
            }
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        } finally {
            if (resultSet != null)
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                    ignored.printStackTrace();
                }
            if (preparedStatement != null)
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                    ignored.printStackTrace();
                }
            if(thiscon != null)
                try {
                    thiscon.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
        }
    }

    public static String  getLastConnection(String jug) {
        String str=null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Connection thiscon = plugin.getConnection();
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT * ");
            queryBuilder.append("FROM `"+ LCBungeeAuth.getInstance().authtable+ "` ");
            queryBuilder.append("WHERE `Player` = ?;");
            preparedStatement = thiscon.prepareStatement(queryBuilder.toString());
            preparedStatement.setString(1, jug);
            resultSet = preparedStatement.executeQuery();
            if (resultSet != null){
                str = resultSet.getString("Time");
            }
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        } finally {
            if (resultSet != null)
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                    ignored.printStackTrace();
                }
            if (preparedStatement != null)
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                    ignored.printStackTrace();
                }
            if(thiscon != null)
                try {
                    thiscon.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
        }
        return str;
    }
    public static ArrayList<String> getPremiums() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> premiums = new ArrayList<String>();
        Connection thiscon = plugin.getConnection();
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT * ");
            queryBuilder.append("FROM `"+ LCBungeeAuth.getInstance().authtable+ "` ");
            queryBuilder.append("WHERE `Premium` = 1;");
            preparedStatement = thiscon.prepareStatement(queryBuilder.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet != null)
                while (resultSet.next()) {
                        premiums.add(resultSet.getString("Player"));
                }
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        } finally {
            if (resultSet != null)
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                    ignored.printStackTrace();
                }
            if (preparedStatement != null)
                try {
                    preparedStatement.close();
                } catch (SQLException ignored) {
                    ignored.printStackTrace();
                }
            if(thiscon != null)
                try {
                    thiscon.close();
                }catch (SQLException e){
                    e.printStackTrace();
                }
        }
        return premiums;
    }

    public static void saveAllPlayerAuthData(final AuthPlayer jug) {
        LCBungeeAuth.getInstance().getProxy().getScheduler().runAsync(LCBungeeAuth.getInstance(), () -> {
            PreparedStatement preparedStatement = null;
            Connection thiscon = plugin.getConnection();
            try {
                StringBuilder queryBuilder = new StringBuilder();
                queryBuilder.append("INSERT INTO `" + LCBungeeAuth.getInstance().authtable+ "` ");
                queryBuilder.append("(`Player`, `IP`, `Password`, `Time` ,`Premium`) VALUES ");
                queryBuilder.append("(?, ?, ?, ?, ?) ");
                queryBuilder.append("ON DUPLICATE KEY UPDATE ");
                queryBuilder.append("`IP` = ?, `Password` = ?, `Time` = ?, `Premium` = ?;");
                preparedStatement = thiscon.prepareStatement(queryBuilder.toString());
                preparedStatement.setString(1, jug.getName());
                if(AuthManager.Encrypt()){
                    EncriptadorAES encrp = new EncriptadorAES();
                    preparedStatement.setString(2, encrp.encriptar(jug.getLastip(), AuthManager.getClave()));
                } else {
                    preparedStatement.setString(2, jug.getLastip());
                }
                preparedStatement.setString(3, jug.getPassword());
                preparedStatement.setString(4, jug.getLastconnection());
                if(jug.isPremium()){
                    preparedStatement.setInt(5, 1);
                }else{
                    preparedStatement.setInt(5, 0);
                }
                //UPDATE
                if(AuthManager.Encrypt()){
                    EncriptadorAES encrp = new EncriptadorAES();
                    preparedStatement.setString(6,encrp.encriptar( jug.getLastip(), AuthManager.getClave()));
                } else {
                    preparedStatement.setString(6, jug.getLastip());

                }
                preparedStatement.setString(7, jug.getPassword());
                preparedStatement.setString(8, jug.getLastconnection());
                if(jug.isPremium()){
                    preparedStatement.setInt(9, 1);
                }else{
                    preparedStatement.setInt(9, 0);
                }




                preparedStatement.executeUpdate();
            } catch (Exception sqlException) {
                sqlException.printStackTrace();
            } finally {
                if (preparedStatement != null)
                    try {
                        preparedStatement.close();
                    } catch (SQLException ignored) {
                        ignored.printStackTrace();
                    }
                if(thiscon != null)
                    try {
                        thiscon.close();
                    }catch (SQLException e){
                        e.printStackTrace();
                    }
            }
        });
    }




}
