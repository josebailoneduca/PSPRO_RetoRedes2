/**
 * 
 */
package fptservidor.modelo.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import fptservidor.Msg;
import fptservidor.modelo.Codigos;
import fptservidor.modelo.Sesion;
import fptservidor.modelo.Usuario;
import fptservidor.modelo.lib.UtilesArchivo;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComCd {
	private Usuario usuario;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Sesion sesion;
	private String cwd;

	public ComCd(Sesion sesion) {
		super();
		this.sesion = sesion;
		this.usuario = sesion.getUsuario();
		this.dis = sesion.getDis();
		this.dos = sesion.getDos();
		this.cwd = sesion.getCwd();
	}

	public Object iniciar() {
		//ruta de la carpeta de usuario
		String rutaUsuario = usuario.getCarpeta();
		// nueva ruta a establecer(relativa a CWD o la de usuario)
		String nuevaRuta;
		
		//comosicon final de la nueva ruta
		String rutaCompleta=null;
		try {
			//leer ruta
			nuevaRuta = dis.readUTF();
			//componer y comprobar validez de ruta
			rutaCompleta = UtilesArchivo.componerRuta(rutaUsuario, cwd, nuevaRuta);
			
			if (UtilesArchivo.rutaDentroDeRuta(rutaCompleta, rutaUsuario + "/") && 
					UtilesArchivo.rutaExiste(rutaCompleta) &&
					sesion.setCwd(rutaCompleta.replace(rutaUsuario, ""))) {
				dos.writeInt(Codigos.OK);
				Msg.msgHora(sesion.getDatosUsuario()+" CD exitoso a "+sesion.getCwd());
			} else {
				dos.writeInt(Codigos.MAL);
				Msg.msgHora(sesion.getDatosUsuario()+"CD erroneo a "+rutaCompleta);
			}
		} catch (IOException e) {
			Msg.msgHora(sesion.getDatosUsuario()+"CD erroneo a "+rutaCompleta);
			try {
				dos.writeInt(Codigos.MAL);
			} catch (IOException e1) {
			}
		}
		return null;
	}

}
