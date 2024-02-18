/**
 * 
 */
package ftpservidor.modelo.lib;
import java.text.SimpleDateFormat;

/**
 * Clase estatica encargada de imprimir mensajes en consola
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Msg {

	/**
	 * Mensaje unico
	 * @param msg El mensaje
	 */
	public static void msg(String msg) {
		System.out.println(msg);
	}
	
	/**
	 * Mensaje encuadrado
	 * @param msg El mensaje
	 */
	public static void cuadro (String msg) {
		cuadro(new String[]{msg});
	}

	/**
	 * Lista de mensajes contenidos en un cuadro
	 * 
	 * @param msgs El mensaje
	 */
	public static void cuadro (String[] msgs) {
		int ancho=90;
		System.out.println("*".repeat(ancho));
		for (String msg : msgs) {
			System.out.println(String.format("* %-"+(ancho-4)+"s *", msg));
		}
		System.out.println("*".repeat(ancho));
	}
	
	
	/**
	 * Mensaje con timestamp
	 * 
	 * @param msg El mensaje
	 */
	public static void msgHora(String msg) {
		System.out.println(new SimpleDateFormat("(YYYY/MM/dd HH:mm:ss)").format(new java.util.Date())+" - "+msg);
	}
}
