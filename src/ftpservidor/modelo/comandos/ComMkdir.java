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
 * Se encarga de manejar un comando MKDIR en el servidor
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComMkdir extends Comando{
 
	
	
	/**
	 * Constructor
	 * 
	 * @param sesion Sesion que realiza la operacion
	 */
	public ComMkdir(Sesion sesion) {
		super(sesion);
	}

	
	/**
	 * Inicia la operacion siguiendo el protocolo MKDIR (Ver estructura del protocolo
	 * en la documentacion)
	 */
	public void iniciar() {
		String rutaUsuario = usuario.getDirUsuario();
		// ruta dentro del usuario
		String directorioACrear;
		String rutaCompleta=null;
		try {
			//leer ruta a crear
			directorioACrear = dis.readUTF();
			
			//componer y comprobar ruta permitida
			rutaCompleta = UtilesArchivo.componerRuta(rutaUsuario, cwd, directorioACrear);
			if (UtilesArchivo.rutaDentroDeRuta(rutaCompleta, rutaUsuario + "/") ) {
				
				//si ya existe avisar
				if (UtilesArchivo.rutaExiste(rutaCompleta) ) {
					dos.writeInt(Codigos.YA_EXISTE);
					Msg.msgHora(sesion.getDatosUsuario()+" MKDIR erroneo por existir: "+rutaCompleta);
					
			    //si se crea bien
				}else if (crearDirectorio(rutaCompleta)) {
						dos.writeInt(Codigos.OK);
						Msg.msgHora(sesion.getDatosUsuario()+" MKDIR exitoso en ruta: "+rutaCompleta);
						
				//si no se crea bien		
				}else {
						dos.writeInt(Codigos.MAL);
						Msg.msgHora(sesion.getDatosUsuario()+" MKDIR erroneo en ruta: "+rutaCompleta);
				}
				
			//ruta no permitda	
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
	}

	/**
	 * Crea  un directorio y los directorios necesarios hasta llegar a el
	 * 
	 * @param rutaCompleta Ruta completa a crear
	 * 
	 * @return True si se ha creado, false si no se ha creado
	 */
	private boolean crearDirectorio(String rutaCompleta) {
		File f = new File(rutaCompleta);
		return f.mkdirs();
	}

}
