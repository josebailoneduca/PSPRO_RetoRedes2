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
	 * @param sesion Referencia a la sesion
	 */
	public ComRegistro(String[] comando,DataInputStream dis, DataOutputStream dos,Sesion sesion) {
		super(comando,dis,dos,sesion);

	}
	
	/**
	 * Inicia la operacion siguiendo el protocolo REGISTRO (Ver estructura del protocolo en la documentacion)
	 */
	public void iniciar() {
		//si la sesion esta iniciada cancelar
		if (conector.isLogged())
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
				conector.msgInfo("Registro con exito: "+usuario);
				sesion.setEstadoLogin(true,usuario);
				conector.addOperacion("LS");
				sesion.setEstadoLogin(true,usuario);
			}
			//si negativo
			else {
				conector.msgError("Registro erroneo: "+usuario);
				sesion.logout();
			}
		} catch (IOException e) {
			conector.msgError("Registro erroneo");
			sesion.logout();
		}
	
	}
	
}
