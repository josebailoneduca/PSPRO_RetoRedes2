/**
 * 
 */
package ftpservidor.modelo;

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

import ftpcliente.conector.ProcesadorOperaciones;
import ftpservidor.Config;
import ftpservidor.Msg;
import ftpservidor.modelo.comandos.ComCd;
import ftpservidor.modelo.comandos.ComDel;
import ftpservidor.modelo.comandos.ComGet;
import ftpservidor.modelo.comandos.ComLs;
import ftpservidor.modelo.comandos.ComMkdir;
import ftpservidor.modelo.comandos.ComPut;
import ftpservidor.modelo.comandos.ComRmdir;
import ftpservidor.modelo.comandos.TiposComando;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Sesion extends Thread {

	private Socket socketInicial;
	private ServerSocket serverSocket;
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	private int tipoSesion = 0;//LOGIN_NORMAL LOGIN_ANONIMO
	private Usuario usuario;
	private String cwd = "/";

 
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
			socket.setSoTimeout(60000);
			
			// cerrar socket inicial
			this.socketInicial.close();
			
			// cerrar server socket
			serverSocket.close();
			
			
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
			Msg.msgHora(((usuario!=null) ? usuario+" ":"" ) + socket.getRemoteSocketAddress()+" conexion terminada");
		}
	}

	/**
	 * 
	 */
	private void buclePeticiones() {
		while (estaConectado()) {
			try {
				String codigoPeticion = dis.readUTF();
				switch (codigoPeticion) {
				case TiposComando.EXIT -> exit();
				case TiposComando.LS -> new ComLs(this).iniciar();
				case TiposComando.CD -> new ComCd(this).iniciar();
				case TiposComando.DEL -> new ComDel(this).iniciar();
				case TiposComando.MKDIR-> new ComMkdir(this).iniciar();
				case TiposComando.RMDIR-> new ComRmdir(this).iniciar();
				case TiposComando.GET-> new ComGet(this).iniciar();
				case TiposComando.PUT-> new ComPut(this).iniciar();
				}

			} catch (IOException e) {
				//e.printStackTrace();
				try {
					socket.close();
				} catch (IOException e1) {
					//e.printStackTrace();
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
				Msg.msgHora(usuario.getNombreUsuario()+" Login desde "+socket.getRemoteSocketAddress());
				return true;
			} else {
				usuario=null;
				dos.writeInt(Codigos.MAL);
				Msg.msgHora("Login erróneo desde "+socket.getRemoteSocketAddress());
				return false;
			}

		} catch (IOException e) {
			Msg.msgHora("Login erróneo desde "+socket.getRemoteSocketAddress());
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
			boolean registroCorrecto=false;
			File carpetaUsuario = new File(Config.getRUTA_ALMACENAMIENTO()+"/"+nombreUsuario);
			if (!carpetaUsuario.exists() &&
				carpetaUsuario.mkdir()&&
				crearPasswordFile(nombreUsuario,contrasena)) {
					registroCorrecto=true;
			}
			if (registroCorrecto) {
				Msg.msgHora("Registro de nuevo usuario "+nombreUsuario+" dede"+socket.getRemoteSocketAddress());
				usuario = new Usuario(nombreUsuario);
				dos.writeInt(Codigos.OK);
				return true;
			}else {
				Msg.msgHora("Registro erróneo desde "+socket.getRemoteSocketAddress());
				dos.writeInt(Codigos.MAL);
				return false;
			}
			
		} catch (IOException e) {
			Msg.msgHora("Registro erróneo desde "+socket.getRemoteSocketAddress());
			try {
				dos.writeInt(Codigos.MAL);
			} catch (IOException e1) {}
			return false;
		}
	}

	/**
	 * @param nombreUsuario
	 */
	private boolean crearPasswordFile(String nombreUsuario, String contrasena) {
		File arch = new File(Config.getRUTA_ALMACENAMIENTO()+"/"+nombreUsuario+".pass");
		try {
			FileWriter fw = new FileWriter(arch);
			fw.write(contrasena);
			fw.flush();
			fw.close();
			return true;
		} catch (IOException e) {
			Msg.msgHora("Error creando archivo de password "+arch.getAbsolutePath());
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
			if (socket!=null) 
				Msg.msgHora(((usuario!=null) ? usuario+" " :"" ) + socket.getRemoteSocketAddress()+" cierra la sesion");
				
			dos.writeInt(Codigos.OK);
			socket.close();
		} catch (IOException e) {
			
		}
	}

 
	public String getDatosUsuario() {
		return usuario.getNombreUsuario()+" "+socket.getRemoteSocketAddress();
	}
	
}
