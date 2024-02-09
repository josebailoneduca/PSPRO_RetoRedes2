/**
 * 
 */
package ftpcliente.conector.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import ftpcliente.conector.Modelo;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComExit extends Comando{

	public ComExit(String[] comando,DataInputStream dis, DataOutputStream dos,Modelo modelo) {
		super(comando,dis,dos,modelo);

	}
	
 
	public void iniciar() {
		
		try {
			dos.writeUTF(TiposComando.EXIT);
		} catch (IOException e) {
		}
		modelo.setEstadoLogin(false,null);
	
	}
	
}
