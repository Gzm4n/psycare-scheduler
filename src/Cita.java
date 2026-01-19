import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Cita implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idCita;
    private int idPaciente; //Relacion mediante id de paciente
    private LocalDateTime fechaHora; //Gestionar dia y hora
    private String motivo; //primera cita, seguimiento, emergencia
    private String estado; //pendiente, completada, cancelada

    public Cita(int idCita, int idPaciente, LocalDateTime fechaHora, String motivo) {
        this.idCita = idCita;
        this.idPaciente = idPaciente;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = "Pendiente";
    }

    // Getters


    public int getIdCita() {
        return idCita;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
