/**
 * 
 */
package prueba;

import java.io.File;

/**
 * 
 * @author Bailon
 */
public class Prueba {
	 public static void main(String[] args) {
		String ruta="almacenamientojose/";
		File f = new File(ruta);
		System.out.println(f.getAbsolutePath());
		File[] archivos = new File(ruta).listFiles();

	}
}
