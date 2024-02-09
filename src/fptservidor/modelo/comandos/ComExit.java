/**
 * 
 */
package fptservidor.modelo.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import fptservidor.modelo.Codigos;
import fptservidor.modelo.Sesion;
import fptservidor.modelo.Usuario;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComExit {
	Usuario usuario;
	DataInputStream dis;
	DataOutputStream dos;
	Sesion sesion;
	String cwd;
	public ComExit(Sesion sesion) {
		super();
		this.sesion = sesion;
		this.usuario = sesion.getUsuario();
		this.dis = sesion.getDis();
		this.dos = sesion.getDos();
		this.cwd = sesion.getCwd();
	}
	
 
	public Object iniciar() {
		String rutaCompleta = usuario.getCarpeta()+sesion.getCwd();
		File[] archivos = new File(rutaCompleta).listFiles();
		this.sesion.exit();
		return null;
		
		
	}
	
}
