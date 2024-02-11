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
 * 
 * @author Bailon
 */
public class ProcesadorOperaciones extends Thread {

	private LinkedBlockingQueue<String> operaciones;
	private DataInputStream dis;
	private DataOutputStream dos;
	private boolean trabajar = true;

	private Modelo modelo;

	public ProcesadorOperaciones(LinkedBlockingQueue<String> operaciones, DataInputStream dis, DataOutputStream dos,
			Modelo modelo) {
		this.operaciones = operaciones;
		this.dis = dis;
		this.dos = dos;
		this.modelo = modelo;
	}

	@Override
	public void run() {
		while (trabajar) {
			String operacion;
			try {
				operacion = operaciones.take();
				String[] partes = extraerPartesComando(operacion);
				if (partes.length > 0) {
					String comando = partes[0];
					switch (comando.toUpperCase()) {
					case "REGISTRO" -> new ComRegistro(partes, dis, dos, modelo).iniciar();
					case "LOGIN" -> new ComLogin(partes, dis, dos, modelo).iniciar();
					case "EXIT" -> new ComExit(partes, dis, dos, modelo).iniciar();
					case "LS" -> new ComLs(partes, dis, dos, modelo).iniciar();
					case "CD" -> new ComCd(partes, dis, dos, modelo).iniciar();
					case "DEL" -> new ComDel(partes, dis, dos, modelo).iniciar();
					case "MKDIR" -> new ComMkdir(partes, dis, dos, modelo).iniciar();
					case "RMDIR" -> new ComRmdir(partes, dis, dos, modelo).iniciar();
					case "GET" -> new ComGet(partes, dis, dos, modelo).iniciar();
					case "PUT" -> new ComPut(partes, dis, dos, modelo).iniciar();
					}
				}
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}

	}

	/**
	 * @param operacion
	 * @return
	 */
	private String[] extraerPartesComando(String operacion) {
		ArrayList<String> partes = new ArrayList<String>();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(operacion);
		while (m.find())
			partes.add(m.group(1).replaceAll("\"", ""));

		return partes.toArray(new String[0]);
	}

	/**
	 * 
	 */
	public void parar() {
		trabajar=false;
	}

}
