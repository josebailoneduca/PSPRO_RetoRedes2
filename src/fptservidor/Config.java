/**
 * 
 */
package fptservidor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Config {

	private static int PUERTO = 50_000;
	private  static  String COD_TEXTO = "UTF-8";
	private  static String RUTA_ALMACENAMIENTO_ANONIMO = "almacenamiento/publico";
	private  static String NOMBRE_USUARIO_ANONIMO = "Anónimo";
	private  static String RUTA_ALMACENAMIENTO = "almacenamiento";
	private static  boolean MODO_TEXTO=false;

	public static void cargarConfiguracion(String ruta) {
		
		try {
			
			Properties prop = new Properties();
			File archConf = new File(ruta);
			FileReader fr = new FileReader(archConf);
			prop.load(fr);
			PUERTO= Integer.parseInt(prop.getProperty("PUERTO",""+PUERTO));
			RUTA_ALMACENAMIENTO_ANONIMO =  prop.getProperty("RUTA_ALMACENAMIENTO_ANONIMO", RUTA_ALMACENAMIENTO_ANONIMO);
			NOMBRE_USUARIO_ANONIMO =  prop.getProperty("NOMBRE_USUARIO_ANONIMO", NOMBRE_USUARIO_ANONIMO);
			RUTA_ALMACENAMIENTO =  prop.getProperty("RUTA_ALMACENAMIENTO", RUTA_ALMACENAMIENTO);
			MODO_TEXTO = Boolean.parseBoolean(prop.getProperty("MODO_TEXTO", ""+MODO_TEXTO));
			comprobarRutas();
		} catch (IOException | NumberFormatException e) {
			System.out.println("No se ha podido cargar la configuracion");
		}

	}

	/**
	 * 
	 */
	public static void comprobarRutas() {
		File f = new File(RUTA_ALMACENAMIENTO);
		if (!f.exists()) {
			System.out.println("La ruta de almacenamiento "+f.getAbsolutePath()+" no existe");
			System.exit(0);
		}
		File fa = new File(getRUTA_ALMACENAMIENTO_ANONIMO());
		if (!fa.exists()) {
			System.out.println("La ruta de almacenamiento anónimo "+f.getAbsolutePath()+" no existe");
			System.exit(0);
		}
	}

	public static int getPUERTO() {
		return PUERTO;
	}

	public static String getCOD_TEXTO() {
		return COD_TEXTO;
	}

	public static String getRUTA_ALMACENAMIENTO_ANONIMO() {
		return RUTA_ALMACENAMIENTO_ANONIMO;
	}

	public static String getNOMBRE_USUARIO_ANONIMO() {
		return NOMBRE_USUARIO_ANONIMO;
	}

	public static String getRUTA_ALMACENAMIENTO() {
		return RUTA_ALMACENAMIENTO;
	}

	public static boolean isMODO_TEXTO() {
		return MODO_TEXTO;
	}
	
	
}
