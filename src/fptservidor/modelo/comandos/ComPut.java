/**
 * 
 */
package fptservidor.modelo.comandos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
public class ComPut {
	private	Usuario usuario;
	private	DataInputStream dis;
	private	DataOutputStream dos;
	private	Sesion sesion;
	private	String cwd;

	public ComPut(Sesion sesion) {
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
			// leer ruta destino
			rutaArchivo = dis.readUTF();
			// componer y comprobar validez de ruta
			rutaCompleta = UtilesArchivo.componerRuta(rutaUsuario, cwd, rutaArchivo);
			File arch = new File(rutaCompleta);
			// comprobar si es ruta interior del usuario, y si existe o no es directorio en caso de existir
			if (UtilesArchivo.rutaDentroDeRuta(rutaCompleta, rutaUsuario + "/")
					&& (!UtilesArchivo.rutaExiste(rutaCompleta) || !arch.isDirectory())) {
				//aceptar PUT y enviar archivo
				dos.writeInt(Codigos.OK);
				if (Config.isMODO_TEXTO())
				recibirArchivoTexto(arch);
				else
				recibirArchivoBinario(arch);
				
			} else {
				dos.writeInt(Codigos.MAL);
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
	 * @param archivoLocal
	 */
	private void recibirArchivoBinario(File arch) {
		crearRuta(arch);
		try (RandomAccessFile raf = new RandomAccessFile(arch, "rw")) {
			long cantidadBytes = dis.readLong();
			byte[] bytes = new byte[(int) cantidadBytes];
			int offset = 0;
			while (offset < cantidadBytes) {
				int leidos = dis.read(bytes, offset, bytes.length - offset);
				raf.write(bytes, offset, leidos);
				offset += leidos;
			}

			// ver si hay que continuar
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */

	private void recibirArchivoTexto(File arch) {
		crearRuta(arch);
		try (FileWriter fw = new FileWriter(arch); BufferedWriter bw = new BufferedWriter(fw);) {
			boolean continuar = true;
			while (continuar) {
				int cantidadLineas = dis.readInt();
				for (int i = 0; i < cantidadLineas; i++) {
					String linea = dis.readUTF();
					bw.write(linea);
					bw.newLine();
					bw.flush();
				}
				// ver si hay que continuar
				int resContinua;
				resContinua = dis.readInt();
				continuar = resContinua == Codigos.CONTINUAR;
			}
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param arch
	 */
	private void crearRuta(File arch) {
		File rutaDirectorio = new File(arch.getParentFile().getAbsolutePath());
		if (!rutaDirectorio.exists())
			rutaDirectorio.mkdirs();
		
	}

}