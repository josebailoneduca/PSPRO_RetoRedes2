/**
 * 
 */
package fptservidor.controlador;

import fptservidor.Config;
import fptservidor.modelo.SModelo;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class SControlador {
	SModelo modelo;

	public SControlador() {
		modelo = new SModelo(Config.PUERTO);
		modelo.start();
	}
	
	
}
