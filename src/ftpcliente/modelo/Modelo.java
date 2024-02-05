/**
 * 
 */
package ftpcliente.modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;

import ftpcliente.Config;


/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Modelo {

	private Socket socket;

    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
 
    private LinkedBlockingDeque<String> operaciones = new LinkedBlockingDeque<String>();
    ProcesadorOperaciones procOperaciones;
    
	public boolean conectar (int tipo,String usuario, String contrasena, String host, int puerto) {
		if (estaConectado())
			return false;
		try {
			socket = new Socket(host,puerto);
			is = socket.getInputStream();
			os = socket.getOutputStream();
			dis = new DataInputStream(is);
			dos = new DataOutputStream(os);
//			isr = new InputStreamReader(is,Config.COD_TEXTO);
//			osw = new OutputStreamWriter(os,Config.COD_TEXTO);
			
			
			dos.writeInt(tipo);
			if (tipo==Codigos.LOGIN_NORMAL) {
				dos.writeUTF(usuario);
				dos.writeUTF(contrasena);
				}
			int res = dis.readInt();
			if (res==Codigos.LOGIN_OK)
				return true;
			else
				return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}




	/**
	 * 
	 */
	public void procesarOperaciones() {
		procOperaciones = new ProcesadorOperaciones()
		
	}




	/**
	 * Devuelve si se esta conectado
	 * 
	 * @return True si se esta conectado, false en otro caso
	 */
	private boolean estaConectado() {
		if (socket!=null&&(socket.isConnected()&&!socket.isClosed()))
			return true;
		return false;
	}
	
	
}
