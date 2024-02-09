/**
 * 
 */
package prueba;

import java.io.File;
import java.nio.file.Paths;

/**
 * 
 * @author Bailon
 */
public class Prueba {
	 public static void main(String[] args) {
		 
			String ruta="almacenamientojose/../ambrosion/dsf";
			String ruta2="almacenamientojose/";
			
			File f=new File(ruta);
			File f2=new File(ruta2);
			


			System.out.println(Paths.get(f.getAbsolutePath()).normalize().toString());
			System.out.println(Paths.get(f2.getAbsolutePath()).normalize().toString());
	}
}
