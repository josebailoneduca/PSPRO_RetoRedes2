/**
 * 
 */
package ftpservidor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 *  Configuracion. Contiene varios parametros accesibles de manera estatica.
 *  Puede cargar los parametros desde un archivo
 * @author Jose Javier Bailon Ortiz
 */
public class Config {

	/**
	 * Charset utilizado para leer y escribir de disco
	 */
	private static String COD_TEXTO = "UTF-8";
	
	/**
	 * Puerto de escucha
	 */
	private static int PUERTO = 50_000;
	
	/**
	 * Ruta para el almacenamiento de acceso anonimo
	 */
	private static String RUTA_ALMACENAMIENTO_ANONIMO = "almacenamiento/publico";
	
	/**
	 * Nomre usado para el usuario anonimo
	 */
	private static String NOMBRE_USUARIO_ANONIMO = "Anónimo";
	
	/**
	 * Ruta de almacenamiento para usuarios normales
	 */
	private static String RUTA_ALMACENAMIENTO = "almacenamiento";
	
	/**
	 * Modo de transferencia de archivo. True se para modo texto. False para hacerlo como flujo de bytes.
	 */
	private static boolean MODO_TEXTO = true;

	/**
	 * El tiempo de vida de una conexion no ocupada en milisegundos
	 */
	private static int TIEMPO_VIDA_DESOCUPADO= 180000;//3minutos

	
	
	
	/**
	 * Carga la configuracion desde una ruta
	 * 
	 * @param ruta La ruta del archivo de configuracion
	 */
	public static void cargarConfiguracion(String ruta) {

		try {

			Properties prop = new Properties();
			File archConf = new File(ruta);
			FileReader fr = new FileReader(archConf);
			prop.load(fr);
			PUERTO = Integer.parseInt(prop.getProperty("PUERTO", "" + PUERTO));
			RUTA_ALMACENAMIENTO_ANONIMO = prop.getProperty("RUTA_ALMACENAMIENTO_ANONIMO", RUTA_ALMACENAMIENTO_ANONIMO);
			NOMBRE_USUARIO_ANONIMO = prop.getProperty("NOMBRE_USUARIO_ANONIMO", NOMBRE_USUARIO_ANONIMO);
			RUTA_ALMACENAMIENTO = prop.getProperty("RUTA_ALMACENAMIENTO", RUTA_ALMACENAMIENTO);
			MODO_TEXTO = Boolean.parseBoolean(prop.getProperty("MODO_TEXTO", "" + MODO_TEXTO));
			TIEMPO_VIDA_DESOCUPADO= Integer.parseInt(prop.getProperty("TIEMPO_VIDA_DESOCUPADO", "" + PUERTO));

			comprobarRutas();
		} catch (IOException | NumberFormatException e) {
			System.out.println("No se ha podido cargar la configuracion");
		}
	}

	
	/**
	 * Comprueba que existan las rutas especificadas terminando el programa si no son validas.
	 */
	public static void comprobarRutas() {
		File f = new File(RUTA_ALMACENAMIENTO);
		if (!f.exists()) {
			System.out.println("La ruta de almacenamiento " + f.getAbsolutePath() + " no existe");
			System.exit(0);
		}
		File fa = new File(getRUTA_ALMACENAMIENTO_ANONIMO());
		if (!fa.exists()) {
			System.out.println("La ruta de almacenamiento anónimo " + fa.getAbsolutePath() + " no existe");
			System.exit(0);
		}
	}

	
	/**
	 * Devuelve la configuracion PUERTO
	 * 
	 * @return El valor del parametro
	 */
	public static int getPUERTO() {
		return PUERTO;
	}

	/**
	 * Devuelve la configuracion COD_TEXTO
	 * 
	 * @return El valor del parametro
	 */
	public static String getCOD_TEXTO() {
		return COD_TEXTO;
	}

	/**
	 * Devuelve la configuracion RUTA_ALMACENAMIENTO_ANONIMO
	 * 
	 * @return El valor del parametro
	 */
	public static String getRUTA_ALMACENAMIENTO_ANONIMO() {
		return RUTA_ALMACENAMIENTO_ANONIMO;
	}

	
	/**
	 * Devuelve la configuracion NOMBRE_USUARIO_ANONIMO
	 * 
	 * @return El valor del parametro
	 */
	public static String getNOMBRE_USUARIO_ANONIMO() {
		return NOMBRE_USUARIO_ANONIMO;
	}

	/**
	 * Devuelve la configuracion RUTA_ALMACENAMIENTO
	 * 
	 * @return El valor del parametro
	 */
	public static String getRUTA_ALMACENAMIENTO() {
		return RUTA_ALMACENAMIENTO;
	}

	/**
	 * Devuelve la configuracion MODO_TEXTO
	 * 
	 * @return El valor del parametro
	 */
	public static boolean isMODO_TEXTO() {
		return MODO_TEXTO;
	}
	/**
	 * Devuelve la configuracion MODO_TEXTO
	 * 
	 * @return El valor del parametro
	 */
	public int isTIEMPO_VIDA_DESOCUPADO() {
		return TIEMPO_VIDA_DESOCUPADO;
	}

}
