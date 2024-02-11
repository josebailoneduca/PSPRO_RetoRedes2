/*
LICENCIA JOSE JAVIER BO
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
Lista de paquetes:
 */
package ftpcliente.vista.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreeModel;

import fptservidor.modelo.Codigos;
import ftpcliente.Config;
import ftpcliente.controlador.Controlador;
import ftpcliente.controlador.dto.DtoArchivo;
import ftpcliente.vista.modelos.ArbolArchivosModel;
import ftpcliente.vista.modelos.ArchArbol;
import ftpcliente.vista.modelos.ArchivoTableModel;

/**
 *
 * @author Jose Javier Bailon Ortiz
 */
public class Ventana extends JFrame implements TreeSelectionListener, ActionListener {

	Controlador controlador;

	public Ventana(Controlador controlador) {
		this.controlador = controlador;
		initComponents();
		initPropio();
		

	}

	private void initPropio() {
		inputHost.setText(Config.getHOST());
		inputPuerto.setText("" + Config.getPUERTO());
		inputUsuario.setText(Config.getUSUARIO());
		inputContrasena.setText(Config.getCONTRASENA());
		remotoTabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		localTabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		iniEventos();
		inicializarRutaLocal();
		activarLogin(true);
	}

	/**
	 * 
	 */
	private void inicializarRutaLocal() {
		//recoger unidades
		String[] roots = getUnidadesDisco();
		//inicializar arbol
		TreeModel model = new ArbolArchivosModel(new ArchArbol((String) localSelectorUnidad.getSelectedItem()));
		localArbol.setModel(model);
		
		//inicializar ruta actual local
		File cwdLocal = new File(new File(".").getAbsolutePath());
		String rootCwdLocal = cwdLocal.toPath().getRoot().toString();
		//activar el selector de unidades a la unidad donde esta el CWDlocal
		for (int i=0;i<roots.length;i++) {
			if (roots[i].equals(rootCwdLocal)) {
				localSelectorUnidad.setSelectedIndex(i);
			}
		}
		//establecer el texto de ruta
		localRuta.setText(Paths.get(cwdLocal.getAbsolutePath()).normalize().toAbsolutePath().toString());
		actualizarArchivosLocales();

	}

