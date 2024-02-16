/**
 * 
 */
package ftpservidor;

import ftpservidor.modelo.SModelo;

/**
 *  Punto de entrada para iniciar el servidor FTP
 * @author Jose Javier Bailon Ortiz
 */
public class MainServidor {
	
	/**
	 * Main del servidor FTP
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
		
		//Comprobar validez de las rutas de configuracion
		Config.comprobarRutas();
		
		
		//inicio del modelo
		SModelo modelo = new SModelo(Config.getPUERTO());
		modelo.start();
	}
}
