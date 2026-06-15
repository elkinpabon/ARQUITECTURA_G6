package ec.edu.monster.modelo;

import java.io.Serializable;

public class Partido implements Serializable {
    private int codigo;
    private String equipoLocal;
    private String equipoVisita;
    private String fecha;
    private String grupo;
    private String estadio;
    private String ciudad;
    private String pais;
    private String lugar;

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }
    public String getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(String equipoLocal) { this.equipoLocal = equipoLocal; }
    public String getEquipoVisita() { return equipoVisita; }
    public void setEquipoVisita(String equipoVisita) { this.equipoVisita = equipoVisita; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }
    public String getEstadio() { return estadio; }
    public void setEstadio(String estadio) { this.estadio = estadio; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    /** "Mexico vs Sudafrica" */
    public String descripcion() {
        return (equipoLocal == null ? "?" : equipoLocal) + " vs "
                + (equipoVisita == null ? "?" : equipoVisita);
    }

    /** "Estadio Azteca, Ciudad de Mexico (Mexico)" con fallback al campo lugar. */
    public String lugarCompleto() {
        if (estadio != null && !estadio.isEmpty()) {
            StringBuilder sb = new StringBuilder(estadio);
            if (ciudad != null && !ciudad.isEmpty()) sb.append(", ").append(ciudad);
            if (pais != null && !pais.isEmpty()) sb.append(" (").append(pais).append(")");
            return sb.toString();
        }
        return lugar == null ? "" : lugar;
    }

    @Override public String toString() {
        return "[" + codigo + "] " + descripcion() + " - " + fecha;
    }
}
