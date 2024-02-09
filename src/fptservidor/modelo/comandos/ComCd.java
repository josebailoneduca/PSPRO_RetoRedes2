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
import fptservidor.modelo.lib.UtilesArchivo;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComCd {
	Usuario usuario;
	DataInputStream dis;
	DataOutputStream dos;
	Sesion sesion;
	String cwd;

	public ComCd(Sesion sesion) {
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
		String nuevaRuta;
		String rutaCompleta=null;
		try {
			//leer ruta
			nuevaRuta = dis.readUTF();
			//componer y comprobar validez de ruta
			rutaCompleta = UtilesArchivo.componerRuta(rutaUsuario, cwd, nuevaRuta);
			if (UtilesArchivo.rutaDentroDeRuta(rutaCompleta, rutaUsuario + "/") && 
					UtilesArchivo.rutaExistente(rutaCompleta) &&
					sesion.setCwd(rutaCompleta.replace(rutaUsuario, ""))) {
				dos.writeInt(Codigos.OK);
			} else {
				dos.writeInt(Codigos.MAL);
				System.out.println("Ruta noc cambiada");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

}
