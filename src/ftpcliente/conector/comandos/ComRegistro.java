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
 *  Se encarga de manejar un comando REGISTRO en el cliente
 *  
 * @author Jose Javier Bailon Ortiz
 */
public class ComRegistro extends Comando{

	/**
	 * Constructor 
	 * 
	 * @param comando Partes del comando siendo el primer elemento el codigo del comando y los siguientes los parametros
	 * @param dis DataInputStream a usar por el comando
	 * @param dos  DataOutputStream a usar por el comando
	 * @param modelo Referencia al modelo
	 */
	public ComRegistro(String[] comando,DataInputStream dis, DataOutputStream dos,Modelo modelo) {
		super(comando,dis,dos,modelo);

	}
	
	/**
	 * Inicia la operacion siguiendo el protocolo REGISTRO (Ver estructura del protocolo en la documentacion)
	 */
	public void iniciar() {
		//si la sesion esta iniciada cancelar
		if (modelo.isLogged())
			return;
					
		//comprobar que hay parametros suficientes
		if (comando.length<3)
			return;
		try {
			//escribir codigo de comando
			dos.writeUTF(Comando.REGISTRO);
			
			String usuario=comando[1];
			String contrasena = comando[2];
			
			//escribir nombre de usuario
			dos.writeUTF(usuario);
			//escribir contrasena
			dos.writeUTF(contrasena);

			//esperar respuesta
			int res = dis.readInt();
			
			//si afirmativo
			if (res==Codigos.OK) {
				modelo.msgInfo("Registro con exito: "+usuario);
				modelo.setEstadoLogin(true,usuario);
			}
			//si negativo
			else {
				modelo.msgError("Registro erroneo: "+usuario);
				modelo.setEstadoLogin(false,null);
			}
		} catch (IOException e) {
			modelo.msgError("Registro erroneo");
			modelo.setEstadoLogin(false,null);
		}
	
	}
	
}
