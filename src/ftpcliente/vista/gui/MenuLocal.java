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
public class MenuLocal extends JPopupMenu {
	Ventana v;
	JTable t;
	public MenuLocal(Ventana v, JTable t) {
		super();
		this.v = v;
		this.t = t;
		
	
	    JMenuItem itemCrearDirectorio= new JMenuItem("Actualizar");
	    itemCrearDirectorio.setActionCommand("ACTUALIZAR");
	    itemCrearDirectorio.addActionListener(v);
	    this.add(itemCrearDirectorio);
	    JMenuItem itemDescargar= new JMenuItem("Subir/PUT");
	    itemDescargar.setActionCommand("PUT");
	    itemDescargar.addActionListener(v);
	    this.add(itemDescargar);
	    
		
	}
	 
	
	
	

}
