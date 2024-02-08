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
	public Comando(String[] comando, DataInputStream dis, DataOutputStream dos, Modelo modelo) {

		this.comando=comando;
		this.dis = dis;
		this.dos = dos;
		this.modelo = modelo;
	}
	
	abstract public void iniciar();
	
}
