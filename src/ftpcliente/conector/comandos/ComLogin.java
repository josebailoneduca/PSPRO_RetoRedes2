/**
 * 
 */
package ftpcliente.conector.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ftpcliente.conector.Codigos;
import ftpcliente.conector.Sesion;

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
	 * @param sesion Referencia a la sesion
	 */
	public ComLogin(String[] comando,DataInputStream dis, DataOutputStream dos,Sesion sesion) {
		super(comando,dis,dos,sesion);

	}
	
 
	/**
	 * Inicia la operacion siguiendo el protocolo LOGIN (Ver estructura del protocolo en la documentacion)
	 */
	public void iniciar() {
		//anular si ya se esta logueado
		if (sesion.isLogged())
			return;
		//ver si hay parametros suficientes
		if (comando.length<2)
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
				sesion.setEstadoLogin(true,usuario);
				conector.msgInfo("Sesión iniciada como "+usuario+" en "+conector.getHost());
				conector.addOperacion("LS");
			}
			
			//respuesta negativa
			else {
				
				conector.msgError("Login erroneo");
				sesion.logout();
			}
		} catch (IOException e) {
				sesion.logout();
		}
	
	}
	
}
