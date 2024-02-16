/**
 * 
 */
package fptservidor.modelo.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
public class ComDel {
	private Usuario usuario;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Sesion sesion;
	private String cwd;

	public ComDel(Sesion sesion) {
		super();
		this.sesion = sesion;
		this.usuario = sesion.getUsuario();
		this.dis = sesion.getDis();
		this.dos = sesion.getDos();
		this.cwd = sesion.getCwd();
	}

	public Object iniciar() {
		String rutaUsuario = usuario.getCarpeta();
		// ruta dentro del usuario
		String rutaAEliminar;
		String rutaCompleta=null;
		try {
			//leer ruta
			rutaAEliminar = dis.readUTF();
			//componer y comprobar validez de ruta
			rutaCompleta = UtilesArchivo.componerRuta(rutaUsuario, cwd, rutaAEliminar);
			if (!UtilesArchivo.rutaDentroDeRuta(rutaCompleta, rutaUsuario + "/")){
				dos.writeInt(Codigos.MAL);
				Msg.msgHora(sesion.getDatosUsuario()+" DEL bloqueado en ruta no permitida:"+rutaCompleta);
			} else if(!UtilesArchivo.rutaExiste(rutaCompleta)) {
				Msg.msgHora(sesion.getDatosUsuario()+" DEL no existente:"+rutaCompleta);
				dos.writeInt(Codigos.NO_EXISTE);
			} else if(eliminarArchivo(rutaCompleta)) {
						dos.writeInt(Codigos.OK);
						Msg.msgHora(sesion.getDatosUsuario()+" DEL exitoso: "+rutaCompleta);
			} else {
				dos.writeInt(Codigos.MAL);
				Msg.msgHora(sesion.getDatosUsuario()+" DEL erroneo: "+rutaCompleta);

			}
		} catch (IOException e) {
			Msg.msgHora(sesion.getDatosUsuario()+" DEL erroneo: "+rutaCompleta);

			try {
				dos.writeInt(Codigos.MAL);
			} catch (IOException e1) {
			}
		}
		return null;

	}

	/**
	 * @param rutaCompleta
	 * @return
	 */
	private boolean eliminarArchivo(String rutaCompleta) {
		File f = new File(rutaCompleta);
		if (!f.isDirectory())
			return f.delete();
		return false;
	}

}
