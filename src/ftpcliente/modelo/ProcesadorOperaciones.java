/**
 * 
 */
package ftpcliente.modelo;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 
 * @author Bailon
 */
public class ProcesadorOperaciones extends Thread{
	
    private LinkedBlockingDeque<String> operaciones;
    private InputStream is;
    private OutputStream os;
    private boolean funcionando = true;
	public ProcesadorOperaciones(LinkedBlockingDeque<String> operaciones,InputStream is, OutputStream os) {
		this.operaciones = operaciones;
		this.is=is;
		this.os=os;
	}

	@Override
	public void run() {
		while (funcionando) {
			String operacion = operaciones.remove();
			String[] comando = extraerPartesComando(operacion);
			if (comando.length>0) {
				
				}
			}
		}
		
	}

	/**
	 * @param operacion
	 * @return
	 */
	private String[] extraerComando(String operacion) {
		String comando ="";
		String parametro ="";
		for (int i=0;i<operacion.length();i++) {
			if (operacion.charAt(i)==' ') {
				while(i<operacion.length()) {
					parametro+=operacion.charAt(i);
					i++;
				}
			}else {
				comando+=operacion.charAt(i);
			}
		}
		String[] salida = {comando,parametro};
		return salida;
	}
	
	
}
