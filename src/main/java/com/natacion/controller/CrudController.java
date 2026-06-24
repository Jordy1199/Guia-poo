package com.natacion.controller;

import com.natacion.conexion.conexion;
import com.natacion.model.Participante;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class CrudController {

    @FXML private TextField txtCedula;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEdad;
    @FXML private TextField txtCorreo;
    @FXML private ComboBox<String> cmbEstadoCivil;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private RadioButton rbMatutina;
    @FXML private RadioButton rbVespertina;
    @FXML private RadioButton rbNocturna;
    @FXML private TextArea txtObservaciones;
    @FXML private Label lblMensaje;
    @FXML private TableView<Participante> tablaParticipantes;
    @FXML private TableColumn<Participante, Integer> colId;
    @FXML private TableColumn<Participante, String> colCedula;
    @FXML private TableColumn<Participante, String> colNombre;
    @FXML private TableColumn<Participante, String> colApellido;
    @FXML private TableColumn<Participante, Integer> colEdad;
    @FXML private TableColumn<Participante, String> colCorreo;
    @FXML private TableColumn<Participante, String> colEstadoCivil;
    @FXML private TableColumn<Participante, String> colJornada;
    @FXML private TableColumn<Participante, String> colCategoria;

    private ToggleGroup grupoJornada;
    private ObservableList<Participante> listaParticipantes;
    private int idSeleccionado = -1;

    @FXML
    public void initialize() {
        grupoJornada = new ToggleGroup();
        rbMatutina.setToggleGroup(grupoJornada);
        rbVespertina.setToggleGroup(grupoJornada);
        rbNocturna.setToggleGroup(grupoJornada);

        cmbEstadoCivil.setItems(FXCollections.observableArrayList(
                "Soltero", "Casado", "Divorciado", "Viudo"
        ));

        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Infantil", "Junior", "Juvenil", "Adulto", "Master"
        ));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colEstadoCivil.setCellValueFactory(new PropertyValueFactory<>("estadoCivil"));
        colJornada.setCellValueFactory(new PropertyValueFactory<>("jornada"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        listaParticipantes = FXCollections.observableArrayList();
        tablaParticipantes.setItems(listaParticipantes);

        cargarTabla();

        tablaParticipantes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        cargarFormulario(newVal);
                    }
                }
        );
    }

    private void cargarTabla() {
        listaParticipantes.clear();
        try {
            Connection con = conexion.getInstancia().getConnection();
            String sql = "SELECT * FROM participantes";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                listaParticipantes.add(new Participante(
                        rs.getInt("id"),
                        rs.getString("cedula"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getInt("edad"),
                        rs.getString("correo"),
                        rs.getString("estado_civil"),
                        rs.getString("jornada"),
                        rs.getString("categoria"),
                        rs.getString("observaciones")
                ));
            }
        } catch (SQLException e) {
            mostrarError("Error al cargar datos: " + e.getMessage());
        }
    }

    private void cargarFormulario(Participante p) {
        idSeleccionado = p.getId();
        txtCedula.setText(p.getCedula());
        txtNombre.setText(p.getNombre());
        txtApellido.setText(p.getApellido());
        txtEdad.setText(String.valueOf(p.getEdad()));
        txtCorreo.setText(p.getCorreo());
        cmbEstadoCivil.setValue(p.getEstadoCivil());
        cmbCategoria.setValue(p.getCategoria());
        txtObservaciones.setText(p.getObservaciones());

        switch (p.getJornada()) {
            case "Matutina" -> rbMatutina.setSelected(true);
            case "Vespertina" -> rbVespertina.setSelected(true);
            case "Nocturna" -> rbNocturna.setSelected(true);
        }

        lblMensaje.setText("");
    }

    private String validar() {
        if (txtCedula.getText().trim().isEmpty() ||
                txtNombre.getText().trim().isEmpty() ||
                txtApellido.getText().trim().isEmpty() ||
                txtEdad.getText().trim().isEmpty() ||
                txtCorreo.getText().trim().isEmpty() ||
                cmbEstadoCivil.getValue() == null ||
                cmbCategoria.getValue() == null ||
                grupoJornada.getSelectedToggle() == null) {
            return "Todos los campos son obligatorios.";
        }

        if (!txtCedula.getText().trim().matches("\\d+")) {
            return "La cedula debe contener solo números.";
        }

        try {
            int edad = Integer.parseInt(txtEdad.getText().trim());
            if (edad <= 5) {
                return "La edad debe ser mayor a 5 anios.";
            }
        } catch (NumberFormatException e) {
            return "La edad debe ser un número valido.";
        }

        if (!txtCorreo.getText().trim().contains("@")) {
            return "El correo debe contener @.";
        }

        return null;
    }

    @FXML
    private void handleGuardar() {
        String error = validar();
        if (error != null) {
            mostrarError(error);
            return;
        }

        try {
            Connection con = conexion.getInstancia().getConnection();

            String sqlDuplicado = "SELECT id FROM participantes WHERE correo = ? OR cedula = ?";
            PreparedStatement psDup = con.prepareStatement(sqlDuplicado);
            psDup.setString(1, txtCorreo.getText().trim());
            psDup.setString(2, txtCedula.getText().trim());
            ResultSet rsDup = psDup.executeQuery();
            if (rsDup.next()) {
                mostrarError("Ya existe un participante con esa cedula o correo.");
                return;
            }

            String sql = "INSERT INTO participantes (cedula, nombre, apellido, edad, correo, estado_civil, jornada, categoria, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, txtCedula.getText().trim());
            ps.setString(2, txtNombre.getText().trim());
            ps.setString(3, txtApellido.getText().trim());
            ps.setInt(4, Integer.parseInt(txtEdad.getText().trim()));
            ps.setString(5, txtCorreo.getText().trim());
            ps.setString(6, cmbEstadoCivil.getValue());
            ps.setString(7, obtenerJornada());
            ps.setString(8, cmbCategoria.getValue());
            ps.setString(9, txtObservaciones.getText().trim());
            ps.executeUpdate();

            mostrarExito("Participante registrado correctamente.");
            cargarTabla();
            handleLimpiar();

        } catch (SQLException e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void handleActualizar() {
        if (idSeleccionado == -1) {
            mostrarError("Selecciona un participante de la tabla para actualizar.");
            return;
        }

        String error = validar();
        if (error != null) {
            mostrarError(error);
            return;
        }

        try {
            Connection con = conexion.getInstancia().getConnection();

            String sqlDuplicado = "SELECT id FROM participantes WHERE (correo = ? OR cedula = ?) AND id != ?";
            PreparedStatement psDup = con.prepareStatement(sqlDuplicado);
            psDup.setString(1, txtCorreo.getText().trim());
            psDup.setString(2, txtCedula.getText().trim());
            psDup.setInt(3, idSeleccionado);
            ResultSet rsDup = psDup.executeQuery();
            if (rsDup.next()) {
                mostrarError("Ya existe otro participante con esa cedula o correo.");
                return;
            }

            String sql = "UPDATE participantes SET cedula=?, nombre=?, apellido=?, edad=?, correo=?, estado_civil=?, jornada=?, categoria=?, observaciones=? WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, txtCedula.getText().trim());
            ps.setString(2, txtNombre.getText().trim());
            ps.setString(3, txtApellido.getText().trim());
            ps.setInt(4, Integer.parseInt(txtEdad.getText().trim()));
            ps.setString(5, txtCorreo.getText().trim());
            ps.setString(6, cmbEstadoCivil.getValue());
            ps.setString(7, obtenerJornada());
            ps.setString(8, cmbCategoria.getValue());
            ps.setString(9, txtObservaciones.getText().trim());
            ps.setInt(10, idSeleccionado);
            ps.executeUpdate();

            mostrarExito("Participante actualizado correctamente.");
            cargarTabla();
            handleLimpiar();

        } catch (SQLException e) {
            mostrarError("Error al actualizar: " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminar() {
        if (idSeleccionado == -1) {
            mostrarError("Selecciona un participante de la tabla para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminacion");
        confirmacion.setHeaderText("Estas seguro");
        confirmacion.setContentText("Se eliminara el participante seleccionado.");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    Connection con = conexion.getInstancia().getConnection();
                    String sql = "DELETE FROM participantes WHERE id = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, idSeleccionado);
                    ps.executeUpdate();

                    mostrarExito("Participante eliminado correctamente.");
                    cargarTabla();
                    handleLimpiar();

                } catch (SQLException e) {
                    mostrarError("Error al eliminar: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleLimpiar() {
        txtCedula.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtEdad.clear();
        txtCorreo.clear();
        txtObservaciones.clear();
        cmbEstadoCivil.setValue(null);
        cmbCategoria.setValue(null);
        grupoJornada.selectToggle(null);
        idSeleccionado = -1;
        lblMensaje.setText("");
    }

    private String obtenerJornada() {
        RadioButton seleccionado = (RadioButton) grupoJornada.getSelectedToggle();
        return seleccionado.getText();
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 11px;");
        lblMensaje.setText(mensaje);
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.setStyle("-fx-text-fill: #00ff88; -fx-font-size: 11px;");
        lblMensaje.setText(mensaje);
    }
}