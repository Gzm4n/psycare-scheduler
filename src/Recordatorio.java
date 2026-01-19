import java.io.Serializable;

public class Recordatorio implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idRecordatorio;
    private int idCita;
    private String medio;
    private String anticipacion;
    private boolean enviado;

    public Recordatorio(int idRecordatorio, int idCita, String medio, String anticipacion) {
        this.idRecordatorio = idRecordatorio;
        this.idCita = idCita;
        this.medio = medio;
        this.anticipacion = anticipacion;
        this.enviado = false;
    }

    public int getIdRecordatorio() {
        return idRecordatorio;
    }

    public int getIdCita() {
        return idCita;
    }

    public String getMedio() {
        return medio;
    }

    public String getAnticipacion() {
        return anticipacion;
    }

    public boolean isEnviado() {
        return enviado;
    }

    public void marcarComoEnviado(){
        enviado = true;
    }
}
