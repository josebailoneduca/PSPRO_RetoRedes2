/**
 * 
 */
package ftpcliente;

import java.awt.EventQueue;

import ftpcliente.controlador.Controlador;
import ftpcliente.vista.gui.Ventana;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class MainCliente {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					Ventana frame = new Ventana();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
		Controlador controlador = new Controlador();
	}

}
