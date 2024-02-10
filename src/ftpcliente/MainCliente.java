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
		String archivoConf=null;
		if (args!=null &&args.length>0) {
			archivoConf=args[0];
			Config.cargarConfiguracion(archivoConf);
		}
		Controlador controlador = new Controlador();
	}

}
