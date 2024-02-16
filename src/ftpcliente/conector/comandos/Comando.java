/**
 * 
 */
package ftpcliente.conector.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import ftpcliente.conector.Modelo;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
abstract public class Comando {
	protected String[] comando;
	protected DataInputStream dis;
	protected DataOutputStream dos;
	protected Modelo modelo;
	public static final String RMDIR = "RMDIR";
	public static final String MKDIR = "MKDIR";
	public static final String DEL = "DEL";
	public static final String CD = "CD";
	public static final String PUT = "PUT";
	public static final String GET = "GET";
	public static final String LS = "LS";
	public static final String EXIT = "EXIT";
	public static final String REGISTRO = "REGISTRO";
	public static final String LOGIN = "LOGIN";

	public Comando(String[] comando, DataInputStream dis, DataOutputStream dos, Modelo modelo) {

		this.comando = comando;
		this.dis = dis;
		this.dos = dos;
		this.modelo = modelo;
	}

	abstract public void iniciar();

}
