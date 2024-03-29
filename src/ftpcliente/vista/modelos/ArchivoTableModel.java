/**
 * 
 */
package ftpcliente.vista.modelos;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ftpcliente.conector.Codigos;
import ftpcliente.controlador.dto.DtoArchivo;
/**
 * Table model para mostrar archivos
 * 
 * @author Jose Javier Bailon Ortiz
 * @see DtoArchivo
 */
public class ArchivoTableModel extends AbstractTableModel{

	String[] columnas = {"Tipo","Nombre"};
	List<DtoArchivo> items = new ArrayList<DtoArchivo>();
	
	
	
	public ArchivoTableModel(List<DtoArchivo> items) {
		super();
		this.items = items;
		
	}
 
	
	@Override
	public int getRowCount() {
		return items.size();
	}

	@Override
	public int getColumnCount() {
		return columnas.length;
	}

	
	
	@Override
	public String getColumnName(int column) {
		 
		return columnas[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		DtoArchivo arc = items.get(rowIndex);
		return switch (columnIndex) {
		case 0 -> 	(arc.getTipo()==Codigos.ARCHIVO)?"Arch.":"Dir.";
		case 1 -> 	arc.getNombre();
		default ->  "";
		};	
	}
	
	
	/**
	 * Devuelve un DtoArchivo correspondiente al indice
	 * 
	 * @param indice El indice
	 * 
	 * @return El DtoArchivo conrresponediente
	 */
	public DtoArchivo getItem(int indice) {
		if (items.size()>indice && indice>-1) {
			return items.get(indice);
		}else {
			return null;
		}
		
	}
}
