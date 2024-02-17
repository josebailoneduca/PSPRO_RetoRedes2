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
import ftpcliente.conector.comandos.Comando;
import ftpcliente.controlador.Controlador;
import ftpcliente.controlador.dto.DtoArchivo;


/**
 * Genera el inicio de la conexion con el puerto public fijo del servidor.
 * Una vez recibe la informacion del puerto efimero crea la conexion con el puerto
 * efimero y pasa la gestion de la conexiona una Sesion. Esta se encarga
 * de llevar a cabo las operaciones.
 * 
 * 
 * Ademas esta clase sirve de puente entre la sesion y el controlador enviando los comandos
 * desde el controlador a la sesion y los mensajes para el usuario desde la sesion al controlador.
 * 
 *  En el caso de los comandos de login y registro esta clase se encarga de establecer la conexion inicial
 *  con el servidor y transformar los comandos suministrados por el controlador en comandos
 *  de login y registro segun el protocolo ya que los comandos de login y registro que llegan
 *  desde controlador contienen la informacion tanto para la conexion inicial como para los comandos en si 
 *  de login y de registro. Ver los comandos en la documentacion para ver esa diferenciacion entre conexion
 *  y login/registro en detalle.
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Conector {
	
	/**
	 * Referencia al controlador
	 */
	private Controlador controlador;
	
	/**
	 * Referencia a la sesion
	 */
    private Sesion sesion;
    


    /**
     * Constructor
     * 
     * @param controlador Referencia al controlador
     */
	public Conector(Controlador controlador) {
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
		Socket socketOperaciones=null;
		try {
			//conexion inicial
			socketConexion= new Socket(host,puerto);
			InputStream is = socketConexion.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			int res = dis.readInt();
			//si respuesta afirmativa
			if (res==Codigos.OK) {
				//recoger puerto para operaciones y crear la nueva conexion a ese puerto efimero del server
				int puertoOperaciones = dis.readInt();
				socketOperaciones = new Socket(host,puertoOperaciones);
				//asegurar el cierre de sesion si hubiese
				if (sesion!=null && sesion.isConectado())
						sesion.logout();
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
	 * Agrega una operacion a realizar por la sesion si existe.
	 * En caso de comandos de login y registro asegura primero que se crea
	 * la conexion con el servidor en caso de no existir aun esa conexion.
	 * 	
	 * @param operacion La operacion a realizar
	 */
	public void addOperacion(String operacion) {
		String[] op = ProcesadorOperaciones.extraerPartesComando(operacion);
		//si no hay identificador de operacion terminar
		if (op.length<1)
			return;
		
		if (op[0].toUpperCase().equals(Comando.LOGIN)) {
			gestionaLogin(op);
			return;
		}
		else if (op[0].toUpperCase().equals(Comando.REGISTRO)) {
			gestionaRegistro(op);
			return;
		}
		else if (sesion!=null && sesion.isLogged()){
			sesion.addOperacion(operacion);
		}else {
			msgError("Debe iniciar sesión");
		}
	}
	
	/**
	 * Gestiona la peticion de registro de un nuevo usuario
	 * 
	 * @param operacion La operacion de registro separada en sus componentes
	 */
	private void gestionaRegistro(String[] operacion) {
		//iniciar la conexion  si no esta iniciada
		if (sesion==null || !sesion.isConectado()) {
			if (operacion.length<5)
				return;
			else {
				String host="";
				int puerto=0;
				try {
				host = operacion[1];
				puerto = Integer.parseInt(operacion[2]);
				}catch(NumberFormatException ex) {
					return;
				}
				if (!iniciarConexion(host, puerto))
					return;
			}
		} 
		//agregar operacion de registro si no se esta logueado
		if (!sesion.isLogged())
			sesion.addOperacion(operacion[0]+" "+operacion[3]+" "+operacion[4]);
		else
			msgError("No puedes registrar un nuevo usuario con la sesion ya iniciada");
	}
 
	/**
	 * Gestiona la peticion de login  
	 * 
	 * @param operacion La operacion de login separada en sus componentes
	 */
	private void gestionaLogin(String[] operacion) {

		//iniciar la conexion si no esta iniciada
		if (sesion==null || !sesion.isConectado()) {
			if (operacion.length<3)
				return;
			else {
				String host="";
				int puerto=0;
				try {
				host = operacion[1];
				puerto = Integer.parseInt(operacion[2]);
				}catch(NumberFormatException ex) {
					msgError("No se puede conectar a "+operacion[1]+":"+operacion[2]);
					return;
				}
				if (!iniciarConexion(host, puerto)) {
					msgError("No se puede conectar a "+operacion[1]+":"+operacion[2]);
					return;
				}
			}
		}
		
		
		//agregar instruccion de login a la cola si no esta logeado
		if (sesion!=null && sesion.isConectado() && !sesion.isLogged()) {
			
			//login anonimo
			if (operacion.length<4) {
				sesion.addOperacion(Comando.LOGIN+" "+Codigos.LOGIN_ANONIMO);
				
				
			//login con usuario
			}else if (operacion.length>=4){
				sesion.addOperacion(Comando.LOGIN+" "+Codigos.LOGIN_NORMAL+" "+operacion[3]+" "+operacion[4]);
			}
			
		}else {
			msgError("Cierra la sesion actual antes de iniciar una nueva");
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
	 * Ordena la actualizacion de archivos locales en el vistos por el usuario
	 */
	public void actualizarArchivosLocales() {
		controlador.actualizarArchivosLocalesl();
	}
	
	
	/**
	 * Devuelve el nombre de usuario
	 * 
	 * @return El nombre de usuario
	 */
	public String getUsuario() {
		if (sesion!=null)
			return sesion.getUsuario();
		else 
			return "";
	}
	
 
	/**
	 * Devuelve el host del servidor al que se esta conectado
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
		if (sesion!=null)
			sesion.logout();
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
	 * Muestra un mensaje informativo
	 * 
	 * @param string El mensaje
	 */
	public void msgInfo(String msg) {
		controlador.mensajeInfo(msg);
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
		return sesion!=null && sesion.isConectado();
	}

	
	/**
	 * Ordena a controlador que actualize el estado de login de cara al usuario
	 */
	public void actualizaLogin() {
		controlador.actualizaLogin();
	}

	/**
	 * Devuelve si hay sesion iniciada
	 * 
	 * @return True si hay sesion iniciada con algun usuario, false si no
	 */
	public boolean isLogged() {
		return sesion!=null && sesion.isLogged();
	}
}
