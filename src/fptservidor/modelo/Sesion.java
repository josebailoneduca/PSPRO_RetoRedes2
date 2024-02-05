/**
 * 
 */
package fptservidor.modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

import fptservidor.Config;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Sesion extends Thread {

	Socket socket;
	InputStream is;
	OutputStream os;
	DataInputStream dis;
	DataOutputStream dos;
	private InputStreamReader isr;
	private OutputStreamWriter osw;

	private int tipoSesion = 0;
	private Usuario usuario;

	/**
	 * @param s
	 */
	public Sesion(Socket s) {
		this.socket = s;
	}

	@Override
	public void run() {
		try {
			is = socket.getInputStream();
			os = socket.getOutputStream();
			dis = new DataInputStream(is);
			dos = new DataOutputStream(os);
			isr = new InputStreamReader(is, Config.COD_TEXTO);
			osw = new OutputStreamWriter(os, Config.COD_TEXTO);
			boolean permitido = gestionaLogin();
			if (!permitido) {
				dos.writeInt(Codigos.LOGIN_MAL);
			} else {
				dos.writeInt(Codigos.LOGIN_OK);
				buclePeticiones();
			}

			socket.close();
		} catch (IOException e) {
		}
	}

	/**
	 * 
	 */
	private void buclePeticiones() {
		System.out.println("bucle peticiones");
		System.out.println(usuario.getNombreUsuario());
		while (estaConectado()) {
		}

	}

	/**
	 * @return
	 */
	private boolean gestionaLogin() {
		boolean loginOk = false;
		try {
			tipoSesion = dis.readInt();
			if (tipoSesion == Codigos.LOGIN_NORMAL) {
				String nombreUsuario = dis.readUTF();
				String contrasena = dis.readUTF();
				usuario = new Usuario(nombreUsuario);
				loginOk = usuario.login(contrasena);
			} else {
				usuario = new Usuario();
				loginOk = true;
			}
			if (loginOk) {
				dos.writeInt(Codigos.LOGIN_OK);
				return true;
			} else {
				dos.writeInt(Codigos.LOGIN_MAL);
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Devuelve si se esta conectado
	 * 
	 * @return True si se esta conectado, false en otro caso
	 */
	private boolean estaConectado() {
		if (socket == null || (socket.isConnected() && !socket.isClosed()))
			return true;
		return false;
	}
}
