/**
 * 
 */
package ftpcliente.conector.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ftpcliente.conector.Modelo;

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
	 * @param modelo Referencia al modelo
	 */
	public ComExit(String[] comando,DataInputStream dis, DataOutputStream dos,Modelo modelo) {
		super(comando,dis,dos,modelo);
	}
	
	/**
	 * Inicia la operacion siguiendo el protocolo EXIT (Ver estructura del protocolo en la documentacion)
	 */
	public void iniciar() {
		
		try {
			//escribir codigo de comando
			dos.writeUTF(Comando.EXIT);
		} catch (IOException e) {
		}
		//Establecer la sesion como terminada
		modelo.setEstadoLogin(false,null);
	
	}
	
}
