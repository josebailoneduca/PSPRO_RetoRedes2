/**
 * 
 */
package ftpcliente.conector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Controla el estado de la sesion y activa y para el procesador de operaciones asociado
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Sesion {
	/**
	 * Socket de comunicacion con el servidor
	 */
	private Socket socket;
	
	/**
	 * Procesador de operaciones encargado de efectuar la comunicacion
	 */
	private ProcesadorOperaciones procOperaciones;
	
	/**
	 * Buffer de operaciones a realizar
	 */
	private LinkedBlockingQueue<String> operacionesPendientes;
	
	/**
	 * InputStream del socket
	 */
	private InputStream is;
	
	/**
	 * OutputStream del socket
	 */
	private OutputStream os;
	
	/**
	 * DataInputStream que envuelve el input stream del socket
	 */
	private DataInputStream dis;
	
	/**
	 * DataOutputStream que envuelve el outputstream del socket
	 */
	private DataOutputStream dos;
	
	/**
	 * Referencia al conector
	 */
	private Conector conector;
	
	/**
	 * True si hay sesion iniciada en el servidor
	 */
	private boolean logged;
	
	/**
	 * Nombre del usuario que tiene iniciada la sesion
	 */
	private String usuario;

	
	/**
	 * Constructor
	 * 
	 * @param socket Socket de comunicacion con el servidor
	 * @param conector Referencia al conector
	 */
	public Sesion(Socket socket, Conector conector) {
		
		//inicializar variables y envolver streams
		operacionesPendientes = new LinkedBlockingQueue<String>();
		this.conector = conector;
		logged=false;

		this.socket = socket;
		try {
			is = socket.getInputStream();
			os = socket.getOutputStream();
			dis = new DataInputStream(is);
			dos = new DataOutputStream(os);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
		
		//crear y poner a funcionar el procesador de operaciones
		procOperaciones = new ProcesadorOperaciones(operacionesPendientes, dis, dos, this);
		procOperaciones.start();
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
		    conector.msgInfo("Desconectado");
		    
		}
		conector.actualizaLogin();
	}
	
	/**
	 * Agrega una operacion al buffer de operacinones
	 * 
	 * @param operacion El comando/operacion a agregar
	 */
	public void addOperacion(String operacion) {
		operacionesPendientes.add(operacion);

	}

	/**
	 * Devuelve el puerto remoto de conexion del socket
	 * 
	 * @return El puerto
	 */
	public int getPuerto() {
		if (socket != null)
			return socket.getPort();
		else
			return -1;
	}

	/**
	 * Devuelve el host con el que esta conectado el socket
	 * 
	 * @return El host
	 */
	public String getHost() {
		if (socket != null)
			return socket.getRemoteSocketAddress().toString();
		else
			return "";
	}

	/**
	 * Cierra el socket, para el procesador de operaciones y establece el estado de sesion no iniciada
	 */
	public void logout() {
		try {
			socket.close();
		} catch (IOException e) {
		}

		operacionesPendientes.clear();
		procOperaciones.parar();
		procOperaciones.interrupt();
		setEstadoLogin(false,null);
	}


	/**
	 * Devuelve el conector(usado por las operaciones)
	 * 
	 * @return El conector
	 */
	public Conector getConector() {
		return conector;
	}

	/**
	 * True si esta conectado y false si no esta conectado(independientemente de si se esta logueado o no)
	 * 
	 * @return True si esta conectado y false si no esta conectado
	 */
	public boolean isConectado() {
		if (this.socket!=null &&
				this.socket.isConnected() &&
				!this.socket.isClosed()
				)
			return true;
		else
		return false;
	}

	
	/**
	 * Devuelve el atributo logged. True cuando se ha iniciado sesion
	 * @return El atributo
	 */
	public boolean isLogged() {
		return isConectado()&&logged;
	}


	/**
	 * Devuelve el nombre de usuario o cadena vacia si no se esta logueado
	 * 
	 * @return El nombre de usuario
	 */
	public String getUsuario() {
		if (isLogged())
			return usuario;
		else 
			return "";
	}
}
