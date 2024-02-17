/**
 * 
 */
package ftpcliente.conector.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ftpcliente.conector.Sesion;

/**
 *  Se encarga de manejar un comando EXIT en el cliente
 *  
 * @author Jose Javier Bailon Ortiz
 */
public class ComExit extends Comando{

	/**
	 * Constructor 
	 * 
	 * @param comando Partes del comando siendo el primer elemento el codigo del comando y los siguientes los parametros
	 * @param dis DataInputStream a usar por el comando
	 * @param dos  DataOutputStream a usar por el comando
	 * @param sesion Referencia a la sesion
	 */
	public ComExit(String[] comando,DataInputStream dis, DataOutputStream dos,Sesion sesion) {
		super(comando,dis,dos,sesion);
	}
	
	/**
	 * Inicia la operacion siguiendo el protocolo EXIT (Ver estructura del protocolo en la documentacion)
	 */
	public void iniciar() {
		try {
			dos.writeUTF(Comando.EXIT);
		} catch (IOException e) {
		}
		sesion.logout();
	}
	
}
