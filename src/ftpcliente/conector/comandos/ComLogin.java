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
public class ComLogin extends Comando{

	public ComLogin(String[] comando,DataInputStream dis, DataOutputStream dos,Modelo modelo) {
		super(comando,dis,dos,modelo);

	}
	
 
	public void iniciar() {
		if (comando.length<4)
			return;
		
		try {
			dos.writeUTF(Comando.LOGIN);
			int tipo = Integer.parseInt(comando[1]);
			String usuario="ANONIMO";
			dos.writeInt(tipo);
			if (tipo==Codigos.LOGIN_NORMAL) {
				 usuario = comando[2];
				String contrasena = comando[3];
				dos.writeUTF(usuario);
				dos.writeUTF(contrasena);
				}
			int res = dis.readInt();
			if (res==Codigos.OK) {
				modelo.setEstadoLogin(true,usuario);
				modelo.msgInfo("Sesión iniciada como "+usuario+" en "+modelo.getHost());
				modelo.addOperacion("LS");
			}
			else {
				modelo.setEstadoLogin(false,null);
				modelo.msgError("Login erroneo");
			}
		} catch (IOException e) {
			 modelo.setEstadoLogin(false,null);
		}
	
	}
	
}
