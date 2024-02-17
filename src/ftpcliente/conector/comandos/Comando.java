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
	/**
	 * Codigo comando RMDIR
	 */
	public static final String RMDIR="RMDIR";

	/**
	 * Codigo comando MKDIR
	 */
	public static final String MKDIR="MKDIR";

	/**
	 * Codigo comando DEL
	 */
	public static final String DEL="DEL";

	/**
	 * Codigo comando CD
	 */
	public static final String CD="CD";

	/**
	 * Codigo comando PUT
	 */
	public static final String PUT="PUT";

	/**
	 * Codigo comando GET
	 */
	public static final String GET="GET";

	/**
	 * Codigo comando LS
	 */
	public static final String LS="LS";

	/**
	 * Codigo comando EXIT
	 */
	public static final String EXIT="EXIT";

	/**
	 * Codigo comando LOGIN
	 */
	public static final String LOGIN="LOGIN";
	
	/**
	 * Codigo comando REGISTRO
	 */
	public static final String REGISTRO="REGISTRO";
	
	
	/**
	 * Partes del comando siendo el primer elemento el codigo del comando y los siguientes los parametros
	 */
	protected String[] comando;
	
	/**
	 * DataInputStream a usar por el comando
	 */
	protected DataInputStream dis;
	
	/**
	 * DataOutputStream a usar por el comando
	 */
	protected DataOutputStream dos;
	
	/**
	 * Referencia al modelo
	 */
	protected Modelo modelo;


	
	/**
	 * Constructor 
	 * 
	 * @param comando Partes del comando siendo el primer elemento el codigo del comando y los siguientes los parametros
	 * @param dis DataInputStream a usar por el comando
	 * @param dos  DataOutputStream a usar por el comando
	 * @param modelo Referencia al modelo
	 */
	public Comando(String[] comando, DataInputStream dis, DataOutputStream dos, Modelo modelo) {

		this.comando = comando;
		this.dis = dis;
		this.dos = dos;
		this.modelo = modelo;
	}

	
	/**
	 * Ejecucion del comando. En este metodo se debe implementar las acciones del comando cuando se ordene su ejecucion.
	 */
	abstract public void iniciar();

}
