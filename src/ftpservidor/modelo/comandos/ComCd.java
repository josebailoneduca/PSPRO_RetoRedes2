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
import ftpservidor.modelo.lib.UtilesArchivo;

/**
 *  Se encarga de manejar un comando CD en el servidor
 *  
 * @author Jose Javier Bailon Ortiz
 */
public class ComCd {
	/**
	 * Usuario que ejecuta el comando
	 */
	private Usuario usuario;
	
	/**
	 * DataInputStream de la operacion
	 */
	private DataInputStream dis;
	
	/**
	 * DataOutputStream de la operacion
	 */
	private DataOutputStream dos;
	
	/**
	 * Sesion de la operacion
	 */
	private Sesion sesion;
	
	/**
	 * CWD de la sesion
	 */
	private String cwd;

	
	/**
	 * Constructor
	 * 
	 * @param sesion Sesion que realiza la operacion
	 */
	public ComCd(Sesion sesion) {
		super();
		this.sesion = sesion;
		this.usuario = sesion.getUsuario();
		this.dis = sesion.getDis();
		this.dos = sesion.getDos();
		this.cwd = sesion.getCwd();
	}

	
	/**
	 * Inicia la operacion siguiendo el protocolo CD (Ver estructura del protocolo en la documentacion)
	 */
	public void iniciar() {
		//ruta de la carpeta de usuario
		String rutaUsuario = usuario.getCarpeta();
		// nueva ruta a establecer(relativa a CWD o la de usuario)
		String nuevaRuta;
		
		//composicon final de la nueva ruta
		String rutaCompleta=null;
		try {
			//leer ruta
			nuevaRuta = dis.readUTF();
			//componer y comprobar validez de ruta
			rutaCompleta = UtilesArchivo.componerRuta(rutaUsuario, cwd, nuevaRuta);
			
			//si la ruta es autorizada y existe establece un nuevo CWD de la sesion
			//y devuelve un resultado OK
			if (UtilesArchivo.rutaDentroDeRuta(rutaCompleta, rutaUsuario + "/") && 
					UtilesArchivo.rutaExiste(rutaCompleta) &&
					sesion.setCwd(rutaCompleta.replace(rutaUsuario, ""))) {
				dos.writeInt(Codigos.OK);
				Msg.msgHora(sesion.getDatosUsuario()+" CD exitoso a "+rutaCompleta);
			//en otro caso devuelve un resultado MAL
			} else {
				dos.writeInt(Codigos.MAL);
				Msg.msgHora(sesion.getDatosUsuario()+" CD erroneo a "+rutaCompleta);
			}
		} catch (IOException e) {
			Msg.msgHora(sesion.getDatosUsuario()+" CD erroneo a "+rutaCompleta);
			try {
				dos.writeInt(Codigos.MAL);
			} catch (IOException e1) {
			}
		}
	}

}
