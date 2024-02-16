/**
 * 
 */
package ftpservidor.modelo.lib;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class UtilesArchivo {

	/**
	 * Comprueba si ruta A esta dentro de rutaB
	 * @param rutaA
	 * @param rutaB
	 * @return
	 */
	public static boolean rutaDentroDeRuta(String rutaA, String rutaB) {
		try {
		File fA=new File(rutaA);
		File fB=new File(rutaB);
		String normalizadaA=Paths.get(fA.getAbsolutePath()).normalize().toString();
		String normalizadaB=Paths.get(fB.getAbsolutePath()).normalize().toString();
		return normalizadaA.startsWith(normalizadaB);
		}catch(InvalidPathException ex) {
			return false;
		}
	}

	
	/**
	 * Devuelve si una ruta existe
	 * @param ruta
	 * @return
	 */
	public static boolean rutaExiste(String ruta) {
		return new File(ruta).exists();
	}

	/**
	 * @param rutaUsuario
	 * @param cwd
	 * @param nuevaRuta
	 * @return
	 */
	public static String componerRuta(String rutaUsuario, String cwd, String nuevaRuta) {
		 if (nuevaRuta==null || nuevaRuta.length()==0)
			 return rutaUsuario+cwd;
		 String rutaCompleta = rutaUsuario;
		 if (nuevaRuta.charAt(0)=='/' || nuevaRuta.charAt(0)=='\\')
			 rutaCompleta +=nuevaRuta;
		 else
			 rutaCompleta += cwd+'/'+nuevaRuta;
		
		return rutaCompleta;
	}

 
}
