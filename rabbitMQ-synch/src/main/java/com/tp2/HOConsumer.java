package com.tp2;

import com.rabbitmq.client.*;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HOConsumer {
    public static void main(String[] args) throws Exception {
        // 1. Connexion RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(DBConfig.MQ_HOST);
        Connection mqConn = factory.newConnection();

        Channel channel1 = mqConn.createChannel();
        Channel channel2 = mqConn.createChannel();
        channel1.queueDeclare(DBConfig.QUEUE_BO1, true, false, false, null);
        channel2.queueDeclare(DBConfig.QUEUE_BO2, true, false, false, null);

        // 2. Connexion MySQL HO
        java.sql.Connection dbConn = DriverManager.getConnection(
            DBConfig.HO_URL, DBConfig.DB_USER, DBConfig.DB_PASS);

        String sql = "INSERT INTO Product_Sales (Date, Region, Product, Qty, Cost, Amt, Tax, Total) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = dbConn.prepareStatement(sql);

        // 3. Callback commun
        DeliverCallback callback = (tag, delivery) -> {
            String body = new String(delivery.getBody());
            String[] parts = body.split(",");
            String source = delivery.getEnvelope().getRoutingKey(); // nom de la queue
            try {
                ps.setString(1, parts[0]);
                ps.setString(2, parts[1]);
                ps.setString(3, parts[2]);
                ps.setInt(4, Integer.parseInt(parts[3]));
                ps.setDouble(5, Double.parseDouble(parts[4]));
                ps.setDouble(6, Double.parseDouble(parts[5]));
                ps.setDouble(7, Double.parseDouble(parts[6]));
                ps.setDouble(8, Double.parseDouble(parts[7]));
                ps.executeUpdate();
                System.out.println("[HO] Inséré depuis " + source + " : " + parts[2]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        // 4. Écoute des deux queues
        channel1.basicConsume(DBConfig.QUEUE_BO1, true, callback, t -> {});
        channel2.basicConsume(DBConfig.QUEUE_BO2, true, callback, t -> {});

        System.out.println("[HO] En attente de messages sur queue_bo1 et queue_bo2...");
        // Le consumer reste actif indéfiniment
    }
}