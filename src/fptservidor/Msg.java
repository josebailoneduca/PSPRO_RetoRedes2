/**
 * 
 */
package fptservidor;
import java.text.SimpleDateFormat;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Msg {

	public static void msg(String msg) {
		System.out.println(msg);
	}
	
	public static void cuadro (String msg) {
		cuadro(new String[]{msg});
	}

	public static void cuadro (String[] msgs) {
		int ancho=58;
		System.out.println("*".repeat(ancho));
		for (String msg : msgs) {
			System.out.println(String.format("* %-"+(ancho-4)+"s *", msg));
		}
		System.out.println("*".repeat(ancho));
	}
	
	public static void msgHora(String msg) {
		System.out.println(new SimpleDateFormat("(YYYY/MM/dd HH:mm:ss)").format(new java.util.Date())+" - "+msg);
	}
}
