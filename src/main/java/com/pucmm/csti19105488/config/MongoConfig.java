package com.pucmm.csti19105488.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Morphia;
import dev.morphia.Datastore;

import java.io.IOException;
import java.util.Properties;

public class MongoConfig {

    private static MongoConfig instancia;
    private Datastore datastore;
    private MongoClient mongoClient;

    private MongoConfig() {
        try {
            // Se leen las configuraciones establecidas en config.properties
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream("/config.properties"));

            // Se conecta a Atlas
            MongoClient mongoClient = MongoClients.create(props.getProperty("mongo.uri"));

            // Se crea el Datastore de Morphia
            this.datastore = Morphia.createDatastore(mongoClient, props.getProperty("mongo.database"));

            // Cerrar la conexion al apagar la aplicacion
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                this.mongoClient.close();
                System.out.println("Conexion a MongoDB cerrada correctamente.");
            }));


        }   catch (IOException e) {
            throw new RuntimeException("Error al cargar config.properties", e);
        }
    }

    // Singleton
    public static MongoConfig getInstance() {
        if (instancia == null) {
            instancia = new MongoConfig();
        }
        return instancia;
    }

    public Datastore getDatastore() {
        return datastore;
    }
}
