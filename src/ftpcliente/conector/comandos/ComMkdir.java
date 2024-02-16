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
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComMkdir extends Comando {

	public ComMkdir(String[] comando, DataInputStream dis, DataOutputStream dos, Modelo modelo) {
		super(comando, dis, dos, modelo);

	}

	public void iniciar() {

		if (comando.length < 2)
			return;

		try {
			dos.writeUTF(Comando.MKDIR);
			dos.writeUTF(comando[1]);

			int res = dis.readInt();
			if (res == Codigos.MAL) {
				modelo.msgError("MKDIR erroneo. No se pudo crear" + comando[1]);
			} else if (res == Codigos.YA_EXISTE) {
				modelo.msgError("MKDIR erroneo. El directorio " + comando[1] + " ya existe");
			} else {
				modelo.msgInfo("MKDIR exitoso. Directorio creado " + comando[1]);
			}

		} catch (

		IOException e) {
			modelo.msgError("MKDIR erroneo. No se pudo crear" + comando[1]);
		}
	}

}
