/**
 * 
 */
package ftpservidor.modelo.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import ftpservidor.modelo.Sesion;
import ftpservidor.modelo.Usuario;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Comando {
	
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
	 * Usuario que ejecuta el comando
	 */
	protected Usuario usuario;
	
	/**
	 * DataInputStream de la operacion
	 */
	protected DataInputStream dis;
	
	/**
	 * DataOutputStream de la operacion
	 */
	protected DataOutputStream dos;
	
	/**
	 * Sesion de la operacion
	 */
	protected Sesion sesion;
	
	/**
	 * CWD de la sesion
	 */
	protected String cwd;


	
	/**
	 * Constructor
	 * 
	 * @param sesion Sesion que realiza la operacion
	 */
	public Comando(Sesion sesion) {
		this.sesion = sesion;
		this.usuario = sesion.getUsuario();
		this.dis = sesion.getDis();
		this.dos = sesion.getDos();
		this.cwd = sesion.getCwd();
	}
}
