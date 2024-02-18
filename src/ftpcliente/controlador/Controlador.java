/**
 * 
 */
package ftpcliente.controlador;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import ftpcliente.conector.Codigos;
import ftpcliente.conector.Conector;
import ftpcliente.conector.comandos.Comando;
import ftpcliente.controlador.dto.DtoArchivo;
import ftpcliente.vista.gui.Ventana;

/**
 * Controlador. sirve de puetne entre la vista y el conector. Tiene metodos que
 * transforman llamadas de la interfaz en comandos en formato string que puede
 * entender el conector. Ademas sirve de puente para mensajes, confirmaciones y
 * comandos en texto entre vista y conector
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Controlador {

	/**
	 * Referencia al conector
	 */
	private Conector conector;

	/**
	 * Referencia a la vista
	 */
	private Ventana vista;

	/**
	 * Constructor. Crea el conector y la vista
	 */
	public Controlador() {
		conector = new Conector(this);
		vista = new Ventana(this);
		vista.setVisible(true);
	}

	/**
	 * Ordena a vista que muestre los datos de ruta remota y lista de archivos
	 * 
	 * @param rutaActual La ruta remota
	 * @param archivos   La lista de archivos
	 */
	public void actualizaListaArchivosRemotos(String rutaActual, ArrayList<DtoArchivo> archivos) {
		SwingUtilities.invokeLater(() -> vista.actualizaListaRemota(rutaActual, archivos));
	}

	/**
	 * Transforma los valores en un comando LOGIN y lo envia a conector
	 * 
	 * @param host       El host del servidor
	 * @param puerto     El puerto del servidor
	 * @param usuario    Usuario para iniciar sesion
	 * @param contrasena Contrasena para iniciar sesion
	 */
	public void login(String host, int puerto, String usuario, String contrasena) {
		conector.addOperacion(Comando.LOGIN + " " + host + " " + puerto + " " + usuario + " " + contrasena);
	}

	/**
	 * Transforma los valores en un comando REGISTRO y lo envia a conector
	 * 
	 * @param host       El host del servidor
	 * @param puerto     El puerto del servidor
	 * @param usuario    Usuario para registrar
	 * @param contrasena Contrasena para registrar
	 */
	public void registrar(String host, int puerto, String usuario, String contrasena) {
		conector.addOperacion(Comando.REGISTRO + " " + host + " " + puerto + " " + usuario + " " + contrasena);
	}

	/**
	 * Recopila los datos de login actual y los envia a vista para que los muestre
	 */
	public void actualizaLogin() {
		if (conector.isLogged()) {
			String usuario = conector.getUsuario();
			String host = conector.getHost();
			vista.actualizaLoginEstado(true, host, usuario);
		} else {
			vista.actualizaLoginEstado(false, "", "");
		}

	}

	/**
	 * Envia a vista un mensaje de error para que lo muestre
	 * 
	 * @param msg Esl mensaje
	 */
	public void mensajeError(String msg) {
		SwingUtilities.invokeLater(() -> {
			vista.msgError(msg);
			vista.addHistorial("ERROR: " + msg);
		});

	}

	/**
	 * Envia a vista un mensaje informativo para que lo muestre
	 * 
	 * @param msg El mensaje
	 */
	public void mensajeInfo(String msg) {
		SwingUtilities.invokeLater(() -> vista.addHistorial("INFO: " + msg));
	}

	/**
	 * Pide a vista que meustre un dialogo de confirmacion
	 * 
	 * @param msg El mensaje a mostrar en el dialogo
	 * 
	 * @return True si el usuario a aceptado, false si no lo ha hecho
	 */
	public boolean confirmar(String msg) {
		return vista.confirmar(msg);
	}

	/**
	 * Devuelve el listado de archvios de una carpeta local, agregando ".." si no se
	 * trata de la raiz de un disco
	 * 
	 * @param ruta La ruta a inspeccionar
	 * 
	 * @return La lista de archivos contenidos en la ruta
	 */
	public List<DtoArchivo> getArchivosLocales(File ruta) {

		List<DtoArchivo> archivos = new ArrayList<DtoArchivo>();

		// agregar .. si no es root
		if (ruta.toPath().getNameCount() != 0) {
			archivos.add(new DtoArchivo("..", Codigos.DIRECTORIO));
		}

		// recoger archivos
		File[] lista = ruta.listFiles();
		if (lista != null) {
			for (File arch : lista) {
				archivos.add(
						new DtoArchivo(arch.getName(), (arch.isDirectory()) ? Codigos.DIRECTORIO : Codigos.ARCHIVO));
			}
		}
		return archivos;
	}

	
	/**
	 * Devuelve las unidades de disco del sitema
	 * 
	 * @return La lista con las unidades
	 */
	public String[] getUnidadesDisco() {
		File[] unidades;
		unidades = File.listRoots();
		String[] nombres = new String[unidades.length];
		for (int i = 0; i < unidades.length; i++) {
			nombres[i] = unidades[i].getAbsolutePath();
		}
		return nombres;
	}
	
	
	/**
	 * Ordena a la vista que actualice el listado de archivos locales
	 */
	public void actualizarArchivosLocalesl() {
		vista.actualizarArchivosLocales();

	}

	/**
	 * Envia al conector la orden de cerrar sesion
	 */
	public void logout() {
		conector.logout();

	}

	/**
	 * Envia a conector un comando como string
	 * 
	 * @param comando El comando a enviar
	 */
	public void enviarComando(String comando) {
		if (comando != null)
			conector.addOperacion(comando);
	}

	/**
	 * Envia a conector el comando LS
	 */
	public void comLs() {
		if (conector.isLogged())
			conector.addOperacion(Comando.LS);
	}

	/**
	 * Envia a conector el comando CD
	 * 
	 * @param valor El directorio al que cambiar
	 */
	public void comCd(String valor) {
		if (valor != null && conector.isLogged()) {
			conector.addOperacion(Comando.CD + " \"" + valor + "\"");
			comLs();
		}
	}

	/**
	 * Envia a conector el comando GET
	 * 
	 * @param rutaRemota Ruta remota que obtener
	 * @param rutaLocal  Ruta local en la que guardar el archivo descargado
	 */
	public void comGet(String rutaRemota, String rutaLocal) {
		if (rutaRemota != null && rutaLocal != null) {
			conector.addOperacion(Comando.GET + " \"" + rutaRemota + "\" \"" + rutaLocal + "\"");
		}
	}

	/**
	 * Envia a conector el comando DEL
	 * 
	 * @param valor la ruta remota del archivo a elminiar
	 */
	public void comDel(String valor) {
		if (valor != null) {
			conector.addOperacion(Comando.DEL + " \"" + valor + "\"");
			comLs();
		}

	}

	/**
	 * Envia a conector el comando MKDIR
	 * 
	 * @param valor La ruta remota del directorio a crear
	 */
	public void comMkdir(String valor) {
		if (valor != null && conector.isLogged()) {
			conector.addOperacion(Comando.MKDIR + " \"" + valor + "\"");
			comLs();
		}
	}

	/**
	 * Envia a conector el comando RMDIR
	 * 
	 * @param valor La ruta del directorio remoto a eliminar
	 */
	public void comRmdir(String valor) {
		if (valor != null) {
			conector.addOperacion(Comando.RMDIR + " \"" + valor + "\"");
			comLs();
		}

	}

	/**
	 * Envia a conector el comando PUT
	 * 
	 * @param rutaLocal  Ruta local a enviar
	 * @param rutaRemota Ruta remota en la que guardar lo enviado
	 */
	public void comPut(String rutaLocal, String rutaRemota) {
		if (rutaRemota != null && rutaLocal != null) {
			conector.addOperacion(Comando.PUT + " \"" + rutaLocal + "\" \"" + rutaRemota + "\"");
			comLs();
		}

	}

	/**
	 * Envia a conector el comando MKDIR
	 * 
	 * @param ruta La ruta remota a crear
	 * @return True si se ha creado, false en cao contrario
	 */
	public boolean comMkdirLocal(String ruta) {
		try {
			return new File(ruta).mkdirs();
		} catch (SecurityException ex) {
			return false;
		}
	}

}
