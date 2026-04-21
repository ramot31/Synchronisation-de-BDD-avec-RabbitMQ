package com.tp2;

import com.rabbitmq.client.*;
import java.sql.*;

public class BO1Producer_OneQueue {
    public static void main(String[] args) throws Exception {

        // RabbitMQ setup
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(DBConfig.MQ_HOST);

        com.rabbitmq.client.Connection mqConn = factory.newConnection();
        Channel channel = mqConn.createChannel();
        channel.queueDeclare(DBConfig.QUEUE, true, false, false, null);//durable, non-exclusive, non-auto-delete, arguments null

        //  DB connection BO1
        java.sql.Connection dbConn = DriverManager.getConnection(
            DBConfig.BO1_URL, DBConfig.DB_USER, DBConfig.DB_PASS
        );

        //  IMPORTANT : on ne prend que les lignes non synchronisées
        PreparedStatement ps = dbConn.prepareStatement(
            "SELECT * FROM Product_Sales WHERE is_synced = 0"
        );

        ResultSet rs = ps.executeQuery();

        //  statement pour update après envoi
        PreparedStatement updatePs = dbConn.prepareStatement(
            "UPDATE Product_Sales SET is_synced = 1 WHERE id = ?"
        );

        int count = 0;

        while (rs.next()) {

            int id = rs.getInt("id"); // clé primaire
// transformer la ligne sql en message texte CSV 
            String msg = "BO1," +
                    rs.getString("Date") + "," +
                    rs.getString("Region") + "," +
                    rs.getString("Product") + "," +
                    rs.getInt("Qty") + "," +
                    rs.getDouble("Cost") + "," +
                    rs.getDouble("Amt") + "," +
                    rs.getDouble("Tax") + "," +
                    rs.getDouble("Total");

            //  envoi vers RabbitMQ
            channel.basicPublish("", DBConfig.QUEUE, null, msg.getBytes());// propriétés null, message en bytes
            System.out.println("[BO1] Envoyé : " + msg);

            //  marquer comme synchronisé
            updatePs.setInt(1, id);
            updatePs.executeUpdate();

            count++;
        }

        System.out.println("[BO1] Total : " + count + " messages envoyés.");

        // fermeture
        rs.close();
        ps.close();
        updatePs.close();
        dbConn.close();
        channel.close();
        mqConn.close();
    }
}