package com.tp2;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class BO1Producer {
    public static void main(String[] args) throws Exception {
        // 1. Connexion RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(DBConfig.MQ_HOST);
        Connection mqConn = factory.newConnection();
        Channel channel = mqConn.createChannel();
        channel.queueDeclare(DBConfig.QUEUE_BO1, true, false, false, null);

        // 2. Lecture MySQL BO1
        java.sql.Connection dbConn = DriverManager.getConnection(
            DBConfig.BO1_URL, DBConfig.DB_USER, DBConfig.DB_PASS);
        PreparedStatement ps = dbConn.prepareStatement("SELECT * FROM Product_Sales WHERE is_synced = 0");
        ResultSet rs = ps.executeQuery();

        // 3. Publication des messages
        PreparedStatement updateStmt = dbConn.prepareStatement("UPDATE Product_Sales SET is_synced = 1 WHERE id = ?");
        int count = 0;
        while (rs.next()) {
            String msg = rs.getString("Date")    + "," +
                         rs.getString("Region")  + "," +
                         rs.getString("Product") + "," +
                         rs.getInt("Qty")        + "," +
                         rs.getDouble("Cost")    + "," +
                         rs.getDouble("Amt")     + "," +
                         rs.getDouble("Tax")     + "," +
                         rs.getDouble("Total");
            channel.basicPublish("", DBConfig.QUEUE_BO1, null, msg.getBytes());
            updateStmt.setInt(1, rs.getInt("id"));
            updateStmt.executeUpdate();
            System.out.println("[BO1] Envoyé : " + msg);
            count++;
        }
        System.out.println("[BO1] Total messages envoyés : " + count);

        dbConn.close();
        channel.close();
        mqConn.close();
    }
}