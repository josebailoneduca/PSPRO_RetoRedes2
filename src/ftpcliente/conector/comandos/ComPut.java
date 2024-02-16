/**
 * 
 */
package ftpcliente.conector.comandos;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import ftpcliente.Config;
import ftpcliente.conector.Modelo;
import ftpcliente.controlador.Codigos;

/**
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComPut extends Comando {

	public ComPut(String[] comando, DataInputStream dis, DataOutputStream dos, Modelo modelo) {
		super(comando, dis, dos, modelo);

	}

	public void iniciar() {

		if (comando.length < 2) {
			return;
		}
		String archivoRemoto=null;
		String rutaLocal=null;
		try {
			// preparar rutas remotas y locales
			rutaLocal = comando[1];
			String nombreArchivo = new File(rutaLocal).getName();
			 archivoRemoto = "./" + nombreArchivo;
			boolean enviarArchivo = false;
			if (comando.length > 2)
				archivoRemoto = comando[2];

			// comprobar archivo local
			File arch = new File(rutaLocal);
			// si existe y si no es directorio
			if (arch.exists() && !arch.isDirectory()) {
				// Iniciar protocolo
				dos.writeUTF(TiposComando.PUT);
				dos.writeUTF(archivoRemoto);

				// respuesta a ver si se permite
				int res = dis.readInt();
				if (res == Codigos.OK) {
					enviarArchivo = true;
				} else if (res == Codigos.YA_EXISTE) {
					// si ya existe preguntar al usuario
					if (modelo.confirmar("¿Desea sobreescribir el archivo: " + archivoRemoto + "?")) {
						enviarArchivo = true;
						dos.writeInt(Codigos.CONTINUAR);
					} else {
						dos.writeInt(Codigos.FIN);
					}
				} else {
					modelo.msgError("PUT erroneo. No puede enviar el archivo " + archivoRemoto);
				}
			} else {
				modelo.msgError(
						"PUT erroneo. No puede enviar el archivo " + archivoRemoto + " por no existir o ser un directorio");
			}
			if (enviarArchivo) {
				int tipo = dis.readInt();
				if (tipo == Codigos.TIPO_TEXTO)
					enviarArchivoTexto(arch);
				else
					enviarArchivoBinario(arch);
				
				modelo.msgInfo("PUT exitoso. "+archivoRemoto);
			}

		} catch(UnsupportedEncodingException ex) {
			modelo.msgError("PUT erroneo. No se soporta la codificacion del archivo "+rutaLocal);
			try {
				dos.writeInt(0);
				dos.writeInt(Codigos.FIN);
			} catch (IOException e) {
			}
		}catch (IOException e) {
			modelo.msgError("PUT erroneo. Error enviando archivo "+archivoRemoto);
		}
	}

	/**
	 * @param arch
	 * @throws IOException 
	 */
	private void enviarArchivoBinario(File arch) throws IOException {

		long nBytes = arch.length();

		ArrayList<String> lineas = new ArrayList<String>();

		// leer archivo
		FileInputStream fis = new FileInputStream(arch);
			// enviar longitud
			dos.writeLong(nBytes);
			byte[] bytes = new byte[(int) nBytes];
			int leidos = fis.read(bytes, 0, bytes.length);
			try {
				dos.write(bytes, 0, bytes.length);
			} catch (EOFException ex) {

			}


	}

	/**
	 * @param arch
	 * @throws IOException 
	 */
	private void enviarArchivoTexto(File arch) throws IOException {

		int maxLineas = 10;

		ArrayList<String> lineas = new ArrayList<String>();

		// leer archivo
		FileInputStream fis = new FileInputStream(arch);
				InputStreamReader isr = new InputStreamReader(fis, Config.getCOD_TEXTO());
				BufferedReader br = new BufferedReader(isr);

			boolean continuar = true;
			while (continuar) {
				lineas.clear();
				int i = maxLineas;
				String linea = "";
				while (i > 0 && (linea = br.readLine()) != null) {
					lineas.add(linea);
					i--;
				}

				// enviar contenido recogido
				dos.writeInt(lineas.size());
				dos.flush();
				for (String l : lineas) {
					dos.writeUTF(l);
				}
				// comprobar fin de archivo
				if (linea == null) {
					dos.writeInt(Codigos.FIN);
					continuar = false;
				} else {
					dos.writeInt(Codigos.CONTINUAR);
				}
			}


	}

}
