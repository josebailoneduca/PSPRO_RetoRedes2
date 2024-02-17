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
 * Genera el inicio de la conexion con el puerto public fijo del servidor.
 * Una vez recibe la informacion del puerto efimero crea la conexion con el puerto
 * efimero y pasa la gestion de la conexiona una Sesion. Esta se encarga
 * de llevar a cabo las operaciones.
 * 
 * Ademas esta clase sirve de contros de estado de la conexion con el servidor
 * y sirve de puente entre la sesion y el controlador.
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Modelo {
	
	/**
	 * Referencia al controlador
	 */
	private Controlador controlador;
	
	/**
	 * Referencia a la sesion
	 */
    private Sesion sesion;
    
    /**
     * Estado de logueado
     */
    private boolean logged=false;
    
    /**
     * Nombre de usuario actual
     */
    private String usuario="";
    
    /**
     * Socket usandose para las operaciones
     */
    private Socket socketOperaciones;
    
    
    
    /**
     * Constructor
     * 
     * @param controlador Referencia al controlador
     */
	public Modelo(Controlador controlador) {
		this.controlador=controlador;
	}

	/**
	 * Inicia la conexion con el servidor
	 * 
	 * @param host Host del servidor
	 * @param puerto puerto
	 * @return True si ha podido iniciar la conexion, false si no se ha realizado
	 */
	public boolean iniciarConexion(String host, int puerto) {
		Socket socketConexion=null;
		socketOperaciones=null;
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
				//recoger puerto para operaciones y crear la nueva conexion a ese puerto efimero del server
				int puertoOperaciones = dis.readInt();
				socketOperaciones = new Socket(host,puertoOperaciones);
				sesion=new Sesion(socketOperaciones,this);
				return true;
				
			//si respuesta negativa cerrar el socket
			}else {
				socketConexion.close();
				return false;
			}
			
		} catch (IOException e) {
			try {
				if (socketConexion!=null)socketConexion.close();
				if (socketOperaciones!=null)socketOperaciones.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			return false;
		}
	}
	
	
	/**
	 * Agrega una operaciona a realizar por la sesion si existe
	 * 	
	 * @param operacion La operacion a realizar
	 */
	public void addOperacion(String operacion) {
		if (sesion!=null) {
			sesion.addOperacion(operacion);
	}
	}
 
	/*
	 * Ordena a controlador actualizar la lista de archivos remotos vista por el usuario
	 * 
	 * @param rutaActual Ruta actual
	 * @param archivos Lista de archivos
	 */
	public void actualizaListaArchivosRemotos(String rutaActual, ArrayList<DtoArchivo> archivos) {
		controlador.actualizaListaArchivosRemotos(rutaActual,archivos);
	}


 

	/**
	 * Define el estado del login de cara al usuario
	 * 
	 * @param ok True si esta iniciada, false si no lo esta
	 * @param usuario Nombre de usuario a usar si esta iniciada
	 */
	public void setEstadoLogin(boolean ok, String usuario) {
		if(ok){
			logged=true;
		    this.usuario=usuario;
		}else {
		    logged=false;
		    usuario="";
		    msgInfo("Desconectado");
		}
		controlador.actualizaLogin();
	}
 

	/**
	 * Devuelve el atributo logged. True cuando se ha iniciado sesion
	 * @return El atributo
	 */
	public boolean isLogged() {
		return logged;
	}
	
	/**
	 * Devuelve el nombre de usuario
	 * 
	 * @return El nombre de usuario
	 */
	public String getUsuario() {
		return usuario;
	}
	
 
	/**
	 * Devuelve el host del servidor
	 * 
	 * @return El host
	 */
	public String getHost() {
		if (sesion!=null)
			return ""+sesion.getHost();
		else
			return "";
	}

	/**
	 * Cierra la sesion
	 */
	public void logout() {
		sesion.logout();
	    boolean logged=false;
	    setEstadoLogin(false, "");
	    
		
	}

	/**
	 * Muestra un mensaje de error
	 * 
	 * @param string El mensaje
	 */
	public void msgError(String string) {
		controlador.mensajeError(string);
		
	}

	/**
	 * Muestra un mensaje informatifo
	 * 
	 * @param string El mensaje
	 */
	public void msgInfo(String msg) {
		controlador.mensajeInfo(msg);
	}

	
	/**
	 * Ordena la actualizacion de archivos locales en el cliente
	 */
	public void actualizarArchivosLocales() {
		controlador.actualizarArchivosLocalesl();
	}
	
	/**
	 * Pide una confirmacion
	 * @param msg El mensaje para confirmar
	 * 
	 * @return True si se ha confirmado, false si no se ha confirmado
	 */
	public boolean confirmar(String msg) {
		return controlador.confirmar(msg);
	}


	/**
	 * Devuelve si se esta conectado o no
	 * 
	 * @return True si se esta conectado, false si no se esta
	 */
	public boolean isConectado() {
		if (this.socketOperaciones!=null &&
				this.socketOperaciones.isConnected() &&
				!this.socketOperaciones.isClosed()
				)
			return true;
		else
		return false;
	}
}
