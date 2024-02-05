/**
 * 
 */
package ftpcliente.vista.gui;

import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.BoxLayout;
import javax.swing.JButton;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Ventana extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Ventana frame = new Ventana();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Ventana() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 488);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
	 
		
		JPanel panelListado = new JPanel();
		contentPane.add(panelListado, BorderLayout.CENTER);
		JPanel panelBotonera = new JPanel();
		panelBotonera.setLayout(new BoxLayout(panelBotonera, BoxLayout.Y_AXIS));
		contentPane.add(panelBotonera, BorderLayout.WEST);
		
		JButton btnConectar = new JButton("CONECTAR");
		panelBotonera.add(btnConectar);
		JButton btnDesconectar = new JButton("DESCONECTAR");
		panelBotonera.add(btnConectar);
		JButton btnListar = new JButton("LS");
		panelBotonera.add(btnListar);
		
		JButton btnGet= new JButton("GET");
		panelBotonera.add(btnGet);
		
		JButton btnPut= new JButton("PUT");
		panelBotonera.add(btnPut);
		
		JButton btnCd= new JButton("CD");
		panelBotonera.add(btnCd);
		
		JButton btnDel= new JButton("DEL");
		panelBotonera.add(btnDel);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
	}

}
