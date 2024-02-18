/**
 * 
 */
package ftpservidor.modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Paths;

import ftpservidor.Config;
import ftpservidor.modelo.comandos.ComCd;
import ftpservidor.modelo.comandos.ComDel;
import ftpservidor.modelo.comandos.ComGet;
import ftpservidor.modelo.comandos.ComLs;
import ftpservidor.modelo.comandos.ComMkdir;
import ftpservidor.modelo.comandos.ComPut;
import ftpservidor.modelo.comandos.ComRmdir;
import ftpservidor.modelo.comandos.Comando;
import ftpservidor.modelo.lib.Msg;
import ftpservidor.modelo.lib.UtilesArchivo;

/**
 * Control de las transferencias de una sesion concreta.
 * 
 * En el inicio de la carrera gestiona la conexion inicial en puerto efimero exclusivo para esta sesion.
 * Una vez la conexion esta en marcha cierra la conexion antigua de puerto fijo y compienza la gestion
 * de registro o login.
 * 
 * 
 * 
 * Una vez se ha registrado o logueado se comienza un bucle que dura mientras se esta conectado.
 * Este bucle espera recepciones de operaciones en el metodo {@link Sesion#buclePeticiones()}
 * 
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Sesion extends Thread {

	/**
	 * Socket abierto con la conexion inicial en el puerto publico fijo
	 */
	private Socket socketInicial;
	
	/**
	 * Serversocket para iniciar la conexion en puerto efimero
	 */
	private ServerSocket serverSocketEfimero;
	
	/**
	 * Socket principal de operaciones para la sesion
	 */
	private Socket socket;
	
	/**
	 * InputStream del socket
	 */
	private InputStream is;
	
	/**
	 * OutputStream del socket
	 */
	private OutputStream os;
	
	/**
	 * DataInputStream del socket
	 */
	private DataInputStream dis;
	
	/**
	 * DataoutputStream del socket
	 */
	private DataOutputStream dos;
	
	/**
	 * Tipo de sesion LOGIN_NORMAL, LOGIN_ANONIMO
	 */
	private int tipoSesion = 0;//LOGIN_NORMAL LOGIN_ANONIMO
	
	/**
	 * Usuario al que pertenece la sesion
	 */
	private Usuario usuario;
	
	/**
	 * Directorio actual de trabajo. Ruta depediente de la carpeta de usuario.
	 */
	private String cwd = "/";

 
	/**
	 * Constructor
	 * 
	 * 
	 * @param serverSocket ServerSocket de puerto efimero para iniciar la comunicacion de operaciones.
	 * 
	 * @param socketInicial Socket de conexion inicial en el puerto fijo de escucha del servidor
	 */
	public Sesion(ServerSocket serverSocket, Socket socketInicial) {
		this.serverSocketEfimero = serverSocket;
		this.socketInicial = socketInicial;
	}

	
	
	@Override
	public void run() {
		try {
			// coger nuevo socket
			serverSocketEfimero.setSoTimeout(10000);
			socket = serverSocketEfimero.accept();
			socket.setSoTimeout(180000);
			
			// cerrar socket inicial de puerto publico fijo
			this.socketInicial.close();
			
			// cerrar server socket efimero
			serverSocketEfimero.close();
			
			
			//recoger stream y crear envolventes
			is = socket.getInputStream();
			os = socket.getOutputStream();
			dis = new DataInputStream(is);
			dos = new DataOutputStream(os);
			Msg.msgHora("Conexion iniciada con "+socket.getRemoteSocketAddress());
			
			
			// gestionar login y registro
			String operacion = dis.readUTF();
			boolean permitido = false;
			if (operacion.toUpperCase().equals(Comando.LOGIN)) 
				permitido = gestionaLogin();
			else if (operacion.toUpperCase().equals(Comando.REGISTRO))
				permitido = gestionarRegistro();
			
			
			// Si ha ganado acceso iniciar el bucle de operaciones
			if (permitido) {
				buclePeticiones();
			}
			//al terminar el bucle de operacines cerrar el socket
			socket.close();
			
		} catch (SocketTimeoutException ex) {
			Msg.msgHora("Cerrando serversocket en puerto "+serverSocketEfimero.getLocalPort()+" por falta de respuesta");
		} catch (IOException e) {
			
		}
		if (socket!=null) {
			Msg.msgHora(((usuario!=null) ? usuario+" ":"" ) + socket.getRemoteSocketAddress()+" conexion terminada");
		}
	}

	/**
	 * Bucle de operaciones. Se encarga de escuchar la llegada de comandos e iniciar el protocolo necesario 
	 * para gestionarlo
	 */
	private void buclePeticiones() {
		while (estaConectado()) {
			try {
				String codigoPeticion = dis.readUTF();
				switch (codigoPeticion) {
				case Comando.EXIT -> exit();
				case Comando.LS -> new ComLs(this).iniciar();
				case Comando.CD -> new ComCd(this).iniciar();
				case Comando.DEL -> new ComDel(this).iniciar();
				case Comando.MKDIR-> new ComMkdir(this).iniciar();
				case Comando.RMDIR-> new ComRmdir(this).iniciar();
				case Comando.GET-> new ComGet(this).iniciar();
				case Comando.PUT-> new ComPut(this).iniciar();
				}

			} catch (IOException e) {
				try {socket.close();} catch (IOException e1) {}
			}
		}
	}

	/**
	 * Gestiona el login.
	 * Ver protocolo LOGIN en la documentacion
	 * 
	 * @return True si hace login exitoso, false si no lo hace.
	 */
	private boolean gestionaLogin() {
		boolean loginOk = false;
		try {
			//leer tipo de login
			tipoSesion = dis.readInt();
			//si login normal
			if (tipoSesion == Codigos.LOGIN_NORMAL) {
				String nombreUsuario = dis.readUTF();
				String contrasena = dis.readUTF();
				
				//comprobar validez de usuario para filtrar ataques de path transversal
				String rutaUsuario = new File(Config.getRUTA_ALMACENAMIENTO()+"/"+nombreUsuario).getAbsolutePath();
				if (!UtilesArchivo.rutaDentroDeRuta(rutaUsuario, Config.getRUTA_ALMACENAMIENTO())) {
					Msg.msgHora("Posible ataque de path transversal a "+rutaUsuario+" desde "+socket.getRemoteSocketAddress());
					dos.writeInt(Codigos.MAL);
					exit();
					return false;
				}
				
				
				usuario = new Usuario(nombreUsuario);
				loginOk = usuario.login(contrasena);
				
			//si login anonimo
			} else if (tipoSesion==Codigos.LOGIN_ANONIMO) {
				usuario = new Usuario();
				loginOk = true;
			} 
			
			//si ha conseguido iniciar sesion avisar
			if (loginOk) {
				dos.writeInt(Codigos.OK);
				Msg.msgHora(usuario.getNombreUsuario()+" Login desde "+socket.getRemoteSocketAddress());
				return true;
				
			//en caso contrario avisar del rechazo
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
	 * Gestiona el registro de un nuevo usuario logeandolo si el registro tiene exito
	 * 
	 * @return True si se ha registrado, False si no lo ha hechoa
	 */
	private boolean gestionarRegistro() {
		try {
			//leer nombre y contraseña
			String nombreUsuario = dis.readUTF();
			String contrasena = dis.readUTF();
			
			//evitar nombres de usuarios con puntos
			if (nombreUsuario.indexOf('.')!=-1) {
				dos.write(Codigos.MAL);
				exit();
				return false;
			}
			//comprobar validez de usuario para filtrar ataques de path transversal
			String rutaUsuario = new File(Config.getRUTA_ALMACENAMIENTO()+"/"+nombreUsuario).getAbsolutePath();

			if (!UtilesArchivo.rutaDentroDeRuta(rutaUsuario, Config.getRUTA_ALMACENAMIENTO())) {
				Msg.msgHora("Posible ataque de path transversal a "+rutaUsuario+" desde "+socket.getRemoteSocketAddress());
				dos.writeInt(Codigos.MAL);
				exit();
				return false;
			}
			
			
			//comprobar si no existe, se puede crear la carpeta y su archivo con la clave
			boolean registroCorrecto=false;
			File carpetaUsuario = new File(Config.getRUTA_ALMACENAMIENTO()+"/"+nombreUsuario);
			if (!carpetaUsuario.exists() &&
				carpetaUsuario.mkdir()&&
				crearPasswordFile(nombreUsuario,contrasena)) {
					registroCorrecto=true;
			}
			
			//si el registro es correcto se avisa
			if (registroCorrecto) {
				Msg.msgHora("Registro de nuevo usuario "+nombreUsuario+" dede"+socket.getRemoteSocketAddress());
				usuario = new Usuario(nombreUsuario);
				dos.writeInt(Codigos.OK);
				return true;
				
			//si no es correcto se avisa del rechazo
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
	 * Crea un archivo para un usuario que contiene su clave
	 * 
	 * @param nombreUsuario Nombre de usuario
	 * @param contrasena Contrasena
	 * 
	 * @return True si se ha creado, false si no se ha creado
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

	
	 
	/**
	 * Devuelve el DataInputStream de la sesion
	 * 
	 * @return El data input stream
	 */
	public DataInputStream getDis() {
		return dis;
	}

	/**
	 * Devuelve el DataOutputStream de la sesion
	 * 
	 * @return El data output stream
	 */
	public DataOutputStream getDos() {
		return dos;
	}

	/**
	 * Devuelve el usuario de la sesion
	 * 
	 * @return El usuario
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * Devuelve el Directorio de trabajo actual de la sesion
	 * 
	 * @return El CWD
	 */
	public String getCwd() {
		return cwd;
	}

	
	/**
	 * Define el CWD
	 * 
	 * @param cwd El nuevo directorio actual de trabajo a establecer
	 * 
	 * @return True si s e ha cambiado, false si no se ha cambiado
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
	
	
	/**
	 * Cierra la sesion
	 */
	public void exit() {
		try {
			if (socket!=null) 
				Msg.msgHora(((usuario!=null) ? usuario+" " :"" ) + socket.getRemoteSocketAddress()+" cierra la sesion");
				
			dos.writeInt(Codigos.OK);
			socket.close();
		} catch (IOException e) {
			
		}
	}

 
	/**
	 * Devuelve un strean con los datos de usuario: Nombre + direccion del socket
	 * 
	 * @return El nombre de usuario y direccion y puerto de conexion
	 */
	public String getDatosUsuario() {
		return usuario.getNombreUsuario()+" "+socket.getRemoteSocketAddress();
	}
	
}
