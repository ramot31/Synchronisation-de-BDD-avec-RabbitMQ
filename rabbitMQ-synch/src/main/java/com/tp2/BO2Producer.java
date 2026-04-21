package com.tp2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BO2Producer {
    public static void main(String[] args) throws Exception {

        // 1. Connexion RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(DBConfig.MQ_HOST);
        Connection mqConn = factory.newConnection();
        Channel channel = mqConn.createChannel();
        channel.queueDeclare(DBConfig.QUEUE_BO2, true, false, false, null);

        // 2. Connexion MySQL BO2
        java.sql.Connection dbConn = DriverManager.getConnection(
            DBConfig.BO2_URL, DBConfig.DB_USER, DBConfig.DB_PASS);

        // 3. Lire uniquement les lignes non synchronisées
        PreparedStatement ps = dbConn.prepareStatement(
            "SELECT * FROM Product_Sales WHERE is_synced = 0"
        );
        ResultSet rs = ps.executeQuery();

        // 4. Préparer la mise à jour après envoi
        PreparedStatement updatePs = dbConn.prepareStatement(
            "UPDATE Product_Sales SET is_synced = 1 WHERE id = ?"
        );

        int count = 0;

        // 5. Publication des messages
        while (rs.next()) {

            int id = rs.getInt("id");

            String msg = rs.getString("Date")    + "," +
                         rs.getString("Region")  + "," +
                         rs.getString("Product") + "," +
                         rs.getInt("Qty")        + "," +
                         rs.getDouble("Cost")    + "," +
                         rs.getDouble("Amt")     + "," +
                         rs.getDouble("Tax")     + "," +
                         rs.getDouble("Total");

            channel.basicPublish("", DBConfig.QUEUE_BO2, null, msg.getBytes());
            System.out.println("[BO2] Envoyé : " + msg);

            // Marquer comme synchronisé
            updatePs.setInt(1, id);
            updatePs.executeUpdate();

            count++;
        }

        System.out.println("[BO2] Total messages envoyés : " + count);

        // Fermeture des ressources
        rs.close();
        ps.close();
        updatePs.close();
        dbConn.close();
        channel.close();
        mqConn.close();
    }
}