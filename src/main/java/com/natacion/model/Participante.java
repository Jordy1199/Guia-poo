package com.natacion.model;

import javafx.beans.property.*;

public class Participante {

    private IntegerProperty id;
    private StringProperty cedula;
    private StringProperty nombre;
    private StringProperty apellido;
    private IntegerProperty edad;
    private StringProperty correo;
    private StringProperty estadoCivil;
    private StringProperty jornada;
    private StringProperty categoria;
    private StringProperty observaciones;

    public Participante(int id, String cedula, String nombre, String apellido,
                        int edad, String correo, String estadoCivil,
                        String jornada, String categoria, String observaciones) {
        this.id = new SimpleIntegerProperty(id);
        this.cedula = new SimpleStringProperty(cedula);
        this.nombre = new SimpleStringProperty(nombre);
        this.apellido = new SimpleStringProperty(apellido);
        this.edad = new SimpleIntegerProperty(edad);
        this.correo = new SimpleStringProperty(correo);
        this.estadoCivil = new SimpleStringProperty(estadoCivil);
        this.jornada = new SimpleStringProperty(jornada);
        this.categoria = new SimpleStringProperty(categoria);
        this.observaciones = new SimpleStringProperty(observaciones);
    }

    public int getId() { return id.get(); }
    public String getCedula() { return cedula.get(); }
    public String getNombre() { return nombre.get(); }
    public String getApellido() { return apellido.get(); }
    public int getEdad() { return edad.get(); }
    public String getCorreo() { return correo.get(); }
    public String getEstadoCivil() { return estadoCivil.get(); }
    public String getJornada() { return jornada.get(); }
    public String getCategoria() { return categoria.get(); }
    public String getObservaciones() { return observaciones.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty cedulaProperty() { return cedula; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty apellidoProperty() { return apellido; }
    public IntegerProperty edadProperty() { return edad; }
    public StringProperty correoProperty() { return correo; }
    public StringProperty estadoCivilProperty() { return estadoCivil; }
    public StringProperty jornadaProperty() { return jornada; }
    public StringProperty categoriaProperty() { return categoria; }
    public StringProperty observacionesProperty() { return observaciones; }
}