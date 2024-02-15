/**
 * 
 */
package fptservidor.modelo.comandos;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import fptservidor.Config;
import fptservidor.modelo.Codigos;
import fptservidor.modelo.Sesion;
import fptservidor.modelo.Usuario;
import fptservidor.modelo.lib.UtilesArchivo;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComGet {
	private	Usuario usuario;
	private	DataInputStream dis;
	private	DataOutputStream dos;
	private	Sesion sesion;
	private	String cwd;

	public ComGet(Sesion sesion) {
		super();
		this.sesion = sesion;
		this.usuario = sesion.getUsuario();
		this.dis = sesion.getDis();
		this.dos = sesion.getDos();
		this.cwd = sesion.getCwd();
	}

	public Object iniciar() {
		String rutaUsuario = usuario.getCarpeta();
		// comprobar ruta dentro del usuario
		String cwd = sesion.getCwd();
		String rutaArchivo;
		String rutaCompleta = null;
		try {
			// leer ruta
			rutaArchivo = dis.readUTF();
			// componer y comprobar validez de ruta
			rutaCompleta = UtilesArchivo.componerRuta(rutaUsuario, cwd, rutaArchivo);
			File arch = new File(rutaCompleta);
			// comprobar si es ruta interior del usuario, si existe y si no es directorio
			if (UtilesArchivo.rutaDentroDeRuta(rutaCompleta, rutaUsuario + "/")
					&& UtilesArchivo.rutaExiste(rutaCompleta) && !arch.isDirectory()) {
				//aceptar GET y enviar archivo
				dos.writeInt(Codigos.OK);
				
				//si debe ser en texto
				if (Config.isMODO_TEXTO()) {
					dos.writeInt(Codigos.TIPO_TEXTO);
					enviarArchivoTexto(arch);
				}else {
					//si debe ser en bytes
					dos.writeInt(Codigos.TIPO_BYTES);
				    enviarArchivoBinario(arch);
				}
				
			} else {
				dos.writeInt(Codigos.NO_EXISTE);
			}
		} catch (IOException e) {
			try {
				dos.writeInt(Codigos.MAL);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * @param arch
	 */
	private void enviarArchivoBinario(File arch) {

		long nBytes = arch.length();

		ArrayList<String> lineas = new ArrayList<String>();

		// leer archivo
			try (
				FileInputStream fis =new FileInputStream(arch);
				){
				//enviar longitud
				dos.writeLong(nBytes);
				byte[] bytes = new byte[(int) nBytes];
				int leidos=fis.read(bytes, 0, bytes.length);
				try {
					dos.write(bytes, 0, bytes.length);
 				}catch(EOFException ex) {
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		
	}

	/**
	 * @param arch
	 */
	private void enviarArchivoTexto(File arch) {

		int maxLineas = 10;

		ArrayList<String> lineas = new ArrayList<String>();

		// leer archivo
			try (
				FileInputStream fis = new FileInputStream(arch);
				InputStreamReader isr = new InputStreamReader(fis, Config.getCOD_TEXTO());
				BufferedReader br = new BufferedReader(isr);
					){
				
				
				boolean continuar=true;
				while (continuar) {
					lineas.clear();
				int i = maxLineas;
				String linea="";
				while (i > 0 && (linea=br.readLine())!=null) {
					lineas.add(linea);
					i--;
				}
				
				//enviar contenido recogido
				dos.writeInt(lineas.size());
				dos.flush();
				for (String l : lineas) {

					dos.writeUTF(l);
				}
				if (linea==null) {
					dos.writeInt(Codigos.FIN);
					continuar=false;
				}
				else {
					dos.writeInt(Codigos.CONTINUAR);
				}
			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			

	}
}
