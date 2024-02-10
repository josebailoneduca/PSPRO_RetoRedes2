/**
 * 
 */
package ftpcliente.conector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import ftpcliente.Config;
import ftpcliente.controlador.Codigos;
import ftpcliente.controlador.Controlador;
import ftpcliente.controlador.dto.DtoArchivo;


/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Modelo {
	Controlador controlador;
    private Conexion conexion;
    boolean logged=false;
    String usuario="";
    
    
    
    
	public Modelo(Controlador controlador) {
		this.controlador=controlador;
		 
	}

	public boolean iniciarConexion(String host, int puerto) {
		Socket socketConexion=null;
		Socket socketOperaciones=null;
		try {
			//conexion inicial
			socketConexion= new Socket(host,puerto);
			InputStream is = socketConexion.getInputStream();
			OutputStream os = socketConexion.getOutputStream();
			DataInputStream dis = new DataInputStream(is);
			DataOutputStream dos = new DataOutputStream(os);
			int res = dis.readInt();
			//si respuesta afirmativa
			if (res==Codigos.OK) {
				//recoger puerto para operaciones y crear la nueva conexion al puerefimero del server
				int puertoOperaciones = dis.readInt();
				socketOperaciones = new Socket(host,puertoOperaciones);
				conexion=new Conexion(socketOperaciones,this);
				return true;
			//si respuesta negativa
			}else {
				socketConexion.close();
				return false;
			}
			
		} catch (IOException e) {
			//e.printStackTrace();
			try {
				if (socketConexion!=null)socketConexion.close();
				if (socketOperaciones!=null)socketOperaciones.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			return false;
		}
	}
	
	
	
	
	
	
	public void addOperacion(String operacion) {
		conexion.addOperacion(operacion);
	}
 
	/*
	 * @param rutaActual
	 * @param archivos
	 */
	public void actualizaLista(String rutaActual, ArrayList<DtoArchivo> archivos) {
		controlador.actualizaLista(rutaActual,archivos);
		
	}


 

	/**
	 * @param ok
	 * @param usuario
	 */
	public void setEstadoLogin(boolean ok, String usuario) {
		if(ok){
			logged=true;
		    this.usuario=usuario;
		}else {
		    logged=false;
		    usuario="";
		}
		controlador.actualizaLogin();
	}

	/**
	 * 
	 */
	public void malRegistro() {
		controlador.mensajeError("Registro erroneo");
		setEstadoLogin(false,null);

		
	}

	
	public boolean isLogged() {
		return logged;
	}
	
	/**
	 * @return
	 */
	public String getUsuario() {
		return usuario;
	}
	
	public int getPuerto() {
		if (conexion!=null)
			return conexion.getPuerto();
		else
			return 0;
	}

	public String getHost() {
		if (conexion!=null)
			return ""+conexion.getHost();
		else
			return "";
	}

	/**
	 * 
	 */
	public void logout() {
		conexion.logout();
	    boolean logged=false;
	    setEstadoLogin(false, "");
	    
		
	}

	/**
	 * @param string
	 */
	public void mensajeError(String string) {
		controlador.mensajeError(string);
		
	}

	/**
	 * 
	 */
	public void actualizarLocal() {
		controlador.actualizarLocal();
		
	}
	
	
}
