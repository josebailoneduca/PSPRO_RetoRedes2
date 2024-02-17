/**
 * 
 */
package ftpcliente.conector.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ftpcliente.conector.Codigos;
import ftpcliente.conector.Sesion;
import ftpcliente.controlador.dto.DtoArchivo;

/**
 *  Se encarga de manejar un comando LS en el cliente
 *  
 * @author Jose Javier Bailon Ortiz
 */
public class ComLs extends Comando{

	/**
	 * Constructor 
	 * 
	 * @param comando Partes del comando siendo el primer elemento el codigo del comando y los siguientes los parametros
	 * @param dis DataInputStream a usar por el comando
	 * @param dos  DataOutputStream a usar por el comando
	 * @param sesion Referencia a la sesion
	 */
	public ComLs(String[] comando,DataInputStream dis, DataOutputStream dos,Sesion sesion) {
		super(comando,dis,dos,sesion);

	}
	
	/**
	 * Inicia la operacion siguiendo el protocolo LS (Ver estructura del protocolo en la documentacion)
	 */ 
	public void iniciar() {
		
		try {
			//escribir codigo de comando
			dos.writeUTF(Comando.LS);
			
			//esperar respuesta
			int res = dis.readInt();
			
			//si es afirmativa 
			if (res==Codigos.OK) {
				//leer CWD remoto
				String rutaActual=dis.readUTF();
				
				ArrayList<DtoArchivo> archivos=new ArrayList<DtoArchivo>();
				//leer cantidad de elementos
				int nArch = dis.readInt();
				
				//leer los elementos
				for (int i=0;i<nArch;i++) {
					String nombre= dis.readUTF();
					int codTipo=dis.readInt();
					archivos.add(new DtoArchivo(nombre, codTipo));
					
				}
				//actualizar interface grafica
				conector.actualizaListaArchivosRemotos(rutaActual,archivos);
				conector.msgInfo("LS correcto de ruta "+rutaActual);
				
			//si es negativa 
			}else {
				conector.msgError("LS erroneo. No existe la ruta");
			}
		} catch (IOException e) {
			sesion.logout();
			conector.msgError("LS erroneo");
		}
	}
	
}
