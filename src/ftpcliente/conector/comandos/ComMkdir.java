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
 *  Se encarga de manejar un comando MKDIR en el cliente
 *  
 * @author Jose Javier Bailon Ortiz
 */
public class ComMkdir extends Comando {

	/**
	 * Constructor 
	 * 
	 * @param comando Partes del comando siendo el primer elemento el codigo del comando y los siguientes los parametros
	 * @param dis DataInputStream a usar por el comando
	 * @param dos  DataOutputStream a usar por el comando
	 * @param sesion Referencia a la sesion
	 */
	public ComMkdir(String[] comando, DataInputStream dis, DataOutputStream dos, Sesion sesion) {
		super(comando, dis, dos, sesion);
	}

	
	/**
	 * Inicia la operacion siguiendo el protocolo MKDIR (Ver estructura del protocolo en la documentacion)
	 */
	public void iniciar() {

		//comprobar que hay parametros suficientes
		if (comando.length < 2)
			return;

		try {
			//escribir codigo de comando
			dos.writeUTF(Comando.MKDIR);
			
			//escribir directorio remoto a crear
			dos.writeUTF(comando[1]);

			//esperar respuesta
			int res = dis.readInt();
			
			//si no se puede crear
			if (res == Codigos.MAL) {
				conector.msgError("MKDIR erroneo. No se pudo crear" + comando[1]);
			
			//si ya existe
			} else if (res == Codigos.YA_EXISTE) {
				conector.msgError("MKDIR erroneo. El directorio " + comando[1] + " ya existe");
			
			//si se ha creado
			} else {
				conector.msgInfo("MKDIR exitoso. Directorio creado " + comando[1]);
				conector.addOperacion("LS");
			}

		} catch (IOException e) {
			conector.msgError("MKDIR erroneo. No se pudo crear " + comando[1]);
			sesion.logout();
		}
	}

}
