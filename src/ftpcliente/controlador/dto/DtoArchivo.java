/**
 * 
 */
package ftpcliente.controlador.dto;

import fptservidor.modelo.Codigos;

/**
 * 
 * @author Bailon
 */
public class DtoArchivo {
	String nombre;
	int tipo;
	public DtoArchivo(String nombre, int tipo) {
		super();
		this.nombre = nombre;
		this.tipo = tipo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	
	public boolean esDirectorio() {
		return tipo == Codigos.DIRECTORIO;
	}
	@Override
	public String toString() {
		return "DtoArchivo [nombre=" + nombre + ", tipo=" + tipo + "]";
	}
	
}
