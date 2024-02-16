/**
 * 
 */
package ftpservidor.modelo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ftpservidor.Config;
import ftpservidor.Msg;

import java.io.File;

/**
 * Punto principal del modelo. Tiene un bucle escuchando al puerto definido en la configuarcion.
 * 
 * Por cada conexion genera un nuevo serversocket en puerto efimero en el que seguira la conexion
 * con el cliente. Esa nueva conexion sera gestionada por un hilo aparte de tipo Sesion.
 * 
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class SModelo extends Thread {
	
	/**
	 * Seversocket de escucha
	 */
	private ServerSocket serverSocket;
	
	/**
	 * Listado de sesiones en funcionamiento
	 */
	private ArrayList<Sesion> sesiones = new ArrayList<Sesion>();
	
	/**
	 * Puerto de escucha
	 */
	private int puerto = 0;

	
	/**
	 * Constructor 
	 * 
	 * @param puerto Puerto en el que escuchar
	 */
	public SModelo(int puerto) {
		this.puerto = puerto;
	}


	
	/**
	 * La carrera consiste en un bucle activo mientras el serversocket no este cerrado.
	 * 
	 * Cuando se recibe una nueva conexion se crea un nuevo serversocket en un puerto efimero y se 
	 * delega la gestion en ese nuevo serversocket a un hilo aparte de tipo Sesion.
	 * 
	 * 
	 */
	@Override
	public void run() {
		//si se ha iniciado el serversocket
		if (iniciar()) {
			Msg.cuadro(new String[] { 
					"SERVIDOR FTP INICIADO", 
					"ESCUCHANDO EN PUERTO: " + puerto ,
					"ALM. USUARIOS: "+ (new File(Config.getRUTA_ALMACENAMIENTO()).getAbsolutePath()),
					"ALM. ANONIMO: "+ (new File(Config.getRUTA_ALMACENAMIENTO_ANONIMO()).getAbsolutePath()),
					});
			
			//mientras se este escuchando
			while (isEscuchando()) {
				try {
					//aceptar neuva conexion
					Socket socketInicial = serverSocket.accept();
					DataOutputStream dos = new DataOutputStream(socketInicial.getOutputStream());
					
					//crerar nuevo serversocket en puerto efimero para comunicarse con el host de la nueva conexion
					ServerSocket servSocketOperar = new ServerSocket(0);
					
					//crear nueva sesion que se encargara de gestionar la conexion por el puerto efimero
					Sesion sesion = new Sesion(servSocketOperar, socketInicial);
					sesiones.add(sesion);
					sesion.start();
					
					
					Msg.msgHora("Nueva conexion con " + socketInicial.getInetAddress() + " en puerto local " + puerto
							+ ". Esperando conexion de operaciones en el puerto local " + servSocketOperar.getLocalPort());
					
					//avisar del nuevo puerto de comunicacion al host remoto que ha conectado
					dos.writeInt(Codigos.OK);
					dos.writeInt(servSocketOperar.getLocalPort());
					
					//realizar limpieza de sesiones
					limpiarSesiones();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Msg.msgHora("Servidor terminado");

	}

	
	/**
	 * Inicia el serversocket que escuchara en el puerto definido por la configuracion
	 * 
	 * @return True si se ha creado, false si no se ha creado
	 */
	public boolean iniciar() {
		try {
			serverSocket = new ServerSocket(puerto);
			return true;
		} catch (IOException e) {
			Msg.msgHora(e.getMessage() + ". En puerto " + puerto);
			return false;
		}
	}
	
	
	/**
	 * Comprueba la lista de sesiones y elimina las que ya no estan vivas
	 */
	private void limpiarSesiones() {
		List<Sesion> borrar = new ArrayList<Sesion>();
		for (Sesion sesion : sesiones) {
			if (!sesion.isAlive())
				borrar.add(sesion);
		}
		sesiones.removeAll(borrar);
	}

	/**
	 * Devuelve si el servidor esta escuchando
	 * @return true si el serversocket no ha sido cerrado
	 */
	private boolean isEscuchando() {
		boolean res = serverSocket != null && !serverSocket.isClosed();
		return res;
	}

}
