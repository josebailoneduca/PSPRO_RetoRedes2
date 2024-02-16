/**
 * 
 */
package ftpservidor.modelo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ftpservidor.Config;

/**
 * Objeto usuario que contiene los datos necesarios sobre un usuario.
 * 
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Usuario {
	
	/**
	 * Carpeta del usuario
	 */
	private String directorioUsuario = "";
	
	/**
	 * Nombre del usuario
	 */
	private String nombreUsuario = "";
	
	/**
	 * True si es anonimo, false si no lo es
	 */
	private boolean anonimo = false;

	
	/**
	 * Constructor de usuario normal
	 * 
	 * @param usuario Nombre del usuario
	 */
	public Usuario(String usuario) {
		this.directorioUsuario = Config.getRUTA_ALMACENAMIENTO() + "/" + usuario;
		this.nombreUsuario = usuario;
	}

	
	/**
	 * Constructor sin nombre para usuarios anonimos
	 */
	public Usuario() {
		this.directorioUsuario = Config.getRUTA_ALMACENAMIENTO_ANONIMO();
		this.nombreUsuario = Config.getNOMBRE_USUARIO_ANONIMO();
		this.anonimo = true;
	}

	
	/**
	 * Comprueba unos datos de login
	 * 
	 * @param contrasena Contrasena a comprobar
	 * 
	 * @return True si es correcta, false si no lo es
	 */
	public boolean login(String contrasena) {
		if (esAnonimo())
			return true;
		else {
			String contrasenaLocal = getContrasenaLocal();
			if (contrasenaLocal == null)
				return false;
			else
				return contrasenaLocal.equals(contrasena);
		}
	}

	/**
	 * Devuelve el directorio de usuario
	 * 
	 * @return La ruta al directorio
	 */
	public String getDirUsuario() {
		return directorioUsuario;
	}

	/**
	 * Devuelve el nombre del usuario
	 * 
	 * @return El nombre
	 */
	public String getNombreUsuario() {
		return nombreUsuario;
	}

	
 

 
	/**
	 * Devuelve la contrasena guardada en su archivo
	 * 
	 * @return La contrasena o null si no existe
	 */
	private String getContrasenaLocal() {
		File f = new File(Config.getRUTA_ALMACENAMIENTO() + "/" + nombreUsuario + ".pass");
		try (FileReader fr = new FileReader(f); BufferedReader br = new BufferedReader(fr)) {
			String contrasena = "";
			contrasena = br.readLine();
			if (contrasena != null && contrasena.length() > 0)
				return contrasena;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	
	/**
	 * True si es anonimo, false si no lo es
	 * 
	 * @return True si es anonimo, false si no lo es
	 */
	public boolean esAnonimo() {
		return anonimo;
	}

	
	@Override
	public String toString() {
		return nombreUsuario;
	}

}
