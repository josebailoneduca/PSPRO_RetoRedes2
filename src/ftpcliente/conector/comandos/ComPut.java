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
import java.io.RandomAccessFile;
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

		try {
			// preparar rutas remotas y locales
			String rutaLocal = comando[1];
			String nombreArchivo = new File(rutaLocal).getName();
			String archivoRemoto = "./" + nombreArchivo;
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
					if (Config.isMODO_TEXTO())
						enviarArchivoTexto(arch);
					else
						enviarArchivoBinario(arch);
				}else {
					modelo.mensajeError("No puede sustituir un directorio con un archivo");
				}

			} else {
				modelo.mensajeError("El archivo no existe o es un directorio: " + arch.getAbsolutePath());
			}

		} catch (IOException e) {
			modelo.mensajeError("Error enviando archivo");
		}
	}
	
	
	
	
	
	
	/**
	 * @param arch
	 */
	private void enviarArchivoBinario(File arch) {

		long nBytes = arch.length();

		ArrayList<String> lineas = new ArrayList<String>();

		// leer archivo
			try (
				RandomAccessFile raf =new RandomAccessFile(arch, "r");
				){
				//enviar longitud
				dos.writeLong(nBytes);
				byte[] bytes = new byte[(int) nBytes];
				int leidos=raf.read(bytes, 0, bytes.length);
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
				//comprobar fin de archivo
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