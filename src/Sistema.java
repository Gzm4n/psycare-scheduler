import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.*;

public class Sistema {
    private ArrayList<Paciente> listaPacientes;
    private ArrayList<Cita> listaCitas;
    private ArrayList<Recordatorio> listaRecordatorios;
    private ArrayList<Usuario> listaUsuarios;

    public Sistema() {
        listaPacientes = new ArrayList<>();
        listaCitas = new ArrayList<>();
        listaRecordatorios = new ArrayList<>();
        listaUsuarios = new ArrayList<>();
    }

    public void agregarUsuario(Usuario u){
        listaUsuarios.add(u);
    }

    public void agregarRecordatorio(Recordatorio r){
        listaRecordatorios.add(r);
    }

    public boolean existeUsuario(int id){
        for (Usuario u : listaUsuarios){
            if (u.getIdUsuario() == id) return true;
        }
        return false;
    }

    public boolean eliminarUsuario(int id) {
        return listaUsuarios.removeIf(u -> u.getIdUsuario() == id);
    }

    public ArrayList<Usuario> getListaUsuarios(){
        return listaUsuarios;
    }

    public ArrayList<Recordatorio> getListaRecordatorios(){
        return listaRecordatorios;
    }

    public boolean eliminarRecordatorio(int idRec){
        for (Recordatorio r : listaRecordatorios){
            if (r.getIdRecordatorio() == idRec){
                listaRecordatorios.remove(r);
                return true;
            }
        }
        return false;
    }

    public void agregarCita(Cita nueva){
        listaCitas.add(nueva);
    }

    public boolean existeCitaEnHorario(LocalDateTime horario){
        for (Cita c : listaCitas){
            if (c.getFechaHora().equals(horario)) return true;
        }
        return false;
    }

    public ArrayList<Cita> getListaCitas(){
        return listaCitas;
    }

    public void agregarPaciente(Paciente nuevo){
        listaPacientes.add(nuevo);
    }

    public void ordenarPacientes(){
        listaPacientes.sort(Comparator.comparingInt(Paciente::getId));
    }

    public int buscarPacienteBinario(int idBuscado){
        ordenarPacientes();
        int inicio = 0;
        int fin = listaPacientes.size()-1;

        while (inicio<=fin){
            int medio = (inicio+fin)/2;
            int idMedio = listaPacientes.get(medio).getId();
            if (idBuscado==idMedio) return medio;
            else if (idBuscado<idMedio) fin = medio-1;
            else inicio = medio+1;
        }
        return -1;
    }

    public ArrayList<Paciente> getListaPacientes(){
        return listaPacientes;
    }

    public void guardarDatos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("datos_psycare.dat"))) {
            oos.writeObject(listaPacientes);
            oos.writeObject(listaCitas);
            oos.writeObject(listaUsuarios);
            oos.writeObject(listaRecordatorios);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void cargarDatos() {
        File archivo = new File("datos_psycare.dat");
        if (!archivo.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            listaPacientes = (ArrayList<Paciente>) ois.readObject();
            listaCitas = (ArrayList<Cita>) ois.readObject();
            listaUsuarios = (ArrayList<Usuario>) ois.readObject();
            listaRecordatorios = (ArrayList<Recordatorio>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
