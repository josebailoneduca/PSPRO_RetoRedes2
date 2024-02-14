/**
 * 
 */
package fptservidor;

import fptservidor.controlador.SControlador;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class MainServidor {
	public static void main(String[] args) {
		String archivoConf=null;
		if (args!=null &&args.length>0) {
			archivoConf=args[0];
			Config.cargarConfiguracion(archivoConf);
		}
		Config.comprobarRutas();
		SControlador controlador = new SControlador();

	}
}
