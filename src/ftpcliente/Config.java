/**
 * 
 */
package ftpcliente;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 *  Configuracion. Contiene varios parametros accesibles de manera estatica.
 *  Puede cargar los parametros desde un archivo
 *  
 * @author Jose Javier Bailon Ortiz
 */
public class Config {
	private  static  String COD_TEXTO = "UTF-8";
	private static  String HOST="localhost";
	private static  int PUERTO=50_000;
	private static  String USUARIO="";
	private static  String CONTRASENA="";
 	
	public static void cargarConfiguracion(String ruta) {
		
		try {
			Properties prop = new Properties();
			if (ruta!=null) {
			File archConf = new File(ruta);
			FileReader fr = new FileReader(archConf);
			prop.load(fr);
			}
			HOST =  prop.getProperty("HOST", HOST);
			PUERTO= Integer.parseInt(prop.getProperty("PUERTO",""+PUERTO));
			USUARIO =  prop.getProperty("USUARIO", USUARIO);
			CONTRASENA =  prop.getProperty("CONTRASENA", CONTRASENA);

		} catch (IOException | NumberFormatException e ) {
			System.out.println("No se ha podido cargar la configuracion");
		}
	}
	
	



	public static String getCOD_TEXTO() {
		return COD_TEXTO;
	}



	public static String getHOST() {
		return HOST;
	}

	public static String getUSUARIO() {
		return USUARIO;
	}

	public static int getPUERTO() {
		return PUERTO;
	}

	public static String getCONTRASENA() {
		return CONTRASENA;
	}

 
	
	
	
	
	
}
