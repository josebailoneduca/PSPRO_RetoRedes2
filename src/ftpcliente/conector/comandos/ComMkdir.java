/**
 * 
 */
package ftpcliente.conector.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import ftpcliente.conector.Modelo;
import ftpcliente.controlador.dto.DtoArchivo;
import ftpservidor.modelo.Codigos;
import ftpservidor.modelo.Sesion;
import ftpservidor.modelo.Usuario;

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
	 * @param modelo Referencia al modelo
	 */
	public ComMkdir(String[] comando, DataInputStream dis, DataOutputStream dos, Modelo modelo) {
		super(comando, dis, dos, modelo);

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
				modelo.msgError("MKDIR erroneo. No se pudo crear" + comando[1]);
			
			//si ya existe
			} else if (res == Codigos.YA_EXISTE) {
				modelo.msgError("MKDIR erroneo. El directorio " + comando[1] + " ya existe");
			
			//si se ha creado
			} else {
				modelo.msgInfo("MKDIR exitoso. Directorio creado " + comando[1]);
			}

		} catch (IOException e) {
			modelo.msgError("MKDIR erroneo. No se pudo crear" + comando[1]);
		}
	}

}
