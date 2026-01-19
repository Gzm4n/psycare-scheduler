import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class Ventana extends JFrame {
    private JTabbedPane tabbedPane;
    private Sistema sistema = new Sistema();
    private JPanel calPanel;

    //Labels
    private JLabel lblDiaSeleccionado = new JLabel("Seleccione un día");
    private JLabel lblTotalCitas = new JLabel("0");
    private JLabel lblAsistencias = new JLabel("0");
    private JLabel lblCanceladas = new JLabel("0");

    //Tablas
    private DefaultTableModel modeloTablaReportes;
    private DefaultTableModel modeloTablaAdmin;
    private DefaultTableModel modeloTablaPacientes;
    private JTable tablaReporte;

    //DFL
    private DefaultListModel<String> modeloListaRecordatorios = new DefaultListModel<>();
    private DefaultListModel<String> modeloListaCitas = new DefaultListModel<>();


    public Ventana() {
        sistema.cargarDatos();
        // Look & Feel: Hace que se vea como Windows/Mac y no como Java antiguo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configuración básica
        setTitle("PsyCare Scheduler");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana
        setLayout(new BorderLayout());

        // Inicializar el TabbedPane
        tabbedPane = new JTabbedPane();

        // Añadimos las pestañas principales
        tabbedPane.addTab("Gestión Pacientes", crearPanelPacientes());
        tabbedPane.addTab("Gestión Citas", crearPanelCitas());
        tabbedPane.addTab("Recordatorios", crearPanelRecordatorios());
        tabbedPane.addTab("Reportes", crearPanelReportes());
        tabbedPane.addTab("Administración", crearPanelAdministracion());

        cargarTablasAlInicio();

        tabbedPane.addChangeListener(e -> {
            // Si la pestaña seleccionada es la de Reportes (índice 3 en tu caso)
            if (tabbedPane.getSelectedIndex() == 3) {
                calcularEstadisticas();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                sistema.guardarDatos(); // Guardar al cerrar
            }
        });
    }

    private void cargarTablasAlInicio() {
        // Cargar Pacientes
        if (modeloTablaPacientes != null) {
            modeloTablaPacientes.setRowCount(0);
            for (Paciente p : sistema.getListaPacientes()) {
                modeloTablaPacientes.addRow(new Object[]{p.getId(), p.getNombre(), p.getTelefono(), p.getEmail()});
            }
        }

        // Cargar Usuarios
        if (modeloTablaAdmin != null) {
            modeloTablaAdmin.setRowCount(0);
            for (Usuario u : sistema.getListaUsuarios()) {
                modeloTablaAdmin.addRow(new Object[]{u.getIdUsuario(), u.getNombre(), u.getRol()});
            }
        }

        // Cargar Recordatorios
        modeloListaRecordatorios.clear();
        for (Recordatorio r : sistema.getListaRecordatorios()) {
            String entry = "🔔 ID:" + r.getIdRecordatorio() + " - Cita:" + r.getIdCita() + " - " + r.getMedio() + " (" + r.getAnticipacion() + ")";
            modeloListaRecordatorios.addElement(entry);
        }
    }
    private JPanel crearPanelPacientes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //============================================================================================================
        // --- FORMULARIO IZQUIERDO ---
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Registro de Pacientes"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Margen entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Inicialización de campos con tamaño controlado
        JTextField txtId = new JTextField(12);
        JTextField txtNombre = new JTextField(12);
        JTextField txtTelefono = new JTextField(12);
        JTextField txtEmail = new JTextField(12);

        // Panel de Fecha (Más compacto)
        JPanel panelFecha = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        JComboBox<String> cbDia = new JComboBox<>(generarNumeros(1, 31));
        JComboBox<String> cbMes = new JComboBox<>(new String[]{"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"});
        JTextField txtAnio = new JTextField("2026", 4);
        panelFecha.add(cbDia); panelFecha.add(cbMes); panelFecha.add(txtAnio);

        // Ubicación de etiquetas y campos
        gbc.gridx = 0; gbc.gridy = 0; panelForm.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; panelForm.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panelForm.add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridx = 1; panelForm.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panelForm.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1; panelForm.add(txtTelefono, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panelForm.add(new JLabel("E-mail:"), gbc);
        gbc.gridx = 1; panelForm.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 4; panelForm.add(new JLabel("Fecha Nacimiento:"), gbc);
        gbc.gridx = 1; panelForm.add(panelFecha, gbc);

        // Botón Registrar
        JButton btnRegistrar = new JButton("Registrar Paciente");
        btnRegistrar.setPreferredSize(new Dimension(100, 35));
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        panelForm.add(btnRegistrar, gbc);

        btnRegistrar.addActionListener(e -> {
            try {
                // Capturar datos de los campos de texto
                int id = Integer.parseInt(txtId.getText());
                String nombre = txtNombre.getText();
                String telf = txtTelefono.getText();
                String email = txtEmail.getText();

                // Construir la fecha
                int dia = Integer.parseInt((String)cbDia.getSelectedItem());
                int mes = cbMes.getSelectedIndex() + 1;
                int anio = Integer.parseInt(txtAnio.getText());

                if (anio < 1940 || anio > 2021) {
                    JOptionPane.showMessageDialog(null,"Error en la fecha de nacimiento");
                    return;
                }

                LocalDate fechaNac = LocalDate.of(anio, mes, dia);


                // Validar
                if(sistema.buscarPacienteBinario(id) != -1) {
                    JOptionPane.showMessageDialog(this, "Error: El ID " + id + " ya está registrado.");
                    return;
                }

                // Guardar
                Paciente nuevo = new Paciente(id, nombre, telf, email, fechaNac);
                sistema.agregarPaciente(nuevo);

                // Actualizar la interfaz visual
                modeloTablaPacientes.addRow(new Object[]{id, nombre, telf, email});

                //Limpiar campos
                txtId.setText("");
                txtNombre.setText("");
                txtTelefono.setText("");
                txtEmail.setText("");
                cbDia.setSelectedIndex(0);
                cbMes.setSelectedIndex(0);
                txtAnio.setText("2026");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, verifica los datos numéricos.");
            }
        });

        //============================================================================================================
        // --- TABLA DERECHA ---
        String[] columnas = {"ID", "Nombre", "Teléfono", "Email"};
        modeloTablaPacientes = new DefaultTableModel(columnas, 0);
        JTable tablaPacientes = new JTable(modeloTablaPacientes);
        JScrollPane scrollTabla = new JScrollPane(tablaPacientes);

        // Boton eliminar
        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        btnEliminar.setBackground(new Color(255, 100, 100));
        btnEliminar.setPreferredSize(new Dimension(100, 35));// Un color rojizo para indicar precaución
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 8, 8, 8);
        panelForm.add(btnEliminar, gbc);

        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tablaPacientes.getSelectedRow();

            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un paciente de la tabla.");
                return;
            }

            // Extraemos el ID de la primera columna de la fila seleccionada
            int idEliminar = (int) modeloTablaPacientes.getValueAt(filaSeleccionada, 0);
            String nombre = (String) modeloTablaPacientes.getValueAt(filaSeleccionada, 1);

            // Ventana de confirmación para mayor seguridad
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que deseas eliminar al paciente: " + nombre + "?",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                // Eliminar del sistema
                int indiceEnSistema=sistema.buscarPacienteBinario(idEliminar);
                if (indiceEnSistema != -1) {
                    // Eliminar de la interfaz
                    sistema.getListaPacientes().remove(indiceEnSistema);
                    modeloTablaPacientes.removeRow(filaSeleccionada);
                    JOptionPane.showMessageDialog(this, "Paciente eliminado con éxito.");
                }
            }
        });

        panelPrincipal.add(panelForm, BorderLayout.WEST);
        panelPrincipal.add(scrollTabla, BorderLayout.CENTER);

        return panelPrincipal;
    }

    // Metodo para cbobox
    private String[] generarNumeros(int inicio, int fin) {
        String[] nums = new String[fin - inicio + 1];
        for (int i = 0; i <= fin - inicio; i++) nums[i] = String.valueOf(inicio + i);
        return nums;
    }

    private JPanel crearPanelCitas() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- ENCABEZADO ---
        JPanel header = new JPanel(new BorderLayout());
        JLabel lblMes = new JLabel("ENERO 2026", SwingConstants.CENTER);
        lblMes.setFont(new Font("Arial", Font.BOLD, 18));
        header.add(lblMes, BorderLayout.CENTER);

        // --- CALENDARIO ---
        calPanel = new JPanel(new GridLayout(0, 7));
        String[] diasSemana = {"Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"};
        for (String d : diasSemana) {
            calPanel.add(new JLabel(d, SwingConstants.CENTER));
        }

        for (int i = 1; i <= 31; i++) {
            int diaActual = i;

            int contador = 0;
            for (Cita c : sistema.getListaCitas()) {
                if (c.getFechaHora().getDayOfMonth() == diaActual &&
                        "Pendiente".equals(c.getEstado())) {
                    contador++;
                }
            }

            String textoBoton = (contador > 0) ? i + " (" + contador + ")" : String.valueOf(i);
            JButton btnDia = new JButton(textoBoton);

            if (contador > 0) btnDia.setBackground(new Color(255, 230, 150));
            else btnDia.setBackground(Color.WHITE);
            btnDia.addActionListener(e -> {
                lblDiaSeleccionado.setText("Día seleccionado: " + diaActual);
                actualizarListaCitasDelDia(diaActual);
            });

            calPanel.add(btnDia);
        }

        // --- PANEL DE DETALLES (Derecha) ---
        JPanel detalles = new JPanel(new BorderLayout(5, 5));
        detalles.setBorder(BorderFactory.createTitledBorder("Citas"));
        detalles.setPreferredSize(new Dimension(250, 0));

        JList<String> listaVisual = new JList<>(modeloListaCitas);
        detalles.add(lblDiaSeleccionado, BorderLayout.NORTH);
        detalles.add(new JScrollPane(listaVisual), BorderLayout.CENTER);

        JButton btnAgendar = new JButton("Agendar Nueva Cita");
        btnAgendar.addActionListener(e -> abrirVentanaAgendar()); // Paso siguiente
        detalles.add(btnAgendar, BorderLayout.SOUTH);

        panelPrincipal.add(header, BorderLayout.NORTH);
        panelPrincipal.add(calPanel, BorderLayout.CENTER);
        panelPrincipal.add(detalles, BorderLayout.EAST);

        return panelPrincipal;
    }

    private void actualizarListaCitasDelDia(int dia) {
        modeloListaCitas.clear();
        for (Cita c : sistema.getListaCitas()) {
            // Solo mostramos citas que sigan "Pendientes"
            if (c.getFechaHora().getDayOfMonth() == dia &&
                    c.getFechaHora().getMonthValue() == 1 &&
                    "Pendiente".equals(c.getEstado())) {

                int idx = sistema.buscarPacienteBinario(c.getIdPaciente());
                String nombrePac = (idx != -1) ? sistema.getListaPacientes().get(idx).getNombre() : "Desconocido";

                modeloListaCitas.addElement(c.getFechaHora().getHour() + ":00 - " + nombrePac + " - ID: " + c.getIdCita() + " - Fecha: " + c.getFechaHora());
            }
        }
        if (modeloListaCitas.isEmpty()) {
            modeloListaCitas.addElement("No hay citas pendientes este día.");
        }
    }

    private void abrirVentanaAgendar() {
        JTextField txtIdPac = new JTextField();
        JTextField txtDia = new JTextField();
        JComboBox<String> cbHora = new JComboBox<>(new String[]{"08", "09", "10", "14", "15", "16"});
        JLabel lblNombrePaciente = new JLabel("Paciente: (No verificado)");
        lblNombrePaciente.setFont(new Font("Arial", Font.ITALIC, 12));

        JButton btnVerificar = new JButton("Verificar ID");

        // Lógica para buscar el nombre antes de agendar
        btnVerificar.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtIdPac.getText());
                int idx = sistema.buscarPacienteBinario(id); // Uso de búsqueda binaria
                if (idx != -1) {
                    String nombre = sistema.getListaPacientes().get(idx).getNombre();
                    lblNombrePaciente.setText("Paciente: " + nombre);
                    lblNombrePaciente.setForeground(new Color(0, 150, 0));
                } else {
                    lblNombrePaciente.setText("Paciente: No encontrado");
                    lblNombrePaciente.setForeground(Color.RED);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Ingrese un ID numérico.");
            }
        });

        Object[] message = {
                "ID Paciente:", txtIdPac,
                btnVerificar,
                lblNombrePaciente,
                "Día del mes (1-31):", txtDia,
                "Hora:", cbHora
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Nueva Cita", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int idPac = Integer.parseInt(txtIdPac.getText());
                int dia = Integer.parseInt(txtDia.getText());
                int hora = Integer.parseInt((String)cbHora.getSelectedItem());

                if (sistema.buscarPacienteBinario(idPac) == -1) {
                    JOptionPane.showMessageDialog(this, "El paciente no existe.");
                    return;
                }

                LocalDateTime fecha = LocalDateTime.of(2026, 1, dia, hora, 0);
                LocalDate soloFecha = LocalDate.of(2026, 1, dia);

                // Validación de colisión de horarios
                if (sistema.existeCitaEnHorario(fecha)) {
                    JOptionPane.showMessageDialog(this, "Horario ya ocupado.");
                    return;
                }

                Cita nueva = new Cita(sistema.getListaCitas().size() + 1, idPac, fecha, "Consulta", soloFecha);
                sistema.agregarCita(nueva);

                refrescarCalendario(); // Actualiza los números del calendario
                actualizarListaCitasDelDia(dia);
                JOptionPane.showMessageDialog(this, "Cita agendada. ID: " + nueva.getIdCita());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Datos inválidos.");
            }
        }
    }

    private JPanel crearPanelAdministracion() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- FORMULARIO IZQUIERDO ---
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Registro de Personal"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtID = new JTextField(15);
        JTextField txtNombre = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        JComboBox<String> cbRol = new JComboBox<>(new String[]{"Psicólogo", "Asistente"});
        JButton btnRegistrar = new JButton("Registrar Usuario");

        // Añadir componentes al layout
        gbc.gridx = 0; gbc.gridy = 0; panelForm.add(new JLabel("ID Usuario:"), gbc);
        gbc.gridx = 1; panelForm.add(txtID, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panelForm.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; panelForm.add(txtNombre, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelForm.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; panelForm.add(txtPass, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panelForm.add(new JLabel("Rol:"), gbc);
        gbc.gridx = 1; panelForm.add(cbRol, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; panelForm.add(btnRegistrar, gbc);

        // --- TABLA DERECHA ---
        String[] columnas = {"ID", "Nombre", "Rol"};
        modeloTablaAdmin = new DefaultTableModel(columnas, 0);
        JTable tablaUsuarios = new JTable(modeloTablaAdmin);
        JScrollPane scrollTabla = new JScrollPane(tablaUsuarios);

        // --- LÓGICA DE REGISTRO ---
        btnRegistrar.addActionListener(e -> {
            String idStr = txtID.getText();
            String nombre = txtNombre.getText();
            String pass = new String(txtPass.getPassword());
            String rol = (String) cbRol.getSelectedItem();

            if (idStr.isEmpty() || nombre.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos.");
                return;
            }

            try {
                int id = Integer.parseInt(idStr);

                // Validar si ya existe el usuario en el sistema
                if (sistema.existeUsuario(id)) {
                    JOptionPane.showMessageDialog(this, "El ID " + id + " ya está registrado.");
                    return;
                }

                // Crear y guardar el objeto Usuario
                Usuario nuevo = new Usuario(id, nombre, pass, rol);
                sistema.agregarUsuario(nuevo);

                // Actualizar interfaz visual
                modeloTablaAdmin.addRow(new Object[]{id, nombre, rol});

                // Limpiar campos por seguridad
                txtID.setText("");
                txtNombre.setText("");
                txtPass.setText("");
                JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente.");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El ID debe ser un número.");
            }
        });

        JButton btnEliminarUsuario = new JButton("Eliminar Usuario");
        btnEliminarUsuario.setBackground(new Color(255, 100, 100)); // Color de alerta

        // Ubicación botón en la interfaz
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panelForm.add(btnEliminarUsuario, gbc);

        btnEliminarUsuario.addActionListener(e -> {
            int filaSeleccionada = tablaUsuarios.getSelectedRow();

            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.");
                return;
            }

            // Obtenemos el ID de la primera columna
            int idEliminar = (int) modeloTablaAdmin.getValueAt(filaSeleccionada, 0);
            String nombre = (String) modeloTablaAdmin.getValueAt(filaSeleccionada, 1);

            int confirmar = JOptionPane.showConfirmDialog(this,
                    "¿Desea eliminar el acceso al sistema para: " + nombre + "?",
                    "Confirmar acción", JOptionPane.YES_NO_OPTION);

            if (confirmar == JOptionPane.YES_OPTION) {
                if (sistema.eliminarUsuario(idEliminar)) {
                    modeloTablaAdmin.removeRow(filaSeleccionada);
                    JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.");
                }
            }
        });

        panelPrincipal.add(panelForm, BorderLayout.WEST);
        panelPrincipal.add(scrollTabla, BorderLayout.CENTER);

        return panelPrincipal;
    }

    private JPanel crearPanelReportes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- INDICADORES ---
        JPanel panelCards = new JPanel(new GridLayout(1, 3, 10, 10));
        panelCards.add(crearCardInformativa("Citas Totales", lblTotalCitas, Color.BLUE));
        panelCards.add(crearCardInformativa("Asistencias", lblAsistencias, new Color(0, 150, 0)));
        panelCards.add(crearCardInformativa("Canceladas", lblCanceladas, Color.RED));

        // --- TABLA DE HISTORIAL ---
        String[] columnas = {"ID Cita", "Paciente", "Fecha", "Estado"};
        modeloTablaReportes = new DefaultTableModel(columnas, 0);
        tablaReporte = new JTable(modeloTablaReportes);
        JScrollPane scroll = new JScrollPane(tablaReporte);

        // --- ACCIONES ---
        JButton btnActualizar = new JButton("Actualizar Datos");
        btnActualizar.addActionListener(e -> calcularEstadisticas());

        panelPrincipal.add(panelCards, BorderLayout.NORTH);
        panelPrincipal.add(scroll, BorderLayout.CENTER);
        panelPrincipal.add(btnActualizar, BorderLayout.SOUTH);

        // Dentro de crearPanelReportes, en la sección de acciones:
        JButton btnCompletar = new JButton("Marcar como Completada");
        JButton btnCancelar = new JButton("Marcar como Cancelada");

        btnCompletar.addActionListener(e -> actualizarEstadoCita("Completada"));
        btnCancelar.addActionListener(e -> actualizarEstadoCita("Cancelada"));

        JPanel panelAccionesCita = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAccionesCita.add(btnCompletar);
        panelAccionesCita.add(btnCancelar);

        panelPrincipal.add(panelAccionesCita, BorderLayout.SOUTH);

        JButton btnExportar = new JButton("Exportar Reporte a PDF/Texto");
        btnExportar.addActionListener(e -> exportarReporte());
        panelAccionesCita.add(btnExportar);

        return panelPrincipal;
    }

    private void exportarReporte() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("Reporte_Citas.txt"))) {
            writer.println("========================================");
            writer.println("   REPORTE DE CITAS - PSYCARE SCHEDULER");
            writer.println("========================================");
            writer.println("Fecha de emisión: " + LocalDate.now());
            writer.println("Totales: " + lblTotalCitas.getText());
            writer.println("Asistencias: " + lblAsistencias.getText());
            writer.println("Canceladas: " + lblCanceladas.getText());
            writer.println("\nDETALLE DE CITAS:");
            writer.println("ID | Paciente | Fecha | Estado");

            for (Cita c : sistema.getListaCitas()) {
                int idx = sistema.buscarPacienteBinario(c.getIdPaciente());
                String nombre = (idx != -1) ? sistema.getListaPacientes().get(idx).getNombre() : "N/A";
                writer.printf("%d | %s | %s | %s%n", c.getIdCita(), nombre, c.getFechaHora().toLocalDate(), c.getEstado());
            }

            JOptionPane.showMessageDialog(this, "Reporte generado como 'Reporte_Citas.txt'.\nPuedes imprimirlo como PDF desde tu editor.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al generar reporte.");
        }
    }

    private void actualizarEstadoCita(String nuevoEstado) {
        int fila = tablaReporte.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita de la tabla.");
            return;
        }

        // Obtenemos el ID de la cita de la primera columna
        int idCita = (int) modeloTablaReportes.getValueAt(fila, 0);

        // Buscamos la cita en el sistema
        for (Cita c : sistema.getListaCitas()) {
            if (c.getIdCita() == idCita) {
                c.setEstado(nuevoEstado); // Actualizamos el objeto real
                JOptionPane.showMessageDialog(this, "Cita actualizada a: " + nuevoEstado);
                break;
            }
        }

        calcularEstadisticas(); // Refresca los contadores y la tabla inmediatamente
        refrescarCalendario();
    }

    private void calcularEstadisticas() {
        // Limpiar los datos actuales de la tabla en la interfaz
        modeloTablaReportes.setRowCount(0);

        int totales = 0;
        int asistencias = 0;
        int canceladas = 0;

        // Recorrer la lista de citas que está en el Sistema
        for (Cita c : sistema.getListaCitas()) {
            totales++;

            // Contar según el estado
            if ("Completada".equals(c.getEstado())) asistencias++;
            if ("Cancelada".equals(c.getEstado())) canceladas++;

            // Buscar el nombre del paciente para la tabla
            int idx = sistema.buscarPacienteBinario(c.getIdPaciente());
            String nombrePac = (idx != -1) ? sistema.getListaPacientes().get(idx).getNombre() : "Desconocido";

            // Agregar la fila a la tabla visual
            modeloTablaReportes.addRow(new Object[]{
                    c.getIdCita(),
                    nombrePac,
                    c.getFechaHora().toLocalDate(),
                    c.getEstado()
            });
        }

        // Actualizar los cuadros de colores (Labels)
        lblTotalCitas.setText(String.valueOf(totales));
        lblAsistencias.setText(String.valueOf(asistencias));
        lblCanceladas.setText(String.valueOf(canceladas));
    }

    private JPanel crearCardInformativa(String titulo, JLabel lblValor, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(color, 2));
        card.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 14));

        // Configuramos el label que recibirá los datos dinámicos
        lblValor.setHorizontalAlignment(SwingConstants.CENTER);
        lblValor.setFont(new Font("Arial", Font.BOLD, 24));
        lblValor.setForeground(color);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearPanelRecordatorios() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- FORMULARIO IZQUIERDO ---
        JPanel panelConfig = new JPanel(new GridBagLayout());
        panelConfig.setBorder(BorderFactory.createTitledBorder("Configurar Nuevo Recordatorio"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Componentes nuevos para selección
        JTextField txtIdCita = new JTextField(10);
        JComboBox<String> cbMedio = new JComboBox<>(new String[]{"WhatsApp", "Email", "SMS"});
        JComboBox<String> cbAnticipacion = new JComboBox<>(new String[]{"24 horas antes", "1 hora antes", "15 minutos antes"});
        JButton btnGenerar = new JButton("Activar Recordatorio");

        gbc.gridx = 0; gbc.gridy = 0; panelConfig.add(new JLabel("ID de la Cita:"), gbc);
        gbc.gridx = 1; panelConfig.add(txtIdCita, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panelConfig.add(new JLabel("Medio de envío:"), gbc);
        gbc.gridx = 1; panelConfig.add(cbMedio, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelConfig.add(new JLabel("Anticipación:"), gbc);
        gbc.gridx = 1; panelConfig.add(cbAnticipacion, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panelConfig.add(btnGenerar, gbc);

        // --- LÓGICA DE GENERAR RECORDATORIO ---
        btnGenerar.addActionListener(e -> {
            try {
                int idCita = Integer.parseInt(txtIdCita.getText());
                Cita citaEncontrada = null;

                // Buscar la cita en el sistema
                for (Cita c : sistema.getListaCitas()) {
                    if (c.getIdCita() == idCita) {
                        citaEncontrada = c;
                        break;
                    }
                }

                if (citaEncontrada == null) {
                    JOptionPane.showMessageDialog(this, "La cita ID " + idCita + " no existe.");
                    return;
                }

                // Crear el recordatorio (ID autoincremental simple)
                int idRec = sistema.getListaRecordatorios().size() + 1;
                String medio = (String) cbMedio.getSelectedItem();
                String tiempo = (String) cbAnticipacion.getSelectedItem();

                Recordatorio nuevo = new Recordatorio(idRec, idCita, medio, tiempo);
                sistema.agregarRecordatorio(nuevo);

                // Actualizar Interfaz (Lista de la derecha)
                String entry = "🔔 ID:" + idRec + " - Cita:" + idCita + " - " + medio + " (" + tiempo + ")";
                modeloListaRecordatorios.addElement(entry);

                txtIdCita.setText("");
                JOptionPane.showMessageDialog(this, "Recordatorio programado.");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un ID de cita válido.");
            }
        });

        // --- LADO DERECHO: LISTA Y ELIMINACIÓN ---
        JList<String> listaVisual = new JList<>(modeloListaRecordatorios);
        JScrollPane scroll = new JScrollPane(listaVisual);
        JButton btnEliminar = new JButton("Eliminar Recordatorio");

        btnEliminar.addActionListener(e -> {
            int index = listaVisual.getSelectedIndex();
            if (index != -1) {
                // Extraer ID del texto del recordatorio para eliminar en el sistema
                String text = modeloListaRecordatorios.get(index);
                int idRec = Integer.parseInt(text.split(":")[1].split(" ")[0]);

                if (sistema.eliminarRecordatorio(idRec)) {
                    modeloListaRecordatorios.remove(index);
                    JOptionPane.showMessageDialog(this, "Recordatorio eliminado.");
                }
            }
        });

        JPanel panelDerecho = new JPanel(new BorderLayout(5, 5));
        panelDerecho.add(scroll, BorderLayout.CENTER);
        panelDerecho.add(btnEliminar, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelConfig, panelDerecho);
        split.setDividerLocation(350);
        panelPrincipal.add(split, BorderLayout.CENTER);

        return panelPrincipal;
    }

    private void refrescarCalendario() {
        calPanel.removeAll(); // Borra los botones actuales

        // Volver a poner los encabezados de los días
        String[] diasSemana = {"Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"};
        for (String d : diasSemana) {
            calPanel.add(new JLabel(d, SwingConstants.CENTER));
        }

        // Volver a crear los botones con la lógica de conteo
        for (int i = 1; i <= 31; i++) {
            int diaActual = i;
            int contador = 0;
            for (Cita c : sistema.getListaCitas()) {
                if (c.getFechaHora().getDayOfMonth() == diaActual && "Pendiente".equals(c.getEstado())) {
                    contador++;
                }
            }

            String textoBoton = (contador > 0) ? i + " (" + contador + ")" : String.valueOf(i);
            JButton btnDia = new JButton(textoBoton);
            if (contador > 0) btnDia.setBackground(new Color(255, 230, 150));
            else btnDia.setBackground(Color.WHITE);

            btnDia.addActionListener(e -> {
                lblDiaSeleccionado.setText("Día seleccionado: " + diaActual);
                actualizarListaCitasDelDia(diaActual);
            });
            calPanel.add(btnDia);
        }

        calPanel.revalidate(); // Avisa que el layout cambió
        calPanel.repaint();    // Redibuja visualmente
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Ventana().setVisible(true);
        });
    }
}