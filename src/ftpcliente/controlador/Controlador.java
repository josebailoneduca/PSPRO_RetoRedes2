/**
 * 
 */
package ftpcliente.controlador;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import ftpcliente.Config;
import ftpcliente.modelo.Modelo;
import ftpcliente.modelo.dto.DtoArchivo;
import ftpcliente.vista.gui.Ventana;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Controlador {
	private Modelo modelo;
	private Ventana vista;
	public Controlador() {
		modelo=new Modelo(this);
		vista = new Ventana(this);
		vista.setVisible(true);

		
		
		while(true) {}
		
	}

	/**
	 * @param rutaActual
	 * @param archivos
	 */
	public void actualizaLista(String rutaActual, ArrayList<DtoArchivo> archivos) {
		SwingUtilities.invokeLater(() -> 
			vista.actualizaLista(rutaActual,archivos));
	}
	
	
	public boolean conectar(String usuario, String contrasena) {
		boolean res = modelo.conectar(1, Config.USUARIO, Config.CONTRASENA, Config.HOST, Config.PUERTO);
		if (res)
			modelo.addOperacion("LS");
		return res;
	}

}
