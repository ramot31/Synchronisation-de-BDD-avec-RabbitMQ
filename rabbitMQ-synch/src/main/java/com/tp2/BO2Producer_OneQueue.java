package com.tp2;

import com.rabbitmq.client.*;
import java.sql.*;

public class BO2Producer_OneQueue {

    public static void main(String[] args) throws Exception {

        // RabbitMQ setup
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(DBConfig.MQ_HOST);

        com.rabbitmq.client.Connection mqConn = factory.newConnection();
        Channel channel = mqConn.createChannel();

        channel.queueDeclare(DBConfig.QUEUE, true, false, false, null);

        // DB connection BO2
        java.sql.Connection dbConn = DriverManager.getConnection(
            DBConfig.BO2_URL, DBConfig.DB_USER, DBConfig.DB_PASS
        );

        // uniquement les lignes non synchronisées
        PreparedStatement ps = dbConn.prepareStatement(
            "SELECT * FROM Product_Sales WHERE is_synced = 0"
        );

        ResultSet rs = ps.executeQuery();

        // requête de mise à jour après envoi
        PreparedStatement updatePs = dbConn.prepareStatement(
            "UPDATE Product_Sales SET is_synced = 1 WHERE id = ?"
        );

        int count = 0;

        while (rs.next()) {

            int id = rs.getInt("id");

            String msg = "BO2," +
                    rs.getString("Date") + "," +
                    rs.getString("Region") + "," +
                    rs.getString("Product") + "," +
                    rs.getInt("Qty") + "," +
                    rs.getDouble("Cost") + "," +
                    rs.getDouble("Amt") + "," +
                    rs.getDouble("Tax") + "," +
                    rs.getDouble("Total");

            channel.basicPublish("", DBConfig.QUEUE, null, msg.getBytes());
            System.out.println("[BO2] Envoyé : " + msg);

            updatePs.setInt(1, id);
            updatePs.executeUpdate();
            int rows = updatePs.executeUpdate();

            System.out.println("Rows updated = " + rows);       

            count++;
        }

        System.out.println("[BO2] Total : " + count + " messages envoyés.");

        rs.close();
        ps.close();
        updatePs.close();
        dbConn.close();
        channel.close();
        mqConn.close();
    }
}