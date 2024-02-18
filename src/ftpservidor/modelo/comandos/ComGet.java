/**
 * 
 */
package ftpservidor.modelo.comandos;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ftpservidor.Config;
import ftpservidor.modelo.Codigos;
import ftpservidor.modelo.Sesion;
import ftpservidor.modelo.lib.Msg;
import ftpservidor.modelo.lib.UtilesArchivo;

/**
 * Se encarga de manejar un comando GET en el servidor
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComGet extends Comando{
 

	/**
	 * Constructor
	 * 
	 * @param sesion Sesion que realiza la operacion
	 */
	public ComGet(Sesion sesion) {
		super(sesion);
	}

	/**
	 * Inicia la operacion siguiendo el protocolo GET (Ver estructura del protocolo
	 * en la documentacion)
	 */
	public void iniciar() {
		String rutaUsuario = usuario.getDirUsuario();
		String rutaArchivo;
		String rutaCompleta = null;
		try {
			// leer ruta
			rutaArchivo = dis.readUTF();
			// componer ruta completa
			rutaCompleta = UtilesArchivo.componerRuta(rutaUsuario, cwd, rutaArchivo);
			File arch = new File(rutaCompleta);

			// comprobar si es ruta interior del usuario, si existe y si no es directorio
			if (UtilesArchivo.rutaDentroDeRuta(rutaCompleta, rutaUsuario + "/")
					&& UtilesArchivo.rutaExiste(rutaCompleta) && !arch.isDirectory()) {
				// aceptar GET y enviar archivo
				dos.writeInt(Codigos.OK);

				// si debe ser en texto se envia siguiendo el protocolo en la parte de texto
				if (Config.isMODO_TEXTO()) {
					dos.writeInt(Codigos.TIPO_TEXTO);
					enviarArchivoTexto(arch);
				} else {
					// si debe ser en bytes envia siguiendo el protocolo en la parte de bytes
					dos.writeInt(Codigos.TIPO_BYTES);
					enviarArchivoBytes(arch);
				}
				Msg.msgHora(sesion.getDatosUsuario() + " GET exitoso: " + rutaCompleta);

				// si no se da el caso anterior devolver que no existe
			} else {
				dos.writeInt(Codigos.NO_EXISTE);
				Msg.msgHora(
						sesion.getDatosUsuario() + " GET archivo no existente/permitido bloqueado: " + rutaCompleta);
			}
			// si se ha producido algun error enviar una respuesa negativa
		} catch (IOException e) {
			try {
				dos.writeInt(Codigos.MAL);
				Msg.msgHora(sesion.getDatosUsuario() + " GET erroneo: " + rutaCompleta);
			} catch (IOException e1) {

			}
		}
	}

	/**
	 * Envio de archivo segun el protocolo GET en el modo de bytes. Va leyendo y
	 * enviando de 500 en 500 bytes hasta enviar todo el archivo
	 * 
	 * @param arch Archivo a enviar
	 * @throws IOException Si hay problemas leyendo el archivo
	 */
	private void enviarArchivoBytes(File arch) throws IOException {

		long nBytes = arch.length();

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
			fis.close();
		}

	}

	/**
	 * Envio de archivo segun el protocolo GET en el modo de texto. Va leyendo
	 * tandas de lineas y enviandolas hasta consumir el archivo.
	 * 
	 * @param arch Archivo a enviar
	 * @throws IOException Si hay  problemas leyendo el archivo
	 */
	private void enviarArchivoTexto(File arch) throws IOException {


		ArrayList<String> lineas = new ArrayList<String>();

		FileInputStream fis = new FileInputStream(arch);
		InputStreamReader isr = new InputStreamReader(fis, Config.getCOD_TEXTO());
		BufferedReader br = new BufferedReader(isr);

		//cantidad de lineas a leer por tanda 
		int maxLineas = 10;
		boolean continuar = true;
		// leer archivo
		while (continuar) {
			//leer paquete de lineas
			lineas.clear();
			int i = maxLineas;
			String linea = "";
			while (i > 0 && (linea = br.readLine()) != null) {
				lineas.add(linea);
				i--;
			}

			// enviar cantidad de lineas
			dos.writeInt(lineas.size());
			dos.flush();
			//enviar las lineas
			for (String l : lineas) {
				dos.writeUTF(l);
			}
			//si linea es null es que se ha llegado al final
			if (linea == null) {
				dos.writeInt(Codigos.FIN);
				continuar = false;
			//en caso contrario hay que continuar enviando lineas
			} else {
				dos.writeInt(Codigos.CONTINUAR);
			}
		}
		isr.close();
		fis.close();

	}
}
