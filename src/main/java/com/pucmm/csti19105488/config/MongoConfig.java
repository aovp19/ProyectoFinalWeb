package com.pucmm.csti19105488.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import.dev.morphia.Morphia;
import dev.morphia.Datastore;
import dev.morphia.Morphia;

import java.io.IOException;
import java.util.Properties;

public class MongoConfig {

    private static MongoConfig instancia;
    private Datastore datastore;

    private MongoConfig() {
        try {
            // Se leen las configuraciones establecidas en config.properties
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream("/config.properties"));

            // Se conecta a Atlas
            MongoClient mongoClient = MongoClients.create(props.getProperty("mongo.uri"));

            // Se crea el Datastore de Morphia
            this.datastore = Morphia.createDatastore(mongoClient, props.getProperty("mongo.database"));

            // Se le especifica a Morphia donde estan las clases modelo (@Entity)
            this.datastore.getMapper().mapPackage("com.pucmm.csti19105488.model");

            // Se indexan los datos para facilitar la busqueda mas adelante, por ahora no es relevante
            // pero en un escenario con muchos datos es más rápido
            this.datastore.ensureIndexes();

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
