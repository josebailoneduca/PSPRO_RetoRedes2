/**
 * 
 */
package ftpservidor.modelo.comandos;

import java.io.IOException;
import ftpservidor.Msg;
import ftpservidor.modelo.Codigos;
import ftpservidor.modelo.Sesion;
import ftpservidor.modelo.lib.UtilesArchivo;

/**
 *  Se encarga de manejar un comando CD en el servidor
 *  
 * @author Jose Javier Bailon Ortiz
 */
public class ComCd extends Comando{

	
	/**
	 * Constructor
	 * 
	 * @param sesion Sesion que realiza la operacion
	 */
	public ComCd(Sesion sesion) {
		super(sesion);
	}

	
	/**
	 * Inicia la operacion siguiendo el protocolo CD (Ver estructura del protocolo en la documentacion)
	 */
	public void iniciar() {
		//ruta de la carpeta de usuario
		String rutaUsuario = usuario.getDirUsuario();
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
