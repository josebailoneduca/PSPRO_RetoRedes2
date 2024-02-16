/**
 * 
 */
package fptservidor.modelo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import fptservidor.Config;
import fptservidor.Msg;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class SModelo extends Thread {
	private ServerSocket serverSocket;
	private ArrayList<Sesion> sesiones = new ArrayList<Sesion>();
	private int puerto = 0;

	public SModelo(int puerto) {
		this.puerto = puerto;
	}

	/**
	 * Bucle de escucha. Se genera en otro hilo
	 */
	public void run() {
		if (iniciar()) {
			Msg.cuadro(new String[] { 
					"SERVIDOR FTP INICIADO", 
					"ESCUCHANDO EN PUERTO: " + puerto ,
					"ALM. USUARIOS: "+ (new File(Config.getRUTA_ALMACENAMIENTO()).getAbsolutePath()),
					"ALM. ANONIMO: "+ (new File(Config.getRUTA_ALMACENAMIENTO_ANONIMO()).getAbsolutePath()),
					});
			while (isEscuchando()) {
				try {
					Socket socketInicial = serverSocket.accept();
					DataOutputStream dos = new DataOutputStream(socketInicial.getOutputStream());
					ServerSocket servSocketOperar = new ServerSocket(0);
					Sesion sesion = new Sesion(servSocketOperar, socketInicial);
					sesiones.add(sesion);
					sesion.start();
					Msg.msgHora("Nueva conexion con " + socketInicial.getInetAddress() + " en puerto local " + puerto
							+ ". Esperando conexion de operaciones en el puerto local " + servSocketOperar.getLocalPort());
					dos.writeInt(Codigos.OK);
					dos.writeInt(servSocketOperar.getLocalPort());
					limpiarSesiones();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Msg.msgHora("Servidor terminado");

	}

	
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
	 * 
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
	 * @return
	 */
	private boolean isEscuchando() {
		boolean res = serverSocket != null && !serverSocket.isClosed();
		return res;
	}

}
