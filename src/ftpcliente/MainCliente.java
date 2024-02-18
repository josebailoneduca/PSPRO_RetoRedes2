/**
 * 
 */
package ftpcliente;

import ftpcliente.controlador.Controlador;

/**
 *  Punto de entrada para iniciar el cliente FTP
 * @author Jose Javier Bailon Ortiz
 */
public class MainCliente {

	/**
	 * Main del cliente FTP
	 * 
	 * @param args Ruta de archivo de configuracion
	 */
	public static void main(String[] args) {
		
		//Cargar configuracion externa si se ha especificado
		String archivoConf=null;
		if (args!=null &&args.length>0) {
			archivoConf=args[0];
			Config.cargarConfiguracion(archivoConf);
		}
		
		//crear el controlador
		Controlador controlador = new Controlador();
	}

}
