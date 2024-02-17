/**
 * 
 */
package ftpcliente.vista.gui;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 * Menu contextual para la tabla de archivos locales
 * 
 * @author Jose Javier Bailon Ortiz
 */
public class MenuLocal extends JPopupMenu {
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
	public MenuLocal(Ventana v, JTable t) {
		super();
		this.v = v;
		this.t = t;
		//item actualizar
	    JMenuItem itemActualizar= new JMenuItem("Actualizar");
	    itemActualizar.setActionCommand("ACTUALIZAR");
	    itemActualizar.addActionListener(v);
	    this.add(itemActualizar);
	    
	    //item subir
	    JMenuItem itemSubir= new JMenuItem("Subir/PUT");
	    itemSubir.setActionCommand("PUT");
	    itemSubir.addActionListener(v);
	    this.add(itemSubir);
	    
	    //item crear directorio
	    JMenuItem itemCrearDirectorio= new JMenuItem("Crear directorio");
	    itemCrearDirectorio.setActionCommand("MKDIRLOCAL");
	    itemCrearDirectorio.addActionListener(v);
	    this.add(itemCrearDirectorio);
	}
}
