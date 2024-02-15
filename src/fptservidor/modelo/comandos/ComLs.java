/**
 * 
 */
package fptservidor.modelo.comandos;

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
	
 
	public Object iniciar() {
		String rutaCompleta = usuario.getCarpeta()+sesion.getCwd();
		File[] archivos = new File(rutaCompleta).listFiles();
		
		try {
			if (archivos==null) {
				dos.writeInt(Codigos.MAL);
				return null;
			}
			dos.writeInt(Codigos.OK);
			dos.writeUTF(sesion.getCwd());
			boolean esRaiz = sesion.getCwd().equals("/")||sesion.getCwd().equals("\\");
			//si es raiz n archivos
			if (esRaiz)
				dos.writeInt(archivos.length);
			else
				//si no es raiz n archivos +1 para directorio padre
				dos.writeInt(archivos.length+1);
			
			if (!esRaiz) {
				dos.writeUTF("..");
				dos.writeInt(Codigos.DIRECTORIO);
			}
			for (File file : archivos) {
				dos.writeUTF(file.getName());
				dos.writeInt((file.isDirectory()?Codigos.DIRECTORIO:Codigos.ARCHIVO));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
	}
	
}
