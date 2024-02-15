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

import fptservidor.modelo.Codigos;
import fptservidor.modelo.Sesion;
import fptservidor.modelo.Usuario;
import ftpcliente.conector.Modelo;
import ftpcliente.controlador.dto.DtoArchivo;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComDel extends Comando {

	public ComDel(String[] comando, DataInputStream dis, DataOutputStream dos, Modelo modelo) {
		super(comando, dis, dos, modelo);

	}

	public void iniciar() {

		if (comando.length < 2)
			return;

		try {
			dos.writeUTF(TiposComando.DEL);
			dos.writeUTF(comando[1]);

			int res = dis.readInt();
			if (res == Codigos.OK) {
				modelo.mensajeInfo("Archivo " + comando[1] + " eliminado");
			} else if (res == Codigos.NO_EXISTE) {
				modelo.mensajeError("No se pudo eliminar. El archivo " + comando[1] + " no existe");
			} else {
				modelo.mensajeError("No se pudo eliminar " + comando[1]);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
