/**
 * 
 */
package ftpcliente.conector.comandos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import ftpcliente.Config;
import ftpcliente.conector.Modelo;
import ftpcliente.controlador.Codigos;
import ftpcliente.controlador.dto.DtoArchivo;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComGet extends Comando {

	public ComGet(String[] comando, DataInputStream dis, DataOutputStream dos, Modelo modelo) {
		super(comando, dis, dos, modelo);

	}

	public void iniciar() {

		if (comando.length < 2) {
			return;
		}

		try {
			// preparar rutas remotas y locales
			String rutaRemota = comando[1];
			String nombreArchivo = new File(rutaRemota).getName();
			File archivoLocal = new File(new File(nombreArchivo).getAbsolutePath());
			if (comando.length > 2)
				archivoLocal = new File(comando[2]);
			// comprobacion de escritura

			if (!Files.isWritable(Paths.get(archivoLocal.getParentFile().toURI()))) {
				modelo.mensajeError("No tiene permisos para escribir en: " + archivoLocal.getAbsolutePath());
				return;
			}

			boolean recibirArchivo = false;
			if (archivoLocal.exists()) {
				if (modelo.confirmar("¿Desea sobreescribir el archivo " + archivoLocal.getAbsolutePath() + "?"))
					recibirArchivo = true;
			} else {
				recibirArchivo = true;
			}

			if (recibirArchivo) {
				// comenzar protocolo
				dos.writeUTF(TiposComando.GET);
				dos.writeUTF(rutaRemota);

				// ver si es posible
				int res = dis.readInt();
				if (res == Codigos.OK) {
					int tipo = dis.readInt();
					// si es posible se recibe el archivo
					if (tipo == Codigos.TIPO_TEXTO)
						recibirArchivoTexto(archivoLocal);
					else
						recibirArchivoBinario(archivoLocal);

					// avisar a modelo de fin de transferencia
					modelo.actualizarLocal();
				} else if (res == Codigos.NO_EXISTE) {
					modelo.mensajeError("El archivo no existe: " + rutaRemota);
				} else {
					modelo.mensajeError("No se puede obtener el archivo " + rutaRemota);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			modelo.mensajeError("No se puede descargar el archivo: " + e.getMessage());
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
			modelo.mensajeError("No se puede descargar el archivo: " + e.getMessage());
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
