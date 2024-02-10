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

import fptservidor.modelo.Codigos;
import fptservidor.modelo.Sesion;
import fptservidor.modelo.Usuario;
import fptservidor.modelo.lib.UtilesArchivo;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComDel {
	Usuario usuario;
	DataInputStream dis;
	DataOutputStream dos;
	Sesion sesion;
	String cwd;

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
		String cwd = sesion.getCwd();
		String rutaAEliminar;
		String rutaCompleta=null;
		try {
			//leer ruta
			rutaAEliminar = dis.readUTF();
			//componer y comprobar validez de ruta
			rutaCompleta = UtilesArchivo.componerRuta(rutaUsuario, cwd, rutaAEliminar);
			if (UtilesArchivo.rutaDentroDeRuta(rutaCompleta, rutaUsuario + "/") && 
					UtilesArchivo.rutaExiste(rutaCompleta) && eliminarArchivo(rutaCompleta)) {
						dos.writeInt(Codigos.OK);
			} else {
				dos.writeInt(Codigos.MAL);
				System.out.println("Archivo no eliminado");
			}
		} catch (IOException e) {
			e.printStackTrace();
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
