/**
 * 
 */
package ftpcliente.vista.modelos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ftpcliente.controlador.Codigos;
import ftpcliente.controlador.dto.DtoArchivo;
/**
 * 
 * @author Bailon
 */
public class ArchivoTableModel extends AbstractTableModel{

	String[] columnas = {"Tipo","Nombre"};
	List<DtoArchivo> items = new ArrayList<DtoArchivo>();
	
	
	
	public ArchivoTableModel(List<DtoArchivo> items) {
		super();
		this.items = items;
		System.out.println(items.size());
		
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
}
