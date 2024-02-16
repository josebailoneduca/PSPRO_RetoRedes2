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
	private  SModelo modelo;

	public SControlador() {
		modelo = new SModelo(Config.getPUERTO());
		modelo.start();
	}
	
	
	
}
