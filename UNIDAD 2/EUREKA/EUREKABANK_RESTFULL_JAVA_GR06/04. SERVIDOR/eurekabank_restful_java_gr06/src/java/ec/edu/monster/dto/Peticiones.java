package ec.edu.monster.dto;

/** DTOs de entrada (JSON) para los recursos REST. */
public final class Peticiones {

    private Peticiones() { }

    public static class Login {
        private String usuario, clave;
        public String getUsuario() { return usuario; }
        public void setUsuario(String v) { usuario = v; }
        public String getClave() { return clave; }
        public void setClave(String v) { clave = v; }
    }

    public static class Monto {
        private String monto, moneda;
        public String getMonto() { return monto; }
        public void setMonto(String v) { monto = v; }
        public String getMoneda() { return moneda; }
        public void setMoneda(String v) { moneda = v; }
    }

    public static class Transferencia {
        private String origen, destino, monto, moneda;
        public String getOrigen() { return origen; }
        public void setOrigen(String v) { origen = v; }
        public String getDestino() { return destino; }
        public void setDestino(String v) { destino = v; }
        public String getMonto() { return monto; }
        public void setMonto(String v) { monto = v; }
        public String getMoneda() { return moneda; }
        public void setMoneda(String v) { moneda = v; }
    }

    public static class NuevoCliente {
        private String paterno, materno, nombre, dni, ciudad,
                direccion, telefono, email;
        public String getPaterno() { return paterno; }
        public void setPaterno(String v) { paterno = v; }
        public String getMaterno() { return materno; }
        public void setMaterno(String v) { materno = v; }
        public String getNombre() { return nombre; }
        public void setNombre(String v) { nombre = v; }
        public String getDni() { return dni; }
        public void setDni(String v) { dni = v; }
        public String getCiudad() { return ciudad; }
        public void setCiudad(String v) { ciudad = v; }
        public String getDireccion() { return direccion; }
        public void setDireccion(String v) { direccion = v; }
        public String getTelefono() { return telefono; }
        public void setTelefono(String v) { telefono = v; }
        public String getEmail() { return email; }
        public void setEmail(String v) { email = v; }
    }

    public static class NuevaCuenta {
        private String cliente, moneda;
        public String getCliente() { return cliente; }
        public void setCliente(String v) { cliente = v; }
        public String getMoneda() { return moneda; }
        public void setMoneda(String v) { moneda = v; }
    }
}
