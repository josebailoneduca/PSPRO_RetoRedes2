/**
 * 
 */
package ftpcliente.conector.comandos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
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
			File archivoLocal = new File(nombreArchivo);
			if (comando.length > 2)
				archivoLocal = new File(comando[2]);
			//comprobacion de escritura
			if(!Files.isWritable(Paths.get(archivoLocal.getParentFile().toURI()))){
				modelo.mensajeError("No tiene permisos para escribir en: "+archivoLocal.getAbsolutePath());
				return;
				}
			
			
			// comenzar protocolo
			dos.writeUTF(TiposComando.GET);
			dos.writeUTF(rutaRemota);
			// ver si es posible
			int res = dis.readInt();
			if (res != Codigos.OK) {
				modelo.mensajeError("No se puede obtener el archivo " + rutaRemota);
			} else {
				System.out.println("recibiendo archivo de texto");
				//recibirArchivoTexto(archivoLocal);
				recibirArchivoBinario(archivoLocal);
				System.out.println("terminada la recepcion");
				modelo.actualizarLocal();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("comando get terminado");
	}

	/**
	 * @param archivoLocal
	 */
	private void recibirArchivoBinario(File arch) {

		try (RandomAccessFile raf = new RandomAccessFile(arch,"rw")){
			long cantidadBytes= dis.readLong();
			byte[] bytes = new byte[(int)cantidadBytes];
			int offset = 0;
			while (offset<cantidadBytes) {
				int leidos=dis.read(bytes, offset, bytes.length-offset);
				//System.out.println("Esperado: "+cantidadBytes+" O " +bytes.length+" leido: "+leidos);
				raf.write(bytes, offset, leidos);
				offset+=leidos;
			}
 
			//ver si hay que continuar
		} catch (IOException e) {
			modelo.mensajeError("No se puede descargar el archivo: "+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */

	private void recibirArchivoTexto(File arch) {
		try (	
				FileWriter fw = new FileWriter(arch);
				BufferedWriter bw = new BufferedWriter(fw);
				){
		boolean continuar = true;
		InputStreamReader isr = new InputStreamReader(dis,Config.getCOD_TEXTO());
		while (continuar) {
			int cantidadLineas= dis.readInt();
			for(int i=0;i<cantidadLineas;i++) {
				String linea = dis.readUTF();
				bw.write(linea);
				bw.newLine();
				bw.flush();
			}
			//ver si hay que continuar
			int resContinua;
				resContinua = dis.readInt();
			continuar = resContinua==Codigos.CONTINUAR;
		}
		bw.close();
		fw.close();
		} catch (IOException e) {
			modelo.mensajeError("No se puede descargar el archivo: "+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	private String leerLinea(InputStreamReader isr) {

		String linea="";
		int ch;
		try {
			while ((ch = isr.read()) !='\n' ) {
				linea+=Character.toString(ch);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return linea;
	}

}
