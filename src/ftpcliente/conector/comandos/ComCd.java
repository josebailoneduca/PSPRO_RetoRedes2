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
public class ComCd extends Comando {

	public ComCd(String[] comando, DataInputStream dis, DataOutputStream dos, Modelo modelo) {
		super(comando, dis, dos, modelo);

	}

	public void iniciar() {
		if (comando.length < 2)
			return;

		try {
			dos.writeUTF(Comando.CD);
			if (comando.length > 0)
				dos.writeUTF(comando[1]);
			else
				dos.writeUTF(" ");

			int res = dis.readInt();
			if (res != Codigos.OK)
				modelo.msgError("CD erroneo a "+comando[1]);
			else
				modelo.msgInfo("CD correcto a "+comando[1]);
		} catch (IOException e) {
			modelo.logout();
		}
	}

}
