/**
 * 
 */
package ftpservidor.modelo.lib;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

/**
 * Utilidades para la gestion de archivos
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class UtilesArchivo {

	/**
	 * Comprueba si ruta A esta dentro de rutaB. Usado para comprobar que las peticiones de un
	 * usuario son de archivos dentro de su carpeta permitida
	 * 
	 * @param rutaA Ruta a comprobar
	 * @param rutaB Ruta a comprobar
	 * @return True si rutaA es hija o igual que rutaB. False en caso contrario 
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
	 * @param ruta La ruta a comprobar
	 * @return True si existe false si no existe
	 */
	public static boolean rutaExiste(String ruta) {
		return new File(ruta).exists();
	}

	
	
	/**
	 * Compone una ruta a usando el directorio de usuario, su CWD y una ruta extra que en caso de ser
	 * relativa se compone respecto al CWD y en caso de empezar con / se supone absoluta a partir
	 * de la ruta. 
	 * 
	 * <p>Asi si la ruta empieza con / la composcion es:
	 * dicrectorioUsuario/rutaExtra </p>
	 * 
	 * <p>Si la ruta no empieza con / entonces la composicion es:
	 * directorioUsuario/CWD/rutaExtra</p>
	 * 
	 * 
	 * @param rutaUsuario Directorio del usuario
	 * @param cwd : Directorio actual de la sesion dentro del directorio de usuario
	 * @param rutaExtra: ruta extra a agregar
	 * 
	 * @return La ruta compuesta
	 */
	public static String componerRuta(String rutaUsuario, String cwd, String rutaExtra) {
		 if (rutaExtra==null || rutaExtra.length()==0)
			 return rutaUsuario+cwd;
		 String rutaCompleta = rutaUsuario;
		 if (rutaExtra.charAt(0)=='/' || rutaExtra.charAt(0)=='\\')
			 rutaCompleta +=rutaExtra;
		 else
			 rutaCompleta += cwd+'/'+rutaExtra;
		
		return rutaCompleta;
	}

 
}
