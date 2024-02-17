/**
 * 
 */
package ftpcliente.conector.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ftpcliente.conector.Codigos;
import ftpcliente.conector.Conector;
import ftpcliente.conector.Sesion;
 

/**
 *  Se encarga de manejar un comando CD en el cliente
 *  
 * @author Jose Javier Bailon Ortiz
 */
public class ComCd extends Comando {

	/**
	 * Constructor 
	 * 
	 * @param comando Partes del comando siendo el primer elemento el codigo del comando y los siguientes los parametros
	 * @param dis DataInputStream a usar por el comando
	 * @param dos  DataOutputStream a usar por el comando
	 * @param sesion Referencia a la sesion
	 */
	public ComCd(String[] comando, DataInputStream dis, DataOutputStream dos, Sesion sesion) {
		super(comando, dis, dos, sesion);

	}

	
	/**
	 * Inicia la operacion siguiendo el protocolo CD (Ver estructura del protocolo en la documentacion)
	 */
	public void iniciar() {
		
		//comprobar que hay parametros suficientes
		if (comando.length < 2)
			return;

		try {
			//escribir codigo de comando
			dos.writeUTF(Comando.CD);
			
			//escribir ruta destino
				dos.writeUTF(comando[1]);

			//esperar respuesta
			int res = dis.readInt();
			
			//actuar segun la respuesta sea OK  o no
			if (res != Codigos.OK)
				conector.msgError("CD erroneo. No existe "+comando[1]);
			else {
				conector.msgInfo("CD correcto a "+comando[1]);
				conector.addOperacion("LS");
			}
		} catch (IOException e) {
			sesion.logout();
		}
	}

}
