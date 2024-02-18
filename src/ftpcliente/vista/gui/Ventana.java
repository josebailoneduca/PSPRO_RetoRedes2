/*
LICENCIA JOSE JAVIER BO
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
Lista de paquetes:
 */
package ftpcliente.vista.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import ftpcliente.Config;
import ftpcliente.controlador.Controlador;
import ftpcliente.controlador.dto.DtoArchivo;
import ftpcliente.vista.modelos.ArbolArchivosModel;
import ftpcliente.vista.modelos.ArchArbol;
import ftpcliente.vista.modelos.ArchivoTableModel;

/**
 * Clase principal de la vista. Muestra un JFrame con la interfaz grafica
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Ventana extends JFrame implements TreeSelectionListener, ActionListener {

	/**
	 * Referencia al controlador
	 */
	private Controlador controlador;
	
	/**
	 * Arbol de directorios locales
	 */
	private TreePath ultimoPath;

	
	/**
	 * Constructor
	 * 
	 * @param controlador Referencia al controlador
	 */
	public Ventana(Controlador controlador) {
		this.controlador = controlador;
		initComponents();
		initPropio();

	}

	/**
	 * Inicializacion propia. Recoge datos de configuracion, inicializa eventos,
	 * ruta local y la botonera superior
	 * 
	 */
	private void initPropio() {
		this.setTitle("FTP - Jose Javier Bailón Ortiz");
		inputHost.setText(Config.getHOST());
		inputPuerto.setText("" + Config.getPUERTO());
		inputUsuario.setText(Config.getUSUARIO());
		inputContrasena.setText(Config.getCONTRASENA());

		iniEventos();
		inicializarRutaLocal();
		activarLogin(true);
		
	}

	/**
	 * Inicializa la ruta local
	 */
	private void inicializarRutaLocal() {
		// recoger unidades
		String[] roots = controlador.getUnidadesDisco();
		localSelectorUnidad.setModel(new DefaultComboBoxModel<String>(roots));

		// inicializar arbol
		TreeModel model = new ArbolArchivosModel(new ArchArbol((String) localSelectorUnidad.getSelectedItem()));
		localArbol.setModel(model);

		// inicializar ruta actual local al directorio de ejecucion
		File cwdLocal = new File(new File(".").getAbsolutePath());
		String rootCwdLocal = cwdLocal.toPath().getRoot().toString();
		// activar el selector de unidades a la unidad donde esta el CWDlocal
		for (int i = 0; i < roots.length; i++) {
			if (roots[i].equals(rootCwdLocal)) {
				localSelectorUnidad.setSelectedIndex(i);
			}
		}
		// establecer el texto de ruta
		localRuta.setText(Paths.get(cwdLocal.getAbsolutePath()).normalize().toAbsolutePath().toString());
		actualizarArchivosLocales();

	}

	/**
	 * Inicialia los eventos
	 */
	private void iniEventos() {
		//listener de arbol de directorios
		localArbol.addTreeSelectionListener(this);

		//listener de botones
		JButton[] botones = { btnCmdCD, btnCmdGET, btnCmdLS, btnCmdMKDIR, btnCmdPUT, btnConectar, btnDesconectar,
				btnEnviar, btnRegistrar };
		for (JButton boton : botones) {
			boton.addActionListener(this);
		}

		//listener de selector de unidades
		localSelectorUnidad.addActionListener(this);
		
		//listener de campo de entrada de comandos
		inputComando.addActionListener(this);

		// clicks tabla remota
		remotoTabla.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				// evento doble click
				JTable target = (JTable) me.getSource();
				int fila = target.getSelectedRow();
				if (me.getClickCount() == 2) {
					clickRemoto(fila);
				}
			}

		});

		// clicks tabla local
		localTabla.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				JTable target = (JTable) me.getSource();
				int fila = target.getSelectedRow();
				if (me.getClickCount() == 2) {
					clickLocal(fila);
				}
			}

		});

		// menu contextual tabla remota
		remotoTabla.setComponentPopupMenu(new MenuRemoto(this, remotoTabla));
		// menu contextual tabla local
		localTabla.setComponentPopupMenu(new MenuLocal(this, localTabla));

		// cambio de tamaño de la ventana
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				panelDivisor.setDividerLocation(getWidth() * 60 / 100);
				panelLocalDivisor.setDividerLocation(getWidth() * 30 / 100);
			}
		});

	}

	/**
	 * Doble click en tabla remota. Ejecuta CD o GET en el achivo clickado
	 * 
	 * @param fila Fila de la tabla a la que aplicar la accion
	 */
	private void clickRemoto(int fila) {
		if (fila == -1)
			return;
		DtoArchivo arch = ((ArchivoTableModel) remotoTabla.getModel()).getItem(fila);
		if (arch.esDirectorio())
			controlador.comCd(arch.getNombre());
		else
			getArchivo();
	}

	/**
	 * Doble click en tabla local. Ejecuta CD o PUT en el archivo clickado
	 * 
	 * @param fila Fila de la tabla a la que aplicar la accion
	 */
	private void clickLocal(int fila) {
		if (fila == -1)
			return;
		DtoArchivo arch = ((ArchivoTableModel) localTabla.getModel()).getItem(fila);
		if (arch.esDirectorio())
			cambiarDirectorioLocal(localRuta.getText() + "/" + arch.getNombre());
		else
			putArchivo();

	}

	/**
	 * Cambia el directorio local a la ruta definida
	 * 
	 * @param string La ruta a establecer
	 */
	private void cambiarDirectorioLocal(String string) {
		String rutaNormalizada = Paths.get(new File(string).getAbsolutePath()).normalize().toAbsolutePath().toString();
		localRuta.setText(rutaNormalizada);
		actualizarArchivosLocales();

	}

	/**
	 * Actualiza la tabla de archivos remotos 
	 * 
	 * @param rutaActual Ruta remota a mostrar
	 * @param archivos Lista de archivos a mostrar
	 */
	public void actualizaListaRemota(String rutaActual, ArrayList<DtoArchivo> archivos) {
		remotoRuta.setText(rutaActual);
		remotoTabla.setModel(new ArchivoTableModel(archivos));
		remotoTabla.getColumnModel().getColumn(0).setMaxWidth(50);
	}

	/**
	 * Click sobre arbol de directorios
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		ultimoPath = e.getPath();
		ArchArbol node = (ArchArbol) ((JTree) e.getSource()).getLastSelectedPathComponent();
		if (node == null) {
			return;
		}
		String ruta = node.getAbsolutePath();
		localRuta.setText(ruta);
		actualizarArchivosLocales();
	}

	/**
	 * Action listener de la interface grafica. Ejecuta una accion dependiendo
	 * del ActionCommand del originario del evento
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String ac = e.getActionCommand();
		switch (ac) {
		case "SELEC_UNIDAD" -> seleccionarUnidad();
		case "CONECTAR" -> login();
		case "REGISTRAR" -> registrar();
		case "EXIT" -> logout();
		case "ENVIAR" -> enviarComando();
		case "LS" -> controlador.comLs();
		case "CD" -> controlador.comCd(getValor("Introduzca la ruta"));
		case "DEL" -> borrar();
		case "BORRAR" -> borrar();
		case "MKDIR" -> controlador.comMkdir(getValor("Introduzca el nombre del directorio"));
		case "RMDIR" -> borrar();
		case "GET" -> getArchivo();
		case "PUT" -> putArchivo();
		case "ACTUALIZAR" -> actualizarArchivosLocales();
		case "MKDIRLOCAL" -> crearDirectorioLocal();
		}
	}

	
	
	/**
	 * Pide a controlador que cree un directorio local
	 * 
	 */
	private void crearDirectorioLocal() {
		//pedir ruta a crear
		String ruta = localRuta.getText() + "/" + getValor("Introduzca el nombre del directorio");
		//crear
		if (controlador.comMkdirLocal(ruta))
			actualizarArchivosLocales();
		else
			msgError("No se pudo crear el directorio: \n" + ruta);
	}

	/**
	 * Recoge los datos de rutas remota y local y nombres de archivos locales seleccionados para 
	 * pedir a controlador que ejecute un comando PUT por cada archivo
	 * 
	 */
	private void putArchivo() {
		int[] filasSeleccionadas = localTabla.getSelectedRows();

		for (int i = 0; i < filasSeleccionadas.length; i++) {
			int filaSeleccionada = filasSeleccionadas[i];
			DtoArchivo arch = ((ArchivoTableModel) localTabla.getModel()).getItem(filaSeleccionada);
			if (!arch.esDirectorio()) {
				String rutaLocal = localRuta.getText() + "/" + arch.getNombre();
				String rutaRemota = remotoRuta.getText() + "/" + arch.getNombre();
				controlador.comPut(rutaLocal, rutaRemota);
			} else {
				msgInfo("Los directorios no se pueden subir directamente");
			}
		}
	}

	/**
	 * Recoge los datos de rutas remota y local y nombres de archivos remotos seleccionados para 
	 * pedir a controlador que ejecute un comando GET por cada archivo
	 * 
	 */
	private void getArchivo() {
		int[] filasSeleccionadas = remotoTabla.getSelectedRows();
		for (int i = 0; i < filasSeleccionadas.length; i++) {
			int filaSeleccionada = filasSeleccionadas[i];
			DtoArchivo arch = ((ArchivoTableModel) remotoTabla.getModel()).getItem(filaSeleccionada);
			if (!arch.esDirectorio()) {
				String rutaRemota = remotoRuta.getText() + "/" + arch.getNombre();
				String rutaLocal = localRuta.getText() + "/" + arch.getNombre();
				controlador.comGet(rutaRemota, rutaLocal);
			} else {
				msgInfo("Lo directorios no se pueden descargar directamente");
			}
		}
	}

	
	/**
	 * Recoge los  nombres de archivos remotos seleccionados para 
	 * pedir a controlador que ejecute un comando DEL o RMDIR seun toque
	 *  por cada archivo presuponiendo que  estaran en el CWD remoto.
	 * 
	 */
	private void borrar() {
		int[] filasSeleccionadas = remotoTabla.getSelectedRows();
		for (int i = 0; i < filasSeleccionadas.length; i++) {
			int filaSeleccionada = filasSeleccionadas[i];
			DtoArchivo arch = ((ArchivoTableModel) remotoTabla.getModel()).getItem(filaSeleccionada);
			if (arch.esDirectorio()) {
				controlador.comRmdir(arch.getNombre());
			} else {
				controlador.comDel(arch.getNombre());
			}
		}
	}

	/**
	 * Envia a controlador el comando introducido en el campo inferior de comandos
	 */
	private void enviarComando() {
		String comando = inputComando.getText();
		if (comando != null && comando.length() > 0)
			controlador.enviarComando(comando);
		inputComando.setText("");
	}

	/**
	 * Pide a controlador el cierre de la sesion
	 */
	private void logout() {
		controlador.logout();
	}

	/**
	 * Recoge los datos de conexion y usuario y envia a controlador
	 * la orden de iniciar sesion con esos datos
	 */
	private void login() {
		String host = inputHost.getText();
		int puerto = 0;
		try {
			puerto = Integer.parseInt(inputPuerto.getText());
		} catch (NumberFormatException ex) {
			msgError("Puerto no valido");
			return;
		}
		String usuario = inputUsuario.getText();
		String contrasena = inputContrasena.getText();
		controlador.login(host, puerto, usuario, contrasena);
	}

	/**
	 * Recoge los datos de conexion y usuario y envia a controlador
	 * la orden de registra nuevo usuario con esos datos
	 */
	private void registrar() {
		String host = inputHost.getText();
		int puerto = 0;
		try {
			puerto = Integer.parseInt(inputPuerto.getText());
		} catch (NumberFormatException ex) {
			msgError("Puerto no valido");
			return;
		}
		String usuario = inputUsuario.getText();
		String contrasena = inputContrasena.getText();
		if (usuario.length()<1||contrasena.length()<1)
			msgError("Debe especificar usuario y contraseña para registrarse");
		else
		controlador.registrar(host, puerto, usuario, contrasena);
	}

	/**
	 * Lleva a cabo la seleccion de una nueva unidad de disco
	 */
	private void seleccionarUnidad() {
		TreeModel model = new ArbolArchivosModel(new ArchArbol((String) localSelectorUnidad.getSelectedItem()));
		localRuta.setText((String) localSelectorUnidad.getSelectedItem());
		localArbol.setModel(model);
		actualizarArchivosLocales();
	}

	/**
	 * Actualiza el listado de archivos locales y refresca el arbol de directorios
	 */
	public void actualizarArchivosLocales() {
		File ruta = new File(localRuta.getText());
		localTabla.setModel(new ArchivoTableModel(controlador.getArchivosLocales(ruta)));
		localTabla.getColumnModel().getColumn(0).setMaxWidth(50);

		//path actual para luego expandir el arbol tras el refresco
		TreePath p = ultimoPath;

		ArbolArchivosModel m = (ArbolArchivosModel) localArbol.getModel();
		String rutaRoot = ((ArchArbol) m.getRoot()).getAbsolutePath();
		TreeModel model = new ArbolArchivosModel(new ArchArbol((String) localSelectorUnidad.getSelectedItem()));
		localArbol.setModel(model);
		localArbol.expandPath(p);
	}
	
	
	/**
	 * Muestra un dialogo con un mensaje informativo
	 * 
	 * @param msg El mensaje
	 */
	public void msgInfo(String msg) {
		JOptionPane.showMessageDialog(this, msg, "", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Muestra un dialogo con un mensaje de error
	 * 
	 * @param msg El mensaje
	 */

	public void msgError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Muestra un dialogo pidiendo un valor
	 * 
	 * @param msg El mensaje para poner en el dialogo
	 * 
	 * @return El valor introducido por el usuario
	 */
	public String getValor(String msg) {
		return JOptionPane.showInputDialog(msg);
	}

	/**
	 * Muestra un dialogo de confirmacion
	 * 
	 * @param msg El mensaje a mostrar
	 * 
	 * @return True si el usuario ha aceptado, false si no ha aceptado
	 */
	public boolean confirmar(String msg) {
		return JOptionPane.showConfirmDialog(this, msg, "Confirmar",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}
	
	
	/**
	 * Actualiza la barra inferior de estado con los datos de conexion
	 * 
	 * @param conectado True si esta conectado, false si no lo esta
	 * @param host Host de la sesion
	 * @param usuario Usuario de la sesion
	 */
	public void actualizaLoginEstado(boolean conectado, String host, String usuario) {
		if (conectado) {
			lbConectado.setText("Conectado");
			datosConexion.setText(usuario + "@" + host);
			activarLogin(false);
		} else {
			lbConectado.setText("Desconectado");
			datosConexion.setText("");
			activarLogin(true);
			remotoRuta.setText("");
			remotoTabla.setModel(new ArchivoTableModel(new ArrayList<DtoArchivo>()));
			remotoTabla.getColumnModel().getColumn(0).setMaxWidth(50);
		}
	}

	/**
	 * Activa y desactiva los paneles de botones y campos de login segun se especifique
	 * 
	 * @param b True Establecer la visibilidad para poder hacer login, False establecer la visibilidad para operar una vez iniciada la sesion
	 */
	private void activarLogin(boolean b) {
		panelBotonera.setVisible(!b);

		panelConexion.setVisible(b);

	}



	/**
	 * Agrega un mensaje al panel de historial inferior
	 * 
	 * @param msg El mensaje
	 */
	public void addHistorial(String msg) {
		historial.setText(historial.getText() + "\n" + msg);

	}
	
	
	/**
	 * Creacion de componentes de la interfaz
	 */
	private void initComponents() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		btnConectar = new JButton();
		btnConectar.setActionCommand("CONECTAR");//
		btnDesconectar = new JButton();
		btnDesconectar.setActionCommand("EXIT");//
		btnRegistrar = new JButton();
		btnRegistrar.setActionCommand("REGISTRAR");//
		btnCmdLS = new JButton();
		btnCmdLS.setActionCommand("LS");//
		btnCmdGET = new JButton();
		btnCmdGET.setActionCommand("GET");
		btnCmdPUT = new JButton();
		btnCmdPUT.setActionCommand("PUT");
		btnCmdCD = new JButton();
		btnCmdCD.setActionCommand("CD");
		btnCmdMKDIR = new JButton();
		btnCmdMKDIR.setActionCommand("MKDIR");
		localSelectorUnidad = new JComboBox<>();
		localSelectorUnidad.setActionCommand("SELEC_UNIDAD");
		btnEnviar = new JButton();
		btnEnviar.setActionCommand("ENVIAR");
		inputComando = new JTextField();
		inputComando.setActionCommand("ENVIAR");

		localArbol = new JTree();
		localTabla = new JTable();
		localRuta = new JTextField();
		remotoTabla = new JTable();
		remotoRuta = new JLabel();
		historial = new JTextArea();
		historial.setEditable(false);
		datosConexion = new JLabel();
		inputHost = new JTextField();
		inputPuerto = new JTextField();
		inputUsuario = new JTextField();
		inputContrasena = new JTextField();

		panelCentral = new JPanel();
		panelDivisor = new JSplitPane();
		panelLocalDivisor = new JSplitPane();
		panelArbol = new JPanel();
		scrollLocalArbol = new JScrollPane();
		panelLocalArchivos = new JPanel();
		scrollLocalArchivos = new JScrollPane();
		panelRemoto = new JPanel();
		scrollRemotoArchivos = new JScrollPane();
		panelInferior = new JPanel();
		panelComandos = new JPanel();
		scrollHistorial = new JScrollPane();
		lbComando = new JLabel();
		panelEstado = new JPanel();
		lbConectado = new JLabel();
		panelSuperior = new JPanel();
		panelConexion = new JPanel();
		lbHost = new JLabel();
		lbPuerto = new JLabel();
		lbUsuario = new JLabel();
		lbContrasena = new JLabel();
		panelBotonera = new JPanel();

		panelDivisor.setDividerLocation(451);
		panelLocalDivisor.setBorder(BorderFactory.createTitledBorder("Archivos locales"));
		panelLocalDivisor.setDividerLocation(250);
		panelArbol.setLayout(new java.awt.BorderLayout(0, 5));
		localSelectorUnidad.setModel(new DefaultComboBoxModel<>(new String[] { "C:\\" }));
		panelArbol.add(localSelectorUnidad, java.awt.BorderLayout.NORTH);
		scrollLocalArbol.setViewportView(localArbol);
		panelArbol.add(scrollLocalArbol, java.awt.BorderLayout.CENTER);
		panelLocalDivisor.setLeftComponent(panelArbol);
		panelLocalArchivos.setLayout(new java.awt.BorderLayout(0, 10));
		scrollLocalArchivos.setViewportView(localTabla);
		panelLocalArchivos.add(scrollLocalArchivos, java.awt.BorderLayout.CENTER);

		localRuta.setEditable(false);
		localRuta.setText("C:\\");
		panelLocalArchivos.add(localRuta, java.awt.BorderLayout.NORTH);

		panelLocalDivisor.setRightComponent(panelLocalArchivos);

		panelDivisor.setLeftComponent(panelLocalDivisor);
		panelRemoto.setBorder(BorderFactory.createTitledBorder("Archivos remotos"));
		panelRemoto.setLayout(new java.awt.BorderLayout(0, 10));
		scrollRemotoArchivos.setViewportView(remotoTabla);
		panelRemoto.add(scrollRemotoArchivos, java.awt.BorderLayout.CENTER);
		remotoRuta.setText("/");
		panelRemoto.add(remotoRuta, java.awt.BorderLayout.NORTH);
		panelDivisor.setRightComponent(panelRemoto);
		panelDivisor.setDividerLocation(550);
		GroupLayout panelCentralLayout = new GroupLayout(panelCentral);
		panelCentral.setLayout(panelCentralLayout);
		panelCentralLayout.setHorizontalGroup(panelCentralLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(panelCentralLayout.createSequentialGroup().addContainerGap()
						.addComponent(panelDivisor, GroupLayout.DEFAULT_SIZE, 929, Short.MAX_VALUE).addContainerGap()));
		panelCentralLayout.setVerticalGroup(panelCentralLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(panelCentralLayout.createSequentialGroup().addContainerGap()
						.addComponent(panelDivisor, GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE).addContainerGap()));

		getContentPane().add(panelCentral, java.awt.BorderLayout.CENTER);

		panelInferior.setLayout(new java.awt.BorderLayout());

		panelComandos.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panelComandos.setLayout(new java.awt.BorderLayout(5, 5));
		panelComandos.add(inputComando, java.awt.BorderLayout.CENTER);
		btnEnviar.setText("Enviar");
		panelComandos.add(btnEnviar, java.awt.BorderLayout.LINE_END);
		historial.setColumns(20);
		historial.setRows(10);
		scrollHistorial.setViewportView(historial);
		panelComandos.add(scrollHistorial, java.awt.BorderLayout.PAGE_START);
		lbComando.setText("Comando:");
		panelComandos.add(lbComando, java.awt.BorderLayout.LINE_START);
		panelInferior.add(panelComandos, java.awt.BorderLayout.CENTER);
		panelEstado.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
				BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204))));
		panelEstado.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
		lbConectado.setText("Desconectado");
		panelEstado.add(lbConectado);
		panelEstado.add(datosConexion);
		panelInferior.add(panelEstado, java.awt.BorderLayout.SOUTH);
		getContentPane().add(panelInferior, java.awt.BorderLayout.SOUTH);
		panelSuperior.setLayout(new java.awt.BorderLayout());
		panelConexion.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		lbHost.setText("Host:");
		panelConexion.add(lbHost);
		inputHost.setMinimumSize(new java.awt.Dimension(100, 22));
		inputHost.setPreferredSize(new java.awt.Dimension(100, 22));
		panelConexion.add(inputHost);
		lbPuerto.setText("Puerto:");
		panelConexion.add(lbPuerto);
		inputPuerto.setPreferredSize(new java.awt.Dimension(40, 22));
		panelConexion.add(inputPuerto);
		lbUsuario.setText("Usuario:");
		panelConexion.add(lbUsuario);
		inputUsuario.setPreferredSize(new java.awt.Dimension(100, 22));
		panelConexion.add(inputUsuario);
		lbContrasena.setText("Contraseña:");
		panelConexion.add(lbContrasena);
		inputContrasena.setPreferredSize(new java.awt.Dimension(100, 22));
		panelConexion.add(inputContrasena);
		btnConectar.setText("Conectar");
		panelConexion.add(btnConectar);
		btnRegistrar.setText("Registrarse");
		btnRegistrar.setHorizontalTextPosition(SwingConstants.CENTER);
		btnRegistrar.setVerticalTextPosition(SwingConstants.BOTTOM);
		panelConexion.add(btnRegistrar);

		panelSuperior.add(panelConexion, java.awt.BorderLayout.NORTH);

		btnCmdLS.setText("LS");
		panelBotonera.add(btnCmdLS);

		btnCmdGET.setText("<-GET");
		panelBotonera.add(btnCmdGET);

		btnCmdPUT.setText("PUT->");
		panelBotonera.add(btnCmdPUT);

		btnCmdCD.setText("CD");
		panelBotonera.add(btnCmdCD);

		btnCmdMKDIR.setText("MKDIR");
		panelBotonera.add(btnCmdMKDIR);

		btnDesconectar.setText("Desconectar");
		panelBotonera.add(btnDesconectar);

		panelBotonera.setVisible(false);
		panelSuperior.add(panelBotonera, java.awt.BorderLayout.CENTER);
		getContentPane().add(panelSuperior, java.awt.BorderLayout.NORTH);
		pack();
	}

	private JButton btnCmdCD;
	private JButton btnCmdGET;
	private JButton btnCmdLS;
	private JButton btnCmdMKDIR;
	private JButton btnCmdPUT;
	private JButton btnConectar;
	private JButton btnDesconectar;
	private JButton btnEnviar;
	private JButton btnRegistrar;
	private JLabel datosConexion;
	private JTextArea historial;
	private JTextField inputComando;
	private JTextField inputContrasena;
	private JTextField inputHost;
	private JTextField inputPuerto;
	private JTextField inputUsuario;
	private JTree localArbol;
	private JTextField localRuta;
	private JComboBox<String> localSelectorUnidad;
	private JTable localTabla;
	private JTable remotoTabla;

	private JLabel lbComando;
	private JLabel lbConectado;
	private JLabel lbContrasena;
	private JLabel lbHost;
	private JLabel lbPuerto;
	private JLabel lbUsuario;
	private JPanel panelArbol;
	private JPanel panelBotonera;
	private JPanel panelCentral;
	private JPanel panelComandos;
	private JPanel panelConexion;
	private JSplitPane panelDivisor;
	private JPanel panelEstado;
	private JPanel panelInferior;
	private JPanel panelLocalArchivos;
	private JSplitPane panelLocalDivisor;
	private JPanel panelRemoto;
	private JPanel panelSuperior;
	private JLabel remotoRuta;
	private JScrollPane scrollHistorial;
	private JScrollPane scrollLocalArbol;
	private JScrollPane scrollLocalArchivos;
	private JScrollPane scrollRemotoArchivos;

	

} 
