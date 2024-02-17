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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import ftpcliente.Config;
import ftpcliente.conector.Codigos;
import ftpcliente.conector.Sesion;

/**
 * Se encarga de manejar un comando PUT en el cliente
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComPut extends Comando {

	/**
	 * Constructor
	 * 
	 * @param comando Partes del comando siendo el primer elemento el codigo del
	 *                comando y los siguientes los parametros
	 * @param dis     DataInputStream a usar por el comando
	 * @param dos     DataOutputStream a usar por el comando
	 * @param sesion Referencia a la sesion
	 */
	public ComPut(String[] comando, DataInputStream dis, DataOutputStream dos, Sesion sesion) {
		super(comando, dis, dos, sesion);

	}

	/**
	 * Inicia la operacion siguiendo el protocolo PUT (Ver estructura del protocolo
	 * en la documentacion)
	 */
	public void iniciar() {

		// comprobar que hay parametros suficientes
		if (comando.length < 2) {
			return;
		}

		String archivoRemoto = null;
		String rutaLocal = null;
		try {
			// preparar rutas remotas y locales
			rutaLocal = comando[1];
			String nombreArchivo = new File(rutaLocal).getName();
			//Presuponer que se pondra en el CWD remoto
			archivoRemoto = "./" + nombreArchivo;
			boolean enviarArchivo = false;
			
			//si existe el parametro entonces se ha especificado ruta remota
			if (comando.length > 2)
				archivoRemoto = comando[2];

			// comprobar archivo local
			File arch = new File(rutaLocal);
			
			// si existe y si no es directorio iniciar protocolo
			if (arch.exists() && !arch.isDirectory()) {
				
				//escribir codigo de comando
				dos.writeUTF(Comando.PUT);
				
				//escribir ruta remota donde colocar el archivo
				dos.writeUTF(archivoRemoto);

				// respuesta a ver si se permite
				int res = dis.readInt();
				if (res == Codigos.OK) {
					enviarArchivo = true;
				
					//si ya existe confirmar sobreescritura
				} else if (res == Codigos.YA_EXISTE) {
					// si ya existe preguntar al usuario
					if (conector.confirmar("¿Desea sobreescribir el archivo: " + archivoRemoto + "?")) {
						enviarArchivo = true;
						//avisar a servidor que se sobreescribira
						dos.writeInt(Codigos.CONTINUAR);
					} else {
						//avisar a servidor la anulacion de la operacion
						dos.writeInt(Codigos.FIN);
					}
				
				//si el servidor no permie el envio 
				} else {
					conector.msgError("PUT erroneo. No puede enviar el archivo " + archivoRemoto);
				}
			
			//si las condiciones locales no lo permiten
			} else {
				conector.msgError("PUT erroneo. No puede enviar el archivo " + archivoRemoto
						+ " por no existir en local o ser un directorio");
			}
			
			//si hay que enviar el archivo se inicia el envio
			if (enviarArchivo) {
				//leer tipo de transferencia de archivo
				int tipo = dis.readInt();
				
				//si la transferencia es tipo texto
				if (tipo == Codigos.TIPO_TEXTO)
					enviarArchivoTexto(arch);
				//si es tipo bytes
				else
					enviarArchivoBytes(arch);

				conector.msgInfo("PUT exitoso. " + archivoRemoto);
			}

		} catch (UnsupportedEncodingException ex) {
			conector.msgError("PUT erroneo. No se soporta la codificacion del archivo " + rutaLocal);
			try {
				dos.writeInt(0);
				dos.writeInt(Codigos.FIN);
			} catch (IOException e) {
			}
		} catch (IOException e) {
			conector.msgError("PUT erroneo. Error enviando archivo " + archivoRemoto);
		}
	}

	/**
	 * Envio de archivo usando el metodo de bytes
	 * 
	 * @param arch Archivo local a leer para enviar
	 * 
	 * @throws IOException Si hay problemas leyendo
	 */
	private void enviarArchivoBytes(File arch) throws IOException {

		//longitud de bytes
		long nBytes = arch.length();

		// leer archivo
		FileInputStream fis = new FileInputStream(arch);
		try {
			// enviar longitud
			dos.writeLong(nBytes);
			// enviar tandas de 500 bytes hasta consumir el archivo
			while (nBytes > 0) {
				int leer = 500;
				if (nBytes < 500)
					leer = (int) nBytes;
				byte[] bytes = new byte[leer];
				int leidos = fis.read(bytes, 0, bytes.length);
				nBytes -= leidos;
				dos.write(bytes, 0, leidos);
			}
			fis.close();
		} catch (EOFException ex) {
		}
		fis.close();

	}

	/**
	 * Envio de archivo usando el metodo de texto
	 * 
	 * @param arch Archivo local a leer para enviar
	 * 
	 * @throws IOException Si hay problemas leyendo
	 */
	private void enviarArchivoTexto(File arch) throws IOException {

		//max de lineas por tanda
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
			//leer lineas de la tanda
			while (i > 0 && (linea = br.readLine()) != null) {
				lineas.add(linea);
				i--;
			}

			// enviar numnero de lineas de la tanda
			dos.writeInt(lineas.size());
			dos.flush();
			//enviar lineas de la tanda
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
