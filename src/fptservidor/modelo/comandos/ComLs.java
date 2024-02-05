/**
 * 
 */
package fptservidor.modelo.comandos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;

import fptservidor.modelo.Sesion;
import fptservidor.modelo.Usuario;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComLs {
	Usuario usuario;
	DataInputStream dis;
	DataOutputStream dos;
	Sesion sesion;
	String cwd;
	public ComLs(Sesion sesion) {
		super();
		this.sesion = sesion;
		this.usuario = sesion.getUsuario();
		this.dis = sesion.getDis();
		this.dos = sesion.getDos();
		this.cwd = sesion.getCwd();
	}
	
	/**
	 * @param usuario2
	 * @param sesion
	 */
	public ComLs(Usuario usuario2, Sesion sesion) {
		// TODO Auto-generated constructor stub
	}

	public void iniciar() {
		String ruta = usuario.getCarpeta()+sesion.getCwd();
		
		ArrayList<File> listaArchivos = new ArrayList<File>();
		
	}
	
}
