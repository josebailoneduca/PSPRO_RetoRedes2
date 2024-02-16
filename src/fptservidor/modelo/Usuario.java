/**
 * 
 */
package fptservidor.modelo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import fptservidor.Config;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Usuario {
	private String carpeta="";
	private String nombreUsuario="";
	private boolean anonimo=false;

	public Usuario(String usuario) {
		this.carpeta = Config.getRUTA_ALMACENAMIENTO()+"/"+usuario;
		this.nombreUsuario = usuario;
	}
	public Usuario() {
		this.carpeta = Config.getRUTA_ALMACENAMIENTO_ANONIMO();
		this.nombreUsuario = Config.getNOMBRE_USUARIO_ANONIMO();
		this.anonimo = true;
	}
	
	public boolean login(String contrasena) {
		if (esAnonimo())
			return true;
		else {
			String contrasenaLocal=getContrasenaLocal();
			return contrasenaLocal.equals(contrasena);
		}
	}
	
	
	
	public String getCarpeta() {
		return carpeta;
	}
 
	public String getNombreUsuario() {
		return nombreUsuario;
	}
 
	public boolean isAnonimo() {
		return anonimo;
	}
	public void setAnonimo(boolean anonimo) {
		this.anonimo = anonimo;
	}
	/**
	 * @return
	 */
	private String getContrasenaLocal() {
		File f =new File(Config.getRUTA_ALMACENAMIENTO()+"/"+nombreUsuario+".pass");
		try (FileReader fr = new FileReader(f); BufferedReader br = new BufferedReader(fr)){
			String contrasena="";
			contrasena=br.readLine();
			if (contrasena!=null && contrasena.length()>0)
				return contrasena;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}  
		
		return null;
	}
	public boolean esAnonimo() {
		return anonimo;
	}
	@Override
	public String toString() {
		return nombreUsuario ;
	}
	
	
}
