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

/**
 * Se encarga de manejar un comando LS en el servidor
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComLs extends Comando{
 

	/**
	 * Constructor
	 * 
	 * @param sesion Sesion que realiza la operacion
	 */
	public ComLs(Sesion sesion) {
		super(sesion);
	}

	/**
	 * Inicia la operacion siguiendo el protocolo LS (Ver estructura del protocolo
	 * en la documentacion)
	 */
	public void iniciar() {

		// Leer contenido de CWD
		String rutaCompleta = usuario.getDirUsuario() + sesion.getCwd();
		File[] archivos = new File(rutaCompleta).listFiles();

		try {
			// si es null es que la ruta no existe, avisar de error
			if (archivos == null) {
				Msg.msgHora(sesion.getDatosUsuario() + "LS erroneo en: " + rutaCompleta);
				dos.writeInt(Codigos.MAL);

			//si hay archivos enviarlos agregando ".." si no se esta en la raiz
			} else {
				dos.writeInt(Codigos.OK);
				dos.writeUTF(cwd);
				boolean esRaiz = cwd.equals("/") || cwd.equals("\\");
				
				//calcular cantidad e archivos
				// si es raiz n archivos
				int cantidadArchivos = 0;
				if (esRaiz)
					cantidadArchivos = archivos.length;
				else
					// si no es raiz n archivos +1 para incluir directorio padre
					cantidadArchivos= archivos.length + 1;

				dos.writeInt(cantidadArchivos);
				
				
				//si no es raiz enviar ".." como directorio
				if (!esRaiz) {
					dos.writeUTF("..");
					dos.writeInt(Codigos.DIRECTORIO);
				}
				
				//enviar archivos que hay en CWD
				for (File file : archivos) {
					dos.writeUTF(file.getName());
					dos.writeInt((file.isDirectory() ? Codigos.DIRECTORIO : Codigos.ARCHIVO));
				}
				Msg.msgHora(sesion.getDatosUsuario() + " LS exitoso en: " + rutaCompleta);
			}
			
		} catch (IOException e) {
			Msg.msgHora(sesion.getDatosUsuario() + " LS erroneo en: " + rutaCompleta);

		}

	}

}
