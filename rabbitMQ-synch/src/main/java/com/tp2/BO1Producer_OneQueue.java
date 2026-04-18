package com.tp2;

import com.rabbitmq.client.*;
import java.sql.*;

public class BO1Producer_OneQueue {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(DBConfig.MQ_HOST);
        com.rabbitmq.client.Connection mqConn = factory.newConnection();       
        Channel channel = mqConn.createChannel();
        channel.queueDeclare(DBConfig.QUEUE, true, false, false, null);

        java.sql.Connection dbConn = DriverManager.getConnection(
            DBConfig.BO1_URL, DBConfig.DB_USER, DBConfig.DB_PASS);
        PreparedStatement ps = dbConn.prepareStatement("SELECT * FROM Product_Sales");
        ResultSet rs = ps.executeQuery();

        int count = 0;
        while (rs.next()) {
            String msg = "BO1," +  // ← on identifie la source
                         rs.getString("Date")    + "," +
                         rs.getString("Region")  + "," +
                         rs.getString("Product") + "," +
                         rs.getInt("Qty")        + "," +
                         rs.getDouble("Cost")    + "," +
                         rs.getDouble("Amt")     + "," +
                         rs.getDouble("Tax")     + "," +
                         rs.getDouble("Total");
            channel.basicPublish("", DBConfig.QUEUE, null, msg.getBytes());
            System.out.println("[BO1] Envoyé : " + msg);
            count++;
        }
        System.out.println("[BO1] Total : " + count + " messages envoyés.");

        dbConn.close();
        channel.close();
        mqConn.close();
    }
}