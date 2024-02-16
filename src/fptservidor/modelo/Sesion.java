/**
 * 
 */
package fptservidor.modelo;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Paths;

import fptservidor.Config;
import fptservidor.Msg;
import fptservidor.modelo.comandos.ComCd;
import fptservidor.modelo.comandos.ComDel;
import fptservidor.modelo.comandos.ComGet;
import fptservidor.modelo.comandos.ComLs;
import fptservidor.modelo.comandos.ComMkdir;
import fptservidor.modelo.comandos.ComPut;
import fptservidor.modelo.comandos.ComRmdir;
import fptservidor.modelo.comandos.TiposComando;
import ftpcliente.conector.ProcesadorOperaciones;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Sesion extends Thread {

	Socket socketInicial;
	ServerSocket serverSocket;
	Socket socket;
	InputStream is;
	OutputStream os;
	DataInputStream dis;
	DataOutputStream dos;
	private int tipoSesion = 0;
	private Usuario usuario;
	private String cwd = "/";

	boolean conectado = false;

	/**
	 * @param s
	 */
	public Sesion(ServerSocket serverSocket, Socket socketInicial) {
		this.serverSocket = serverSocket;
		this.socketInicial = socketInicial;

	}

	@Override
	public void run() {
		try {
			// coger nuevo socket
			serverSocket.setSoTimeout(10000);;
			socket = serverSocket.accept();
			// cerrar socket inicial
			this.socketInicial.close();
			// cerrar server socket
			serverSocket.close();
			
			socket.setSoTimeout(60000);
			
			conectado = true;
			is = socket.getInputStream();
			os = socket.getOutputStream();
			dis = new DataInputStream(is);
			dos = new DataOutputStream(os);
			Msg.msgHora("Conexion iniciada con "+socket.getRemoteSocketAddress());
			// gestionar login y registro
			String operacion = dis.readUTF();
			boolean permitido = false;
			if (operacion.toUpperCase().equals(TiposComando.LOGIN)) 
				permitido = gestionaLogin();
			else if (operacion.toUpperCase().equals(TiposComando.REGISTRO))
				permitido = gestionarRegistro();
			// bucle de operaciones
			if (permitido) {
				buclePeticiones();
			}
			socket.close();
		} catch (SocketTimeoutException ex) {
			Msg.msgHora("Cerrando serversocket en puerto "+serverSocket.getLocalPort()+" por falta de respuesta");
		} catch (IOException e) {
			
		}
		if (socket!=null) {
			Msg.msgHora("Conexion terminada con "+ ((usuario!=null) ? usuario+ " en ":"" ) + socket.getRemoteSocketAddress());
		}
	}

	/**
	 * 
	 */
	private void buclePeticiones() {
		while (estaConectado()) {
			System.out.println("bucleeee");
			try {
				String codigoPeticion = dis.readUTF();
				switch (codigoPeticion) {
				case TiposComando.EXIT -> {
					dos.writeInt(Codigos.OK);
					socket.close();
				}
				case TiposComando.LS -> new ComLs(this).iniciar();
				case TiposComando.CD -> new ComCd(this).iniciar();
				case TiposComando.DEL -> new ComDel(this).iniciar();
				case TiposComando.MKDIR-> new ComMkdir(this).iniciar();
				case TiposComando.RMDIR-> new ComRmdir(this).iniciar();
				case TiposComando.GET-> new ComGet(this).iniciar();
				case TiposComando.PUT-> new ComPut(this).iniciar();
				}

			} catch (IOException e) {
				e.printStackTrace();
				try {
					socket.close();
				} catch (IOException e1) {
					e.printStackTrace();
				}

			}
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
			} else if (tipoSesion==Codigos.LOGIN_ANONIMO) {
				usuario = new Usuario();
				loginOk = true;
			} 
			if (loginOk) {
				dos.writeInt(Codigos.OK);
				Msg.msgHora("Login de "+usuario.getNombreUsuario()+" desde "+socket.getRemoteSocketAddress());
				return true;
			} else {
				usuario=null;
				dos.writeInt(Codigos.MAL);
				Msg.msgHora("Login erróneo desde "+socket.getRemoteSocketAddress());
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @return
	 */
	private boolean gestionarRegistro() {
		try {
			String nombreUsuario = dis.readUTF();
			String contrasena = dis.readUTF();
			
			File carpetaUsuario = new File(Config.getRUTA_ALMACENAMIENTO()+"/"+nombreUsuario);
			if (carpetaUsuario.exists()) {
				Msg.msgHora("Registro erróneo desde "+socket.getRemoteSocketAddress());
				dos.writeInt(Codigos.MAL);
				return false;
			}
			else {
				carpetaUsuario.mkdir();
				crearPasswordFile(nombreUsuario,contrasena);
				
			}
			Msg.msgHora("Registro de nuevo usuario "+nombreUsuario+" dede"+socket.getRemoteSocketAddress());
			usuario = new Usuario(nombreUsuario);
			dos.writeInt(Codigos.OK);
			return true;
			
		} catch (IOException e) {
			Msg.msgHora("Registro erróneo desde "+socket.getRemoteSocketAddress());
			return false;
		}
	}

	/**
	 * @param nombreUsuario
	 */
	private void crearPasswordFile(String nombreUsuario, String contrasena) {
		File arch = new File(Config.getRUTA_ALMACENAMIENTO()+"/"+nombreUsuario+".pass");
		try {
			FileWriter fw = new FileWriter(arch);
			fw.write(contrasena);
			fw.flush();
			fw.close();
			
			
		} catch (IOException e) {
		
			e.printStackTrace();
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

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public InputStream getIs() {
		return is;
	}

	public OutputStream getOs() {
		return os;
	}

	public DataInputStream getDis() {
		return dis;
	}

	public DataOutputStream getDos() {
		return dos;
	}

	public int getTipoSesion() {
		return tipoSesion;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public String getCwd() {
		return cwd;
	}

	/**
	 * @param replace
	 */
	public boolean setCwd(String cwd) {
		try {
		cwd = cwd.replace("\\", "/");
		cwd = cwd.replace("//", "/");

		this.cwd=Paths.get(cwd).normalize().toString().replace("\\", "/");
		return true;
		}catch(Exception ex) {
			return false;
		}
	}
	public void exit() {
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

 
	
}
