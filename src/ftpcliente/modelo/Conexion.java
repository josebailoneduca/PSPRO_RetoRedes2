/**
 * 
 */
package ftpcliente.modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Conexion {
	Socket socket;
	 ProcesadorOperaciones procOperaciones;
	 private LinkedBlockingQueue<String> operacionesPendientes;
	    private InputStream is;
	    private OutputStream os;
	    private DataInputStream dis;
	    private DataOutputStream dos;
	    Modelo modelo;
	    
	public Conexion(Socket socket, Modelo modelo) {
		operacionesPendientes= new LinkedBlockingQueue<String>();
		this.modelo=modelo;
		this.socket = socket;

		try {
			is=socket.getInputStream();
		os=socket.getOutputStream();
		dis=new DataInputStream(is);
		dos=new DataOutputStream(os);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException();
		}
		
		System.out.println(socket.getLocalPort()+" "+socket.getLocalAddress()+" "+ socket.getPort()+""+socket.getRemoteSocketAddress());
		procOperaciones=new ProcesadorOperaciones(operacionesPendientes, dis, dos, modelo);
		procOperaciones.start();
	}

	/**
	 * @param operacion
	 */
	public void addOperacion(String operacion) {
		operacionesPendientes.add(operacion);
		
	}
	
}
