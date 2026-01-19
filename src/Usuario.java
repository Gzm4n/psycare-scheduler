import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    protected int idUsuario;
    protected String nombre;
    protected String password;
    protected String rol;

    public Usuario(int idUsuario, String nombre, String password, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
    }

    public boolean login(int id, String pass){
        return this.idUsuario == id && this.password.equals(pass);
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPassword() {
        return password;
    }

    public String getRol() {
        return rol;
    }
}
