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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import fptservidor.Config;
import fptservidor.Msg;
import fptservidor.modelo.Codigos;
import fptservidor.modelo.Sesion;
import fptservidor.modelo.Usuario;
import fptservidor.modelo.lib.UtilesArchivo;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComPut {
	private Usuario usuario;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Sesion sesion;
	private String cwd;

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
 
		String rutaArchivo;
		String rutaCompleta = null;
		boolean recibirArchivo = false;
		try {
			// leer ruta destino
			rutaArchivo = dis.readUTF();
			// componer y comprobar validez de ruta
			rutaCompleta = UtilesArchivo.componerRuta(rutaUsuario, cwd, rutaArchivo);
			File arch = new File(rutaCompleta);
			// comprobar si no es ruta interior del usuario o es directorio contestamos que no
			if (!UtilesArchivo.rutaDentroDeRuta(rutaCompleta, rutaUsuario + "/") || arch.isDirectory()) {
				Msg.msgHora(sesion.getDatosUsuario()+" PUT bloqueado en ruta no permitida: "+rutaCompleta);
				dos.writeInt(Codigos.MAL);
				// si no existe acepta
			} else if (!UtilesArchivo.rutaExiste(rutaCompleta)) {
				dos.writeInt(Codigos.OK);
				recibirArchivo = true;
				// si existe avisa y espera orden de fin o continuar sobreescribiendo
			} else {
				dos.writeInt(Codigos.YA_EXISTE);
				int resp = dis.readInt();
				// si responde CONTIUAR continua la recepcion
				if (resp == Codigos.CONTINUAR)
					recibirArchivo = true;
			}
			
			//si la ruta es permitida y no existe el archivo o se ha permitido la sobreescritura
			//se recibe el archivo
			if (recibirArchivo) {
				// enviar tipo de comunicacion y recibir archivo
				if (Config.isMODO_TEXTO()) {
					dos.writeInt(Codigos.TIPO_TEXTO);
					recibirArchivoTexto(arch);
				} else {
					dos.writeInt(Codigos.TIPO_BYTES);
					recibirArchivoBinario(arch);
				}
				Msg.msgHora(sesion.getDatosUsuario()+" PUT exitoso en ruta: "+rutaCompleta);
			}

		} catch (IOException e) {
			Msg.msgHora(sesion.getDatosUsuario()+" PUT erroneo en: "+rutaCompleta);

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
		try (FileOutputStream fos = new FileOutputStream(arch)) {
			long cantidadBytes = dis.readLong();
			byte[] bytes = new byte[(int) cantidadBytes];
			int offset = 0;
			while (offset < cantidadBytes) {
				int leidos = dis.read(bytes, offset, bytes.length - offset);
				fos.write(bytes, offset, leidos);
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
