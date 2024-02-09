/*
LICENCIA JOSE JAVIER BO
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
Lista de paquetes:
 */
package ftpcliente.vista.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
		seleccionarUnidad();

	}

	private void initPropio() {
		inputHost.setText(Config.HOST);
		inputPuerto.setText("" + Config.PUERTO);
		inputUsuario.setText(Config.USUARIO);
		inputContrasena.setText(Config.CONTRASENA);

		getUnidades();
		iniEventos();
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
		
		
		remotoTabla.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent me) {
	            if (me.getClickCount() == 2) {     
	                JTable target = (JTable)me.getSource();
	                int fila = target.getSelectedRow();
	               clickRemoto(fila);
	             }
		    }

		});
		localTabla.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent me) {
	            if (me.getClickCount() == 2) {     
	                JTable target = (JTable)me.getSource();
	                int fila = target.getSelectedRow();
	               clickLocal(fila);
	             }
		    }

		});
	}
	private void clickRemoto(int fila) {
		DtoArchivo arch = ((ArchivoTableModel)remotoTabla.getModel()).getItem(fila);
		if (arch.getTipo()==Codigos.DIRECTORIO)
			controlador.comCd(arch.getNombre());
		else
			controlador.comGet(arch.getNombre());
		
	}

	
	private void clickLocal(int row) {
		// TODO Auto-generated method stub
		
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

	private void getUnidades() {
		File[] unidades;
		unidades = File.listRoots();
		String[] nombres = new String[unidades.length];
		for (int i = 0; i < unidades.length; i++) {
			nombres[i] = unidades[i].getAbsolutePath();
		}
		localSelectorUnidad.setModel(new DefaultComboBoxModel<String>(nombres));
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

	private void actualizarArchivosLocales() {
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
		panelLocalDivisor.setDividerLocation(150);
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
	public void actualizaLogin(boolean conectado, String host, String usuario) {
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
	}

 

}// fin Ftp
