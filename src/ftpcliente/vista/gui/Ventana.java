/**
 * 
 */
package ftpcliente.vista.gui;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ftpcliente.controlador.Controlador;
import ftpcliente.modelo.dto.DtoArchivo;

import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Ventana extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Controlador controlador;
	JTextField campoRuta;
	private JTable tabla;
	ArchivoTableModel tableModel;
 

	/**
	 * Create the frame.
	 * @param controlador 
	 */
	public Ventana(Controlador controlador) {
		this.controlador=controlador;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 621, 495);
		
		//PADRE
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		//LISTADO
		JPanel panelListado = new JPanel();
		panelListado.setLayout(new BorderLayout());
		contentPane.add(panelListado, BorderLayout.CENTER);
		//ruta
		JPanel panelRuta = new JPanel();
		panelRuta.setLayout(new BorderLayout());
		panelListado.add(panelRuta,BorderLayout.NORTH);
		
		JLabel lbRutaActual = new JLabel("Ruta actual:");
		panelRuta.add(lbRutaActual,BorderLayout.WEST);
		
		campoRuta = new JTextField();
		panelRuta.add(campoRuta,BorderLayout.CENTER);
		
		//tabla
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setLayout(new ScrollPaneLayout());
		panelListado.add(scrollPane, BorderLayout.CENTER);
		
		tabla = new JTable();
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabla.setFillsViewportHeight(true);
		ArrayList<DtoArchivo> l = new ArrayList<DtoArchivo>();
		tableModel=new ArchivoTableModel(new ArrayList<DtoArchivo>());
		tabla.setModel(tableModel);
		tabla.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		tabla.getColumnModel().getColumn(0).setMaxWidth(50);
		scrollPane.add(tabla);
		scrollPane.setViewportView(tabla);
		tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Verificar si fue un doble clic
                    JTable target = (JTable)e.getSource();
                    int row = target.getSelectedRow();
                    // Obtener el contenido de la fila doble clickeada
                    String id = target.getValueAt(row, 0).toString();
                    String firstName = target.getValueAt(row, 1).toString();
                    // Imprimir los datos de la fila
                    System.out.println("Doble clic en la fila: " + id + ", " + firstName);
                }
            }
        });
		
		
		
		
		//BOTONERA
		JPanel panelBotonera = new JPanel();
		contentPane.add(panelBotonera, BorderLayout.NORTH);
		panelBotonera.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		ArrayList <JButton> botones = new ArrayList<JButton>();
		
		String[] btnEtiquetas= {"CONECTAR","DESCONECTAR","LS","GET","PUT","CD","DEL","MKDIR","RMDIR"};
		
		for(String st :btnEtiquetas) {
			JButton btn = new JButton(st);
			btn.setActionCommand(st);
			btn.addActionListener(this);
			panelBotonera.add(btn);
		}
		
		JPanel panelEstado = new JPanel();
		contentPane.add(panelEstado, BorderLayout.SOUTH);

	}

	/**
	 * @param rutaActual
	 * @param archivos
	 */
	public void actualizaLista(String rutaActual, ArrayList<DtoArchivo> archivos) {
		 campoRuta.setText(rutaActual);
		 tabla.setModel(new ArchivoTableModel(archivos));
		 tabla.getColumnModel().getColumn(0).setMaxWidth(50);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String ac = e.getActionCommand();
		
		switch (ac) {
		case "CONECTAR"-> controlador.conectar("","");
		case "DESCONECTAR"-> controlador.conectar("","");
		case "LS"-> controlador.conectar("","");
		case "GET"-> controlador.conectar("","");
		case "PUT"-> controlador.conectar("","");
		case "CD"-> controlador.conectar("","");
		case "DEL"-> controlador.conectar("","");
		case "MKDIR"-> controlador.conectar("","");
		case "RMDIR"-> controlador.conectar("","");
		};
		
		
		
		}
	 

}
