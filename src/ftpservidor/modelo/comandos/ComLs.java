/**
 * 
 */
package ftpservidor.modelo.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ftpservidor.Msg;
import ftpservidor.modelo.Codigos;
import ftpservidor.modelo.Sesion;
import ftpservidor.modelo.Usuario;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComLs {
	private Usuario usuario;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Sesion sesion;
	private String cwd;
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
				Msg.msgHora(sesion.getDatosUsuario()+"LS erroneo en: "+rutaCompleta);
				dos.writeInt(Codigos.MAL);
				return null;
			}
			dos.writeInt(Codigos.OK);
			dos.writeUTF(cwd);
			boolean esRaiz = cwd.equals("/")||cwd.equals("\\");
			//si es raiz n archivos
			if (esRaiz)
				dos.writeInt(archivos.length);
			else
				//si no es raiz n archivos +1 para incluir directorio padre
				dos.writeInt(archivos.length+1);
			
			if (!esRaiz) {
				dos.writeUTF("..");
				dos.writeInt(Codigos.DIRECTORIO);
			}
			for (File file : archivos) {
				dos.writeUTF(file.getName());
				dos.writeInt((file.isDirectory()?Codigos.DIRECTORIO:Codigos.ARCHIVO));
			}
			Msg.msgHora(sesion.getDatosUsuario()+" LS exitoso en: "+rutaCompleta);
		} catch (IOException e) {
			Msg.msgHora(sesion.getDatosUsuario()+" LS erroneo en: "+rutaCompleta);

		}
		return null;
		
		
	}
	
}
