package com.tp2;

import com.rabbitmq.client.*;
import java.sql.*;

public class HOConsumer_OneQueue {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(DBConfig.MQ_HOST);
        com.rabbitmq.client.Connection mqConn = factory.newConnection();
        Channel channel = mqConn.createChannel();
        channel.queueDeclare(DBConfig.QUEUE, true, false, false, null);

        java.sql.Connection dbConn = DriverManager.getConnection(
            DBConfig.HO_URL, DBConfig.DB_USER, DBConfig.DB_PASS);
        String sql = "INSERT INTO Product_Sales (Date, Region, Product, Qty, Cost, Amt, Tax, Total) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = dbConn.prepareStatement(sql);

        System.out.println("[HO] En attente de messages sur " + DBConfig.QUEUE + "...");

        DeliverCallback callback = (tag, delivery) -> {
            String body = new String(delivery.getBody());
            String[] parts = body.split(",");
            // parts[0] = source (BO1 ou BO2), parts[1..8] = données
            String source = parts[0];
            try {
                ps.setString(1, parts[1]);
                ps.setString(2, parts[2]);
                ps.setString(3, parts[3]);
                ps.setInt(4, Integer.parseInt(parts[4]));
                ps.setDouble(5, Double.parseDouble(parts[5]));
                ps.setDouble(6, Double.parseDouble(parts[6]));
                ps.setDouble(7, Double.parseDouble(parts[7]));
                ps.setDouble(8, Double.parseDouble(parts[8]));
                ps.executeUpdate();
                System.out.println("[HO] Inséré depuis " + source + " : " + parts[3]);
            } catch (SQLException e) {//problème d'insertion
                e.printStackTrace();
            }
        };

        channel.basicConsume(DBConfig.QUEUE, true, callback, t -> {});// autoAck = true, callback de livraison, callback de cancel
    }
}