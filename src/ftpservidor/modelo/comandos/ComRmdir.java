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
 * Se encarga de manejar un comando RMDIR en el servidor
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComRmdir extends Comando {
 
 
	
	/**
	 * Constructor
	 * 
	 * @param sesion Sesion que realiza la operacion
	 */
	public ComRmdir(Sesion sesion) {
		super(sesion);
	}

	
	/**
	 * Inicia la operacion siguiendo el protocolo RMDIR (Ver estructura del protocolo
	 * en la documentacion)
	 */
	public void iniciar() {
		String rutaUsuario = usuario.getDirUsuario();
		String rutaAEliminar;
		String rutaCompleta = null;
		try {
			// leer ruta
			rutaAEliminar = dis.readUTF();
			// componer y comprobar validez de ruta
			rutaCompleta = UtilesArchivo.componerRuta(rutaUsuario, cwd, rutaAEliminar);
			if (UtilesArchivo.rutaDentroDeRuta(rutaCompleta, rutaUsuario + "/")
					&& UtilesArchivo.rutaExiste(rutaCompleta)) {

				File f = new File(rutaCompleta);
				//comprobar que es directorio
				if (f.isDirectory()) {
					//si se borra correctamente se avisa
					if (f.delete()) {
						dos.writeInt(Codigos.OK);
						Msg.msgHora(sesion.getDatosUsuario() + " RMDIR exitoso en: " + rutaCompleta);
						
					//si no se borra es porque no esta vacio, se avisa	
					} else {
						dos.writeInt(Codigos.NO_VACIO);
						Msg.msgHora(sesion.getDatosUsuario() + " RMDIR bloqueado por no estar vacio: " + rutaCompleta);
					}
					
				//si no es directorio se avisa de que no existe como archivo	
				} else {
					dos.writeInt(Codigos.NO_EXISTE);
					Msg.msgHora(sesion.getDatosUsuario() + " RMDIR bloqueado por no ser directorio: " + rutaCompleta);
				}

			//Si la ruta no es valida avisamos de mal resultado	
			} else {
				dos.writeInt(Codigos.MAL);
				Msg.msgHora(
						sesion.getDatosUsuario() + " PUT bloqueado por ruta no permitida/existente: " + rutaCompleta);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
