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
 *  Se encarga de manejar un comando DEL en el servidor
 *  
 * @author Jose Javier Bailon Ortiz
 */
public class ComDel {
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
	public ComDel(Sesion sesion) {
		super();
		this.sesion = sesion;
		this.usuario = sesion.getUsuario();
		this.dis = sesion.getDis();
		this.dos = sesion.getDos();
		this.cwd = sesion.getCwd();
	}

	/**
	 * Inicia la operacion siguiendo el protocolo DEL 
	 * (Ver estructura del protocolo en la documentacion)
	 */
	public void iniciar() {
		//ruta de la carpeta de usuario
		String rutaUsuario = usuario.getCarpeta();
		
		// ruta a eliminar suministrada por el comando
		String rutaAEliminar;
		
		//ruta a elmininar completa
		String rutaCompleta=null;
		try {
			//leer ruta
			rutaAEliminar = dis.readUTF();
			
			//componer ruta completa
			rutaCompleta = UtilesArchivo.componerRuta(rutaUsuario, cwd, rutaAEliminar);
			
			//si la ruta no es autorizada contestar MAL
			if (!UtilesArchivo.rutaDentroDeRuta(rutaCompleta, rutaUsuario + "/")){
				dos.writeInt(Codigos.MAL);
				Msg.msgHora(sesion.getDatosUsuario()+" DEL bloqueado en ruta no permitida:"+rutaCompleta);
				
			//si la ruta no existe contestar NO_EXISTE
			} else if(!UtilesArchivo.rutaExiste(rutaCompleta)) {
				Msg.msgHora(sesion.getDatosUsuario()+" DEL no existente:"+rutaCompleta);
				dos.writeInt(Codigos.NO_EXISTE);
			//si se elimina el archivo contestar OK
			} else if(eliminarArchivo(rutaCompleta)) {
						dos.writeInt(Codigos.OK);
						Msg.msgHora(sesion.getDatosUsuario()+" DEL exitoso: "+rutaCompleta);
			//en cualquier otro caso contestar MAL
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
	}

	/**
	 * Ejecuta la eliminacion del archivo si no es un directorio
	 * 
	 * @param rutaCompleta Ruta del archivo a elminiar
	 * 
	 * @return True si se ha eliminado, false si no se ha eliminado
	 */
	private boolean eliminarArchivo(String rutaCompleta) {
		File f = new File(rutaCompleta);
		if (!f.isDirectory())
			return f.delete();
		return false;
	}

}
