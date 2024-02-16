/**
 * 
 */
package ftpservidor.modelo.comandos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import ftpservidor.Config;
import ftpservidor.Msg;
import ftpservidor.modelo.Codigos;
import ftpservidor.modelo.Sesion;
import ftpservidor.modelo.lib.UtilesArchivo;

/**
 * Se encarga de manejar un comando PUT en el servidor
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class ComPut extends Comando{
 

	/**
	 * Constructor
	 * 
	 * @param sesion Sesion que realiza la operacion
	 */
	public ComPut(Sesion sesion) {
		super(sesion);
	}

	/**
	 * Inicia la operacion siguiendo el protocolo PUT (Ver estructura del protocolo
	 * en la documentacion)
	 */
	public void iniciar() {
		String rutaUsuario = usuario.getDirUsuario();
		// comprobar ruta dentro del usuario
		String rutaArchivo;
		String rutaCompleta = null;
		boolean recibirArchivo = false;
		try {
			// leer ruta destino
			rutaArchivo = dis.readUTF();

			// componer ruta completa
			rutaCompleta = UtilesArchivo.componerRuta(rutaUsuario, cwd, rutaArchivo);
			File arch = new File(rutaCompleta);

			// si no es ruta interior del usuario o es directorio contestamos que no se
			// puede
			if (!UtilesArchivo.rutaDentroDeRuta(rutaCompleta, rutaUsuario + "/") || arch.isDirectory()) {
				Msg.msgHora(sesion.getDatosUsuario() + " PUT bloqueado en ruta no permitida: " + rutaCompleta);
				dos.writeInt(Codigos.MAL);
				// si no existe acepta

				// si no es directorio se recibe
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

			// si la ruta es permitida y no existe el archivo o se ha permitido la
			// sobreescritura
			// se recibe el archivo
			if (recibirArchivo) {
				// si debe ser en texto se recibe siguiendo el protocolo en la parte de texto
				if (Config.isMODO_TEXTO()) {
					dos.writeInt(Codigos.TIPO_TEXTO);
					recibirArchivoTexto(arch);
				} else {
					// si debe ser en bytes recibe siguiendo el protocolo en la parte de bytes
					dos.writeInt(Codigos.TIPO_BYTES);
					recibirArchivoBytes(arch);
				}
				Msg.msgHora(sesion.getDatosUsuario() + " PUT exitoso en ruta: " + rutaCompleta);
			}

		} catch (IOException e) {
			Msg.msgHora(sesion.getDatosUsuario() + " PUT erroneo en: " + rutaCompleta);

			try {
				dos.writeInt(Codigos.MAL);
			} catch (IOException e1) {
			}
		}
	}

	/**
	 * Recibe y escribe el archivo en disco siguiendo el protocolo PUT en su modo de
	 * envio por bytes
	 * 
	 * @param arch Archivo a escribir
	 * 
	 * @throws IOException Si se produce algun problema con la escritura
	 */
	private void recibirArchivoBytes(File arch) throws IOException {
		crearRuta(arch);
		FileOutputStream fos = new FileOutputStream(arch);
		// recoger cantidad de bytes
		long cantidadBytes = dis.readLong();

		// leer los bytes y escribirlos a disco
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
	 * Recibe un archivo siguiendo el protocolo PUT en su modo de texto
	 * 
	 * @param arch El archivo a escribir
	 * 
	 * @throws IOException Si hay algun problema con la escritura
	 */
	private void recibirArchivoTexto(File arch) throws IOException {
		// crea la ruta del archivo
		crearRuta(arch);

		FileWriter fw = new FileWriter(arch);
		BufferedWriter bw = new BufferedWriter(fw);
		boolean continuar = true;
		
		//bucle de lectura
		while (continuar) {
			//leer cantidad de lineas en la siguiente tanda
			int cantidadLineas = dis.readInt();
			
			//leer lineas de la tanda
			for (int i = 0; i < cantidadLineas; i++) {
				String linea = dis.readUTF();
				bw.write(linea);
				bw.newLine();
				bw.flush();
			}
			// ver si hay que continuar o ha terminado
			int resContinua;
			resContinua = dis.readInt();
			continuar = resContinua == Codigos.CONTINUAR;
		}
		//cerrar archivo
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
