/**
 * 
 */
package ftpservidor.modelo.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import ftpservidor.Msg;
import ftpservidor.modelo.Codigos;
import ftpservidor.modelo.Sesion;
import ftpservidor.modelo.Usuario;
import ftpservidor.modelo.lib.UtilesArchivo;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComMkdir {
	private Usuario usuario;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Sesion sesion;
	private String cwd;

	public ComMkdir(Sesion sesion) {
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
		String directorioACrear;
		String rutaCompleta=null;
		try {
			//leer ruta
			directorioACrear = dis.readUTF();
			//componer y comprobar validez de ruta
			rutaCompleta = UtilesArchivo.componerRuta(rutaUsuario, cwd, directorioACrear);
			if (UtilesArchivo.rutaDentroDeRuta(rutaCompleta, rutaUsuario + "/") ) {
				if (UtilesArchivo.rutaExiste(rutaCompleta) ) {
					dos.writeInt(Codigos.YA_EXISTE);
					Msg.msgHora(sesion.getDatosUsuario()+" MKDIR erroneo por existir: "+rutaCompleta);
				}else if (crearDirectorio(rutaCompleta)) {
						dos.writeInt(Codigos.OK);
						Msg.msgHora(sesion.getDatosUsuario()+" MKDIR exitoso en ruta: "+rutaCompleta);
				}else {
						dos.writeInt(Codigos.MAL);
						Msg.msgHora(sesion.getDatosUsuario()+" MKDIR erroneo en ruta: "+rutaCompleta);
				}
			} else {
				Msg.msgHora(sesion.getDatosUsuario()+" MKDIR bloqueado en ruta no permitida: "+rutaCompleta);
				dos.writeInt(Codigos.MAL);
			}
		} catch (IOException e) {
			Msg.msgHora(sesion.getDatosUsuario()+" MKDIR erroneo en ruta: "+rutaCompleta);
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
	private boolean crearDirectorio(String rutaCompleta) {
		File f = new File(rutaCompleta);
		return f.mkdirs();
	}

}
