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
	    
		
	}
	 
	
	
	

}
