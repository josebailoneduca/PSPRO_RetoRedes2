/**
 * 
 */
package ftpcliente.controlador.dto;

import ftpcliente.conector.Codigos;

/**
 * DTO que representa los datos basicos de un archivo: nombre y tipo (archivo o directorio)
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class DtoArchivo {
	
	/**
	 * Nombre del archivo
	 */
	private String nombre;
	
	/**
	 * Tipo (directorio o archivo segun lo establecido en Codigos)
	 * @see Codigos
	 */
	private int tipo;
	
	
	/**
	 * Constructor 
	 * @param nombre El nombre de archivo
	 * @param tipo El tipo de archivo (directorio y archivo)
	 * @see Codigos
	 */
	public DtoArchivo(String nombre, int tipo) {
		super();
		this.nombre = nombre;
		this.tipo = tipo;
	}
	
	/**
	 * Get nombre
	 * @return El nombre
	 */
	public String getNombre() {
		return nombre;
	}
	
	/**
	 * Set nombre
	 * @param nombre El nombre a definir
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	/**
	 * Get tipo
	 * @return El tipo del archivo
	 */
	public int getTipo() {
		return tipo;
	}
	
	/**
	 * Set tipo
	 * @param tipo El tipo del archivo a definir
	 */
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	
	/**
	 * Devuelve si es directorio o no
	 * @return True si es directorio, false si no lo es
	 */
	public boolean esDirectorio() {
		return tipo == Codigos.DIRECTORIO;
	}
	@Override
	public String toString() {
		return "DtoArchivo [nombre=" + nombre + ", tipo=" + tipo + "]";
	}
	
}
