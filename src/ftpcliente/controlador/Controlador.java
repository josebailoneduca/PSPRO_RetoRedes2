/**
 * 
 */
package ftpcliente.controlador;

import ftpcliente.Config;
import ftpcliente.modelo.Modelo;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Controlador {
	private Modelo modelo;
	
	public Controlador() {
		modelo=new Modelo();
		modelo.conectar(1, Config.USUARIO, Config.CONTRASENA, Config.HOST, Config.PUERTO);
		modelo.procesarOperaciones();
		
	}
	

}
