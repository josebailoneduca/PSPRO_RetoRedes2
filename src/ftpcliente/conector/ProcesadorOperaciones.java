/**
 * 
 */
package ftpcliente.conector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ftpcliente.conector.comandos.ComCd;
import ftpcliente.conector.comandos.ComDel;
import ftpcliente.conector.comandos.ComExit;
import ftpcliente.conector.comandos.ComGet;
import ftpcliente.conector.comandos.ComLogin;
import ftpcliente.conector.comandos.ComLs;
import ftpcliente.conector.comandos.ComMkdir;
import ftpcliente.conector.comandos.ComPut;
import ftpcliente.conector.comandos.ComRegistro;
import ftpcliente.conector.comandos.ComRmdir;

/**
 * Hilo asociado a una sesion que se encarga de ir procesando los comandos
 * que se van agregando a la sesion.
 * 
 * Una LinkedBlockingQueue sirve de buffer de comandos. 
 * 
 * Sesion va agregando Strings a esa lista y esta clase va recogiendo y analizando esos string
 * para efectuar las acciones que describen.
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ProcesadorOperaciones extends Thread {

	/**
	 * Buffer de operaciones a realizar
	 */
	private LinkedBlockingQueue<String> operaciones;
	
	/**
	 * DataInputStream a utilizar para efectuar las operaciones
	 */
	private DataInputStream dis;
	
	/**
	 * DataOutputStream a utilizar para efectuar las operaciones
	 */
	private DataOutputStream dos;
	
	/**
	 * Define si el hilo debe continuar trabajando(usado en el bucle de la carrera)
	 */
	private boolean trabajar = true;

	/**
	 * Sesion a la que este procesador de operaciones esta asociado
	 */
	private Sesion sesion;

	/**
	 * Constructor
	 * 
	 * @param operaciones Buffer de operaciones a realizar
	 * @param dis DataInputStream a utilizar para efectuar las operaciones
	 * @param dos DataOutputStream a utilizar para efectuar las operaciones
	 * @param sesion Sesion a la que este procesador de operaciones esta asociado
	 */
	public ProcesadorOperaciones(LinkedBlockingQueue<String> operaciones, DataInputStream dis, DataOutputStream dos,
			Sesion sesion) {
		this.operaciones = operaciones;
		this.dis = dis;
		this.dos = dos;
		this.sesion = sesion;
	}

	@Override
	public void run() {
		
		//blucle mientras trabajar sea true
		while (trabajar) {
			//recoger operacion
			String operacion;
			try {
				operacion = operaciones.take();
				if (!sesion.isConectado()) {
					sesion.logout();
					return;
				}
				//extraer las partes de la operacion (codigo de comando y parametros)
				String[] partes = extraerPartesComando(operacion);
				if (partes.length > 0) {
					String comando = partes[0];
					
					//ejecutar el comando segun su tipo
					switch (comando.toUpperCase()) {
					case "REGISTRO" -> new ComRegistro(partes, dis, dos, sesion).iniciar();
					case "LOGIN" -> new ComLogin(partes, dis, dos, sesion).iniciar();
					case "EXIT" -> new ComExit(partes, dis, dos, sesion).iniciar();
					case "LS" -> new ComLs(partes, dis, dos, sesion).iniciar();
					case "CD" -> new ComCd(partes, dis, dos, sesion).iniciar();
					case "DEL" -> new ComDel(partes, dis, dos, sesion).iniciar();
					case "MKDIR" -> new ComMkdir(partes, dis, dos, sesion).iniciar();
					case "RMDIR" -> new ComRmdir(partes, dis, dos, sesion).iniciar();
					case "GET" -> new ComGet(partes, dis, dos, sesion).iniciar();
					case "PUT" -> new ComPut(partes, dis, dos, sesion).iniciar();
					}
				}
			} catch (InterruptedException e) {
				trabajar=false;
			}
		}

	}

	/**
	 * Extrae las partes de un comando expresado como string.
	 * 
	 * Divide las partes por espacios en blanco conservando como un unico parametro lo que
	 * haya puesto entre comillas dobles
	 * 
	 * @param operacion La cadena con la operacion a realizar
	 * 
	 * @return Array de partes que componen la operacion
	 */
	public static String[] extraerPartesComando(String operacion) {
		ArrayList<String> partes = new ArrayList<String>();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(operacion);
		while (m.find())
			partes.add(m.group(1).replaceAll("\"", ""));

		return partes.toArray(new String[0]);
	}

	/**
	 * Para el procesador de operaciones
	 */
	public void parar() {
		trabajar=false;
	}

}
