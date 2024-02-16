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
public class ComRegistro extends Comando{

	public ComRegistro(String[] comando,DataInputStream dis, DataOutputStream dos,Modelo modelo) {
		super(comando,dis,dos,modelo);

	}
	
 
	public void iniciar() {
		
		try {
			dos.writeUTF(TiposComando.REGISTRO);
			String usuario=comando[1];
			String contrasena = comando[2];
			dos.writeUTF(usuario);
			dos.writeUTF(contrasena);
			int res = dis.readInt();
			if (res==Codigos.OK) {
				modelo.msgInfo("Registro con exito: "+usuario);
				modelo.setEstadoLogin(true,usuario);
			}
			else {
				modelo.malRegistro();
				modelo.msgError("Registro erroneo: "+usuario);
			}
		} catch (IOException e) {
			 modelo.malRegistro();
		}
	
	}
	
}
