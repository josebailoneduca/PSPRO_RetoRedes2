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
public class ComRmdir extends Comando {

	public ComRmdir(String[] comando, DataInputStream dis, DataOutputStream dos, Modelo modelo) {
		super(comando, dis, dos, modelo);

	}

	public void iniciar() {

		if (comando.length < 2) 
			return;
		try {
				dos.writeUTF(TiposComando.RMDIR);
				dos.writeUTF(comando[1]);
				int res = dis.readInt();
				if (res == Codigos.MAL) {
					modelo.msgError("No se pudo eliminar " + comando[1]);
				} else if (res == Codigos.NO_VACIO) {
					modelo.msgError("El directorio no está vacío. No se pudo eliminar: " + comando[1]);
				}else if (res==Codigos.OK) {
					modelo.msgInfo("Directorio borrado "+comando[1]);
				}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
