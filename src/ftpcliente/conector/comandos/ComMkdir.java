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
public class ComMkdir extends Comando{

	public ComMkdir(String[] comando,DataInputStream dis, DataOutputStream dos,Modelo modelo) {
		super(comando,dis,dos,modelo);

	}
	
 
	public void iniciar() {
		
		try {
			if (comando.length>0) {
				dos.writeUTF(TiposComando.MKDIR);
				dos.writeUTF(comando[1]);

			int res = dis.readInt();
			if (res!=Codigos.OK) 
				modelo.mensajeError("No se pudo crear"+comando[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}