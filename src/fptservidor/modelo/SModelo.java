/**
 * 
 */
package fptservidor.modelo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class SModelo extends Thread{
	private ServerSocket serverSocket;
	private Thread hiloEscucha;
	private ArrayList<Sesion> sesiones = new ArrayList<Sesion>();
	private int puerto=0;
	public SModelo(int puerto) {
		this.puerto=puerto;
	}

	public boolean iniciar()  {
		try {
			serverSocket=new ServerSocket(puerto);
			return true;
		} catch (IOException e) {
		return false;
		}
	}

	
	/**
	 * Bucle de escucha. Se genera en otro hilo
	 */
	public void run() {
			iniciar();
			while(escuchando()) {
				try {
					Socket s = serverSocket.accept();
					Sesion sesion=new Sesion(s);
					sesiones.add(sesion);
					sesion.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}

	/**
	 * @return
	 */
	private boolean escuchando() {
		boolean res= serverSocket!=null && !serverSocket.isClosed();
		return res;
	}
	
}
