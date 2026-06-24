package com.natacion.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexion {

    private static conexion instancia;
    private Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/natacion_db";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "root";

    private conexion() {
        try {
            connection = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("Conexion exitosa a MySQL");
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
    }

    public static conexion getInstancia() {
        if (instancia == null) {
            instancia = new conexion();
        }
        return instancia;
    }

    public Connection getConnection() {
        return connection;
    }
}