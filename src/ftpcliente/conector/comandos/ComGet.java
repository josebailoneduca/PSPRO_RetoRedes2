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
 * Se encarga de manejar un comando GET en el cliente
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComGet extends Comando {

	/**
	 * Constructor
	 * 
	 * @param comando Partes del comando siendo el primer elemento el codigo del
	 *                comando y los siguientes los parametros
	 * @param dis     DataInputStream a usar por el comando
	 * @param dos     DataOutputStream a usar por el comando
	 * @param modelo  Referencia al modelo
	 */
	public ComGet(String[] comando, DataInputStream dis, DataOutputStream dos, Modelo modelo) {
		super(comando, dis, dos, modelo);

	}

	/**
	 * Inicia la operacion siguiendo el protocolo GET (Ver estructura del protocolo
	 * en la documentacion)
	 */
	public void iniciar() {

		// comprobar que hay parametros suficientes
		if (comando.length < 2) {
			return;
		}

		// recoger ruta remota
		String rutaRemota = comando[1];
		try {
			// preparar rutas remotas y locales presponiendo que en local es relativa a la
			// ejecucion
			String nombreArchivoRemoto = new File(rutaRemota).getName();
			File archivoLocal = new File(new File(nombreArchivoRemoto).getAbsolutePath());

			// si hay segundo parametro es que se ha definido el sitio al que descargar
			if (comando.length > 2)
				archivoLocal = new File(comando[2]);

			// comprobacion de premiso de escritura
			if (!Files.isWritable(Paths.get(archivoLocal.getParentFile().toURI()))) {
				modelo.msgError("GET erroneo. No tiene permisos para escribir en: " + archivoLocal.getAbsolutePath());
				return;
			}

			boolean recibirArchivo = false;

			// comprobar si existe y confirmar sobreescritura si existe
			if (archivoLocal.exists()) {
				if (modelo.confirmar("¿Desea sobreescribir el archivo " + archivoLocal.getAbsolutePath() + "?"))
					recibirArchivo = true;
			} else {
				recibirArchivo = true;
			}

			// si se va a recibir el archivo iniciar el protocolo
			if (recibirArchivo) {
				// escribir codigo de comando
				dos.writeUTF(Comando.GET);

				// escribir ruta remota que obtener
				dos.writeUTF(rutaRemota);

				// ver si es posible
				int res = dis.readInt();

				// si es posible recibir el archivo
				if (res == Codigos.OK) {
					// ver el tipo de transferencia de archivo
					int tipo = dis.readInt();

					// si modo texto
					if (tipo == Codigos.TIPO_TEXTO)
						recibirArchivoTexto(archivoLocal);
					// si modo bytes
					else
						recibirArchivoBytes(archivoLocal);

					// avisar a modelo de fin de transferencia
					modelo.actualizarArchivosLocales();
					modelo.msgInfo("GET exitoso. Archivo: " + archivoLocal);

					// si no se puede recibir el archivo
				} else if (res == Codigos.NO_EXISTE) {
					modelo.msgError("GET erroneo. El archivo no existe: " + rutaRemota);
				} else {
					modelo.msgError("GET erroneo.No se puede obtener el archivo " + rutaRemota);
				}
			}
		} catch (IOException e) {
			modelo.msgError("GET erroneo.No se puede descargar el archivo: " + rutaRemota + " - " + e.getMessage());
		}
	}

	/**
	 * Recibe un archivo siguiendo el caso de transferencia por bytes
	 * 
	 * @param arch Archivo en el que escribir
	 * 
	 * @throws IOException Si hay algun problema de escritura
	 */
	private void recibirArchivoBytes(File arch) throws IOException {

		// crear la ruta hasta el archivo
		crearRuta(arch);
		// recibir los bytes
		FileOutputStream fos = new FileOutputStream(arch);
		long cantidadBytes = dis.readLong();
		byte[] bytes = new byte[(int) cantidadBytes];
		int offset = 0;
		while (offset < cantidadBytes) {
			int leidos = dis.read(bytes, offset, bytes.length - offset);
			fos.write(bytes, offset, leidos);
			offset += leidos;
		}
		fos.close();

	}

	/**
	 * Recibe un archivo siguiendo el caso de transferencia como texto
	 * 
	 * @param arch Archivo en el que escribir
	 * 
	 * @throws IOException Si hay algun problema de escritura
	 */

	private void recibirArchivoTexto(File arch) throws IOException {
		
		//crear ruta hasta el archivo
		crearRuta(arch);
		
		FileWriter fw = new FileWriter(arch);
		BufferedWriter bw = new BufferedWriter(fw);
		boolean continuar = true;
		
		//lectura de lineas
		while (continuar) {
			//leer cantidad de lineas en la siguiente tanda
			int cantidadLineas = dis.readInt();
			//leer y escribir lineas de la sigueiten tanda
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
	}

	/**
	 * Crea la ruta hasta el archivo
	 * 
	 * @param arch El archivo
	 */
	private void crearRuta(File arch) {
		File rutaDirectorio = new File(arch.getParentFile().getAbsolutePath());
		if (!rutaDirectorio.exists())
			rutaDirectorio.mkdirs();

	}
}
