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

import fptservidor.Msg;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class SModelo extends Thread{
	private ServerSocket serverSocket;
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
			Msg.cuadro(new String[] {"SERVIDOR INICIADO","PUERTO: "+puerto});
			while(escuchando()) {
				try {
					Socket socketInicial = serverSocket.accept();
					DataOutputStream dos = new DataOutputStream(socketInicial.getOutputStream());
					ServerSocket servSocketOperar = new ServerSocket(0);
					Sesion sesion=new Sesion(servSocketOperar,socketInicial);
					sesiones.add(sesion);
					sesion.start();
					Msg.msgHora("Esperando conexion con "+socketInicial.getInetAddress()+" en el puerto local "+servSocketOperar.getLocalPort());
					dos.writeInt(Codigos.OK);
					dos.writeInt(servSocketOperar.getLocalPort());
					limpiarSesiones();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Msg.msgHora("Servidor terminado");

	}

	/**
	 * 
	 */
	private void limpiarSesiones() {
		List<Sesion> borrar=new ArrayList<Sesion>();
		for (Sesion sesion : sesiones) {
			if (!sesion.isAlive())
				borrar.add(sesion);
		}
		sesiones.removeAll(borrar);
	}

	/**
	 * @return
	 */
	private boolean escuchando() {
		boolean res= serverSocket!=null && !serverSocket.isClosed();
		return res;
	}
	
}
