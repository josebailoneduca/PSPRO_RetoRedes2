/**
 * 
 */
package ftpcliente.modelo.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import fptservidor.modelo.Codigos;
import fptservidor.modelo.Sesion;
import fptservidor.modelo.Usuario;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComLs {
	Usuario usuario;
	DataInputStream dis;
	DataOutputStream dos;
	Sesion sesion;
	String cwd;
	public ComLs(Sesion sesion) {
		super();
		this.sesion = sesion;
		this.usuario = sesion.getUsuario();
		this.dis = sesion.getDis();
		this.dos = sesion.getDos();
		this.cwd = sesion.getCwd();
	}
	
 
	public void iniciar() {
		String ruta = usuario.getCarpeta()+sesion.getCwd();
		File[] archivos = new File(ruta).listFiles();
		try {
			if (archivos==null) {
				dos.writeInt(Codigos.MAL);
				return;
			}
			dos.writeInt(Codigos.OK);
			dos.writeUTF(sesion.getCwd());
			dos.writeInt(archivos.length);
			
			for (File file : archivos) {
				dos.writeUTF(file.getName());
				dos.writeInt((file.isDirectory()?Codigos.DIRECTORIO:Codigos.ARCHIVO));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
