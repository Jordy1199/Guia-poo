package com.natacion.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;

    @FXML
    private void handleIngresar() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        try {
            if (usuario.isEmpty() || contrasena.isEmpty()) {
                throw new Exception("Todos los campos son obligatorios.");
            }

            if (!usuario.equals("admin") || !contrasena.equals("admin123")) {
                throw new Exception("Usuario o contrasenia incorrectos.");
            }

            Parent root = FXMLLoader.load(getClass().getResource("/com/natacion/crud.fxml"));
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Participantes");
            stage.setResizable(true);
            stage.setWidth(1100);
            stage.setHeight(700);

        } catch (Exception e) {
            lblError.setText(e.getMessage());
        }
    }

    @FXML
    private void handleSalir() {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.close();
    }
}