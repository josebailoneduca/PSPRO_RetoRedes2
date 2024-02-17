/**
 * 
 */
package ftpcliente.conector.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ftpcliente.conector.Codigos;
import ftpcliente.conector.Sesion;

/**
 *  Se encarga de manejar un comando DEL en el cliente
 *  
 * @author Jose Javier Bailon Ortiz
 */
public class ComDel extends Comando {

	/**
	 * Constructor 
	 * 
	 * @param comando Partes del comando siendo el primer elemento el codigo del comando y los siguientes los parametros
	 * @param dis DataInputStream a usar por el comando
	 * @param dos  DataOutputStream a usar por el comando
	 * @param sesion Referencia a la sesion
	 */
	public ComDel(String[] comando, DataInputStream dis, DataOutputStream dos, Sesion sesion) {
		super(comando, dis, dos, sesion);
	}

	
	
	/**
	 * Inicia la operacion siguiendo el protocolo DEL (Ver estructura del protocolo en la documentacion)
	 */
	public void iniciar() {
		
		
		
		//comprobar que hay parametros suficientes
		if (comando.length < 2)
			return;
		
		//confirmacion del borrado
		if (!conector.confirmar("¿Desea borrar el archivo "+comando[1]+"?"))
		return;
		
		
		try {
			//escribir codigo de comando
			dos.writeUTF(Comando.DEL);
			
			//escribir ruta a eliminar
			dos.writeUTF(comando[1]);
			
			//esperar respuesta
			int res = dis.readInt();
			
			//actuar segun la respuesta sea OK  o no
			if (res == Codigos.OK) {
				conector.msgInfo("DEL Exitoso. Archivo " + comando[1] + " eliminado");
			} else if (res == Codigos.NO_EXISTE) {
				conector.msgError("DEL erroneo. No se pudo eliminar. El archivo " + comando[1] + " no existe");
			} else {
				conector.msgError("DEL erroneo. No se pudo eliminar " + comando[1]);
			}

		} catch (IOException e) {
			sesion.logout();
		}
	}

}
