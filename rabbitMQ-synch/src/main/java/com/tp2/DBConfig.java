package com.tp2;

public class DBConfig {
    // URLs des bases de données BO1Producer doit lire les données depuis la base BO1, les envoyer vers rabbitMQ, donc il doit
    //se connecter à la base BO1 via cet URL ( de meme pour BO2)
    //HOConsumer doit lire les données depuis RabbitMQ, les ecrire dans la base HO, donc il doit se connecter à la base HO via cet URL
    public static final String HO_URL  = "jdbc:mysql://localhost:3306/db_ho?useSSL=false&allowPublicKeyRetrieval=true";
    public static final String BO1_URL = "jdbc:mysql://localhost:3306/db_bo1?useSSL=false&allowPublicKeyRetrieval=true";
    public static final String BO2_URL = "jdbc:mysql://localhost:3306/db_bo2?useSSL=false&allowPublicKeyRetrieval=true";

    public static final String DB_USER = "root";
    public static final String DB_PASS = "585"; 

    // RabbitMQ
    public static final String MQ_HOST   = "localhost";
    public static final String QUEUE_BO1 = "queue_bo1";
    public static final String QUEUE_BO2 = "queue_bo2";

    //Pour l'approche d'une seuele queue
    public static final String QUEUE   = "queue_ventes";
}