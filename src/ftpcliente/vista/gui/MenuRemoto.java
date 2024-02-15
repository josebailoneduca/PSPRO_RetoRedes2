/**
 * 
 */
package ftpcliente.vista.gui;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 * 
 * @author Bailon
 */
public class MenuRemoto extends JPopupMenu {
	Ventana v;
	JTable t;
	public MenuRemoto(Ventana v, JTable t) {
		super();
		this.v = v;
		this.t = t;
		
	    JMenuItem itemBorrar= new JMenuItem("Borrar");
	    itemBorrar.setActionCommand("BORRAR");
	    itemBorrar.addActionListener(v);
	    this.add(itemBorrar);
	    JMenuItem itemCrearDirectorio= new JMenuItem("Crear directorio");
	    itemCrearDirectorio.setActionCommand("MKDIR");
	    itemCrearDirectorio.addActionListener(v);
	    this.add(itemCrearDirectorio);
	    JMenuItem itemDescargar= new JMenuItem("Descargar");
	    itemDescargar.setActionCommand("GET");
	    itemDescargar.addActionListener(v);
	    this.add(itemDescargar);
	    
		
	}
	 
	
	
	

}
