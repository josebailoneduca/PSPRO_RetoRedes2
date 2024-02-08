/**
 * 
 */
package ftpcliente.controlador;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import fptservidor.modelo.Codigos;
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

		
		
		//conectar("","");
		
	}

	/**
	 * @param rutaActual
	 * @param archivos
	 */
	public void actualizaLista(String rutaActual, ArrayList<DtoArchivo> archivos) {
		SwingUtilities.invokeLater(() -> 
			vista.actualizaListaRemota(rutaActual,archivos));
	}
	
	
	public boolean login(String host, int puerto, String usuario, String contrasena) {
		modelo.iniciarConexion(host, puerto);
		modelo.addOperacion("REGISTRO "+usuario+" "+contrasena);
		modelo.addOperacion("LS");
		return true;
//		boolean res = modelo.conectar(1, usuario, contrasena, host, puerto);
//		if (res)
//			modelo.addOperacion("LS");
//		return res;
	}

	public boolean registrar(String host, int puerto, String usuario, String contrasena) {
		boolean res = modelo.registrar(1, usuario, contrasena, host, puerto);
		if (res)
			modelo.addOperacion("LS");
		return res;
	}
	
}
