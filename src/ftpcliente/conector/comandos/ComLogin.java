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
 *  Se encarga de manejar un comando LOGIN en el cliente
 *  
 * @author Jose Javier Bailon Ortiz
 */
public class ComLogin extends Comando{

	/**
	 * Constructor 
	 * 
	 * @param comando Partes del comando siendo el primer elemento el codigo del comando y los siguientes los parametros
	 * @param dis DataInputStream a usar por el comando
	 * @param dos  DataOutputStream a usar por el comando
	 * @param modelo Referencia al modelo
	 */
	public ComLogin(String[] comando,DataInputStream dis, DataOutputStream dos,Modelo modelo) {
		super(comando,dis,dos,modelo);

	}
	
 
	/**
	 * Inicia la operacion siguiendo el protocolo LOGIN (Ver estructura del protocolo en la documentacion)
	 */
	public void iniciar() {
		//anular si ya se esta logueado
		if (modelo.isLogged())
			return;
		//ver si hay parametros suficientes
		if (comando.length<4)
			return;
		
		try {
			//escribir codigo de comando
			dos.writeUTF(Comando.LOGIN);
			
			//escribir tipo e login
			int tipo = Integer.parseInt(comando[1]);
			String usuario="ANONIMO";
			dos.writeInt(tipo);
			
			//escribir usuario y contrasena si es tipo normal
			if (tipo==Codigos.LOGIN_NORMAL) {
				 usuario = comando[2];
				String contrasena = comando[3];
				dos.writeUTF(usuario);
				dos.writeUTF(contrasena);
				}
			
			//leer respuesta
			int res = dis.readInt();
			
			//respuesta afirmativa, lanzar LS automaticamente
			if (res==Codigos.OK) {
				modelo.setEstadoLogin(true,usuario);
				modelo.msgInfo("Sesión iniciada como "+usuario+" en "+modelo.getHost());
				modelo.addOperacion("LS");
			}
			
			//respuesta negativa
			else {
				modelo.setEstadoLogin(false,null);
				modelo.msgError("Login erroneo");
			}
		} catch (IOException e) {
			 modelo.setEstadoLogin(false,null);
		}
	
	}
	
}
