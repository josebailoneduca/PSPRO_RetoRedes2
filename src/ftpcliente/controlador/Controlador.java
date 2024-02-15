/**
 * 
 */
package ftpcliente.controlador;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;

import ftpcliente.conector.Modelo;
import ftpcliente.conector.comandos.TiposComando;
import ftpcliente.controlador.dto.DtoArchivo;
import ftpcliente.vista.gui.Ventana;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class Controlador {
	private Modelo modelo;
	private Ventana vista;
	public Controlador() {
		modelo=new Modelo(this);
		vista = new Ventana(this);
		vista.setVisible(true);

		
		
		//conectar("","");
		
	}

	/**
	 * @param rutaActual
	 * @param archivos
	 */
	public void actualizaLista(String rutaActual, ArrayList<DtoArchivo> archivos) {
		SwingUtilities.invokeLater(() -> 
			vista.actualizaListaRemota(rutaActual,archivos));
	}
	
	
	public boolean login(String host, int puerto, String usuario, String contrasena) {
		if (modelo.iniciarConexion(host, puerto)) {
			int tipo = Codigos.LOGIN_NORMAL;
			if (usuario.length()==0){
				tipo=Codigos.LOGIN_ANONIMO;
			}
			modelo.addOperacion(TiposComando.LOGIN+" "+tipo+" "+usuario+" "+contrasena);
			modelo.addOperacion("LS");
			return true;
		}else {
			return false;
		}
	}

	public boolean registrar(String host, int puerto, String usuario, String contrasena) {
		if (modelo.iniciarConexion(host, puerto)) {
			modelo.addOperacion(TiposComando.REGISTRO+" "+usuario+" "+contrasena);
			modelo.addOperacion("LS");
			return true;
		}else {
			return false;
		}

	}

	/**
	 * 
	 */
	public void actualizaLogin() {
		if (modelo.isLogged()) {
		String usuario = modelo.getUsuario();
		String host = modelo.getHost();
		vista.actualizaLoginEstado(true,host,usuario);
		}else {
			vista.actualizaLoginEstado(false,"","");
		}
		
	}

	/**
	 * @param string
	 */
	public void mensajeError(String string) {
		SwingUtilities.invokeLater(() -> 
		vista.msgError(string));
		
	}

	/**
	 * 
	 */
	public void logout() {
		modelo.logout();
		
	}

	/**
	 * @return
	 */
	public void comLs() {
		if (modelo.isLogged())
			modelo.addOperacion(TiposComando.LS);
	}

	/**
	 * @param ruta
	 * @return
	 */
	public List<DtoArchivo> getArchivosLocales(File ruta) {

		List<DtoArchivo> archivos=new ArrayList<DtoArchivo>();
		//FilenameFilter filtroArchivos= (File current, String name) -> !(new File(current, name).isDirectory());
		//agregar .. si no es root
		
		if (ruta.toPath().getNameCount()!=0) {
			archivos.add(new DtoArchivo("..", Codigos.DIRECTORIO));
		}
		
		
		File[] lista = ruta.listFiles();
		if (lista!=null) {
			for (File arch : lista) {
				archivos.add(new DtoArchivo(arch.getName(), (arch.isDirectory())?Codigos.DIRECTORIO:Codigos.ARCHIVO));
			}
		}
		return archivos;	
	}

	/**
	 * @param valor
	 * @return
	 */
	public void comCd(String valor) {
		if (valor !=null && modelo.isLogged()) {
			modelo.addOperacion(TiposComando.CD+" \""+valor+"\"");
			comLs();
		}
	}

	/**
	 * @param nombre
	 */
	public void comGet(String rutaRemota, String rutaLocal) {
		if (rutaRemota!=null && rutaLocal!=null) {
			modelo.addOperacion(TiposComando.GET+" \""+rutaRemota+"\" \""+rutaLocal+"\"");
		}
	}

	public void enviarComando(String comando) {
		if (comando!=null && modelo.isLogged())
			modelo.addOperacion(comando);
	}

	/**
	 * @param nombre
	 */
	public void comDel(String valor) {
		if (valor!=null) {
			modelo.addOperacion(TiposComando.DEL+" \""+valor+"\"");
			comLs();
		}
		
	}

	/**
	 * @param valor
	 * @return
	 */
	public void comMkdir(String valor) {
		if (valor !=null && modelo.isLogged()) {
			modelo.addOperacion(TiposComando.MKDIR+" \""+valor+"\"");
			comLs();
		}
	}

	/**
	 * @param nombre
	 */
	public void comRmdir(String valor) {
		if (valor!=null) {
			modelo.addOperacion(TiposComando.RMDIR+" \""+valor+"\"");
			comLs();
		}
		
	}

	/**
	 * 
	 */
	public void actualizarLocal() {
		vista.actualizarArchivosLocales();
		
	}

	/**
	 * @param rutaLocal
	 * @param rutaRemota
	 */
	public void comPut(String rutaLocal, String rutaRemota) {
		if (rutaRemota!=null && rutaLocal!=null) {
			modelo.addOperacion(TiposComando.PUT+" \""+rutaLocal+"\" \""+rutaRemota+"\"");
			comLs();
		}
		
	}

	/**
	 * @param msg
	 * @return
	 */
	public boolean confirmar(String msg) {
		return vista.confirmar(msg);
	}


		/**
		 * Recoger unidades de disco e inicializar el selector de undidaes
		 * @return
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
	
}
