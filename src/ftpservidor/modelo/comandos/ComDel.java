/**
 * 
 */
package ftpservidor.modelo.comandos;

import java.io.File;
import java.io.IOException;

import ftpservidor.modelo.Codigos;
import ftpservidor.modelo.Sesion;
import ftpservidor.modelo.lib.Msg;
import ftpservidor.modelo.lib.UtilesArchivo;

/**
 *  Se encarga de manejar un comando DEL en el servidor
 *  
 * @author Jose Javier Bailon Ortiz
 */
public class ComDel extends Comando{
	 

	/**
	 * Constructor
	 * 
	 * @param sesion Sesion que realiza la operacion
	 */
	public ComDel(Sesion sesion) {
		super(sesion);
	}

	/**
	 * Inicia la operacion siguiendo el protocolo DEL 
	 * (Ver estructura del protocolo en la documentacion)
	 */
	public void iniciar() {
		//ruta de la carpeta de usuario
		String rutaUsuario = usuario.getDirUsuario();
		
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