	private void iniEventos() {
		localArbol.addTreeSelectionListener(this);
		
		JButton[] botones = { btnCmdCD, btnCmdDEL, btnCmdGET, btnCmdLS, btnCmdMKDIR, btnCmdPUT,
				btnCmdRMDIR, btnConectar, btnDesconectar, btnEnviar, btnRegistrar };
		for (JButton boton: botones) {
			boton.addActionListener(this);
		}
		
		localSelectorUnidad.addActionListener(this);
		inputComando.addActionListener(this);
		
		//clicks tabla remota
		remotoTabla.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent me) {
		    	//seleccion con boton derecho
		    	int r = remotoTabla.rowAtPoint(me.getPoint());
		        if (r >= 0 && r < remotoTabla.getRowCount()) {
		        	remotoTabla.setRowSelectionInterval(r, r);
		        } else {
		        	remotoTabla.clearSelection();
		        }
		        //evento doble click
		    	JTable target = (JTable)me.getSource();
		    	int fila = target.getSelectedRow();
	            if (me.getClickCount() == 2) {     
	               clickRemoto(fila);
	             }
		    }

		});
		
		//clicks tabla local
		localTabla.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent me) {
		    	JTable target = (JTable)me.getSource();
		    	int fila = target.getSelectedRow();
	            if (me.getClickCount() == 2) {     
	               clickLocal(fila);
	             }
		    }

		});
		
		//menu contextual tabla remota
		remotoTabla.setComponentPopupMenu(new MenuRemoto(this,remotoTabla));
	}
	
	/**
	 * Click en tabla remota. Ejecuta CD o GET en el achivo clickado
	 * @param fila
	 */
	private void clickRemoto(int fila) {
		if (fila==-1)
			return;
		DtoArchivo arch = ((ArchivoTableModel)remotoTabla.getModel()).getItem(fila);
		if (arch.esDirectorio())
			controlador.comCd(arch.getNombre());
		else
			getArchivo();
		
	}

	
	private void clickLocal(int fila) {
		if (fila==-1)
			return;
		DtoArchivo arch = ((ArchivoTableModel)localTabla.getModel()).getItem(fila);
		if (!arch.esDirectorio())
			putArchivo();
	
	}

	/**
	 * @param rutaActual
	 * @param archivos
	 */
	public void actualizaListaRemota(String rutaActual, ArrayList<DtoArchivo> archivos) {
		remotoRuta.setText(rutaActual);
		remotoTabla.setModel(new ArchivoTableModel(archivos));
		remotoTabla.getColumnModel().getColumn(0).setMaxWidth(50);
	}

	/**
	 * Recoger unidades de disco e inicializar el selector de undidaes
	 * @return
	 */
	private String[] getUnidadesDisco() {
		File[] unidades;
		unidades = File.listRoots();
		String[] nombres = new String[unidades.length];
		for (int i = 0; i < unidades.length; i++) {
			nombres[i] = unidades[i].getAbsolutePath();
		}
		localSelectorUnidad.setModel(new DefaultComboBoxModel<String>(nombres));
		return nombres;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {

		ArchArbol node = (ArchArbol) ((JTree) e.getSource()).getLastSelectedPathComponent();
		if (node == null) {
			// since Nothing is selected.
			return;
		}
		localRuta.setText(node.getAbsolutePath());
		actualizarArchivosLocales();
	}

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
		case "DEL" -> borrarArchivo();
		case "BORRAR" -> borrarCualquiera();
		case "MKDIR" -> controlador.comMkdir(getValor("Introduzca el nombre del directorio"));
		case "RMDIR" -> borrarDirectorio();
		case "GET" -> getArchivo();
		case "PUT" -> putArchivo();

		}

	}

	

	/**
	 * @return
	 */
	private void putArchivo() {
		int filaSeleccionada = localTabla.getSelectedRow();
        if (filaSeleccionada == -1) {
        	msgInfo("Seleccione un archivo local");
            return;//no hacer nada si no hay fila seleccionada
        }     
        DtoArchivo arch = ((ArchivoTableModel)localTabla.getModel()).getItem(filaSeleccionada);
        if (!arch.esDirectorio()) {
        	String rutaLocal=localRuta.getText()+"/"+arch.getNombre();
        	String rutaRemota=remotoRuta.getText()+"/"+arch.getNombre();
        	controlador.comPut(rutaLocal,rutaRemota);
        }
        else {
        	msgInfo("Debe elegir un archivo, no un directorio");
        }
	}

	/**
	 * @return
	 */
	private void getArchivo() {

		int filaSeleccionada = remotoTabla.getSelectedRow();
        if (filaSeleccionada == -1) {
        	msgInfo("Seleccione un archivo remoto");
            return;//no hacer nada si no hay fila seleccionada
        }     
        DtoArchivo arch = ((ArchivoTableModel)remotoTabla.getModel()).getItem(filaSeleccionada);
        if (!arch.esDirectorio()) {
        	String rutaRemota=remotoRuta.getText()+"/"+arch.getNombre();
        	String rutaLocal=localRuta.getText()+"/"+arch.getNombre();
        	controlador.comGet(rutaRemota,rutaLocal);
        }
        else {
        	msgInfo("Debe elegir un archivo, no un directorio");
        }
	}

	/**
	 * @return
	 */
	private void borrarDirectorio() {
        int filaSeleccionada = remotoTabla.getSelectedRow();
        if (filaSeleccionada == -1) {
        	msgInfo("Seleccione un directorio remoto");
            return;//no hacer nada si no hay fila seleccionada
        }        
        
        DtoArchivo arch = ((ArchivoTableModel)remotoTabla.getModel()).getItem(filaSeleccionada);
        if (arch.esDirectorio()) {
        	controlador.comRmdir(arch.getNombre());
        }
        else {
        	msgInfo("Debe elegir un directorio, no un archivo");
        }
	}
	

	/**
	 * @return
	 */
	private void borrarCualquiera() {
        int filaSeleccionada = remotoTabla.getSelectedRow();
        if (filaSeleccionada == -1) {
        	msgInfo("Seleccione un archivo remoto");
            return;//no hacer nada si no hay fila seleccionada
        }        
        DtoArchivo arch = ((ArchivoTableModel)remotoTabla.getModel()).getItem(filaSeleccionada);
        if (arch.esDirectorio()) {
        	borrarDirectorio();
        }
        else { 
        	borrarArchivo();
        }
	}

	/**
	 * @return
	 */
	private void borrarArchivo() {
        int filaSeleccionada = remotoTabla.getSelectedRow();
        if (filaSeleccionada == -1) {
        	msgInfo("Seleccione un archivo remoto");
            return;//no hacer nada si no hay fila seleccionada
        }        
        DtoArchivo arch = ((ArchivoTableModel)remotoTabla.getModel()).getItem(filaSeleccionada);
        if (!arch.esDirectorio()) {
        	controlador.comDel(arch.getNombre());
        }
        else {
        	msgInfo("Debe elegir un archivo, no un directorio");
        }
	}

	/**
	 * @return
	 */
	private void enviarComando() {
		String comando = inputComando.getText();
		if (comando!=null && comando.length()>0)
			controlador.enviarComando(comando);
		inputComando.setText("");
	}

	/**
	 * @return
	 */
	private void logout() {
		controlador.logout();
	}

	/**
	 * @return
	 */
	private void login() {
		String host = inputHost.getText();
		int puerto = 0;
		try {
			puerto = Integer.parseInt(inputPuerto.getText());
		} catch (NumberFormatException ex) {
			// TODO AVISAR
			return;
		}
		String usuario = inputUsuario.getText();
		String contrasena = inputContrasena.getText();
		if (!controlador.login(host, puerto, usuario, contrasena))
			msgError("No se puede conectar");
	}

	private void registrar() {
		String host = inputHost.getText();
		int puerto = 0;
		try {
			puerto = Integer.parseInt(inputPuerto.getText());
		} catch (NumberFormatException ex) {
			// TODO AVISAR
			return;
		}
		String usuario = inputUsuario.getText();
		String contrasena = inputContrasena.getText();
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

	public void actualizarArchivosLocales() {
		File ruta = new File(localRuta.getText());
		localTabla.setModel(new ArchivoTableModel(controlador.getArchivosLocales(ruta)));
		localTabla.getColumnModel().getColumn(0).setMaxWidth(50);
	}


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
		btnCmdDEL = new JButton();
		btnCmdDEL.setActionCommand("DEL");
		btnCmdMKDIR = new JButton();
		btnCmdMKDIR.setActionCommand("MKDIR");
		btnCmdRMDIR = new JButton();
		btnCmdRMDIR.setActionCommand("RMDIR");
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
		panelCentralLayout
				.setHorizontalGroup(panelCentralLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(panelCentralLayout.createSequentialGroup().addContainerGap()
								.addComponent(panelDivisor, GroupLayout.DEFAULT_SIZE, 929, Short.MAX_VALUE)
								.addContainerGap()));
		panelCentralLayout
				.setVerticalGroup(panelCentralLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(panelCentralLayout.createSequentialGroup().addContainerGap()
								.addComponent(panelDivisor, GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
								.addContainerGap()));

		getContentPane().add(panelCentral, java.awt.BorderLayout.CENTER);

		panelInferior.setLayout(new java.awt.BorderLayout());
		panelComandos.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panelComandos.setLayout(new java.awt.BorderLayout(5, 5));
		panelComandos.add(inputComando, java.awt.BorderLayout.CENTER);
		btnEnviar.setText("Enviar");
		panelComandos.add(btnEnviar, java.awt.BorderLayout.LINE_END);
		historial.setColumns(20);
		historial.setRows(5);
		scrollHistorial.setViewportView(historial);
		panelComandos.add(scrollHistorial, java.awt.BorderLayout.PAGE_START);
		lbComando.setText("Comando:");
		panelComandos.add(lbComando, java.awt.BorderLayout.LINE_START);
		panelInferior.add(panelComandos, java.awt.BorderLayout.CENTER);
		panelEstado.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
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
		btnDesconectar.setText("Desconectar");
		panelConexion.add(btnDesconectar);
		btnDesconectar.setVisible(false);
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
		btnCmdDEL.setText("DEL");
		panelBotonera.add(btnCmdDEL);
		btnCmdMKDIR.setText("MKDIR");
		panelBotonera.add(btnCmdMKDIR);
		btnCmdRMDIR.setText("RMDIR");
		panelBotonera.add(btnCmdRMDIR);
		panelSuperior.add(panelBotonera, java.awt.BorderLayout.CENTER);
		getContentPane().add(panelSuperior, java.awt.BorderLayout.NORTH);
		pack();
	}


	private JButton btnCmdCD;
	private JButton btnCmdDEL;
	private JButton btnCmdGET;
	private JButton btnCmdLS;
	private JButton btnCmdMKDIR;
	private JButton btnCmdPUT;
	private JButton btnCmdRMDIR;
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

	
	
	

	public void msgInfo(String msg) {
		JOptionPane.showMessageDialog(this, msg, "", JOptionPane.INFORMATION_MESSAGE);
	}

	
	/**
	 * @param string
	 * @return
	 */
	public void msgError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public String getValor(String msg) {
		return JOptionPane.showInputDialog(msg); 
	}
	
	
	/**
	 * @param host
	 * @param puerto
	 * @param usuario
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
	 * @param b
	 */
	private void activarLogin(boolean b) {
		btnConectar.setVisible(b);
		btnRegistrar.setVisible(b);
		btnDesconectar.setVisible(!b);
		JButton[] botones = { btnCmdCD, btnCmdDEL, btnCmdGET, btnCmdLS, btnCmdMKDIR, btnCmdPUT,
				btnCmdRMDIR, btnEnviar};
		for (JButton btn : botones) {
			btn.setEnabled(!b);
		}
	}

 

}// fin Ftp
