/**
 * 
 */
package ftpcliente.vista.gui;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 * Menu contextual para la tabla de archivos remotos
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class MenuRemoto extends JPopupMenu {
	/**
	 * Referencia a la ventana
	 */
	private Ventana v; 
	
	/**
	 * Referencia a la tabla
	 */
	private JTable t;
	
	/**
	 * Constructor 
	 * @param v Referencia a la ventana
	 * @param t Referencia a la tabla
	 */
	public MenuRemoto(Ventana v, JTable t) {
		super();
		this.v = v;
		this.t = t;
		
		//item descargar
		JMenuItem itemDescargar= new JMenuItem("Descargar/GET");
		itemDescargar.setActionCommand("GET");
		itemDescargar.addActionListener(v);
		this.add(itemDescargar);
	    
	    //item crear directorio
	    JMenuItem itemCrearDirectorio= new JMenuItem("Crear directorio");
	    itemCrearDirectorio.setActionCommand("MKDIR");
	    itemCrearDirectorio.addActionListener(v);
	    this.add(itemCrearDirectorio);
	    
	    //item borrar
	    JMenuItem itemBorrar= new JMenuItem("Borrar");
	    itemBorrar.setActionCommand("BORRAR");
	    itemBorrar.addActionListener(v);
	    this.add(itemBorrar);
	    
		
	}
	 
	
	
	

}
