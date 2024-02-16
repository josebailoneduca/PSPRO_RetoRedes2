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
public class ComLs extends Comando{

	public ComLs(String[] comando,DataInputStream dis, DataOutputStream dos,Modelo modelo) {
		super(comando,dis,dos,modelo);

	}
	
 
	public void iniciar() {
		
		try {
			dos.writeUTF(TiposComando.LS);
			int res = dis.readInt();
			if (res==Codigos.OK) {
				String rutaActual=dis.readUTF();
				ArrayList<DtoArchivo> archivos=new ArrayList<DtoArchivo>();
				int nArch = dis.readInt();
				for (int i=0;i<nArch;i++) {
					String nombre= dis.readUTF();
					int codTipo=dis.readInt();
					archivos.add(new DtoArchivo(nombre, codTipo));
					
				}
				modelo.actualizaLista(rutaActual,archivos);
				modelo.msgInfo("LS correcto de ruta "+rutaActual);
			}else {
				modelo.msgError("LS erroneo");
			}
		} catch (IOException e) {
			modelo.setEstadoLogin(false, null);
			modelo.msgError("LS erroneo");
		}
	}
	
}
