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
		
	
	    JMenuItem itemActualizar= new JMenuItem("Actualizar");
	    itemActualizar.setActionCommand("ACTUALIZAR");
	    itemActualizar.addActionListener(v);
	    this.add(itemActualizar);
	    JMenuItem itemDescargar= new JMenuItem("Subir/PUT");
	    itemDescargar.setActionCommand("PUT");
	    itemDescargar.addActionListener(v);
	    this.add(itemDescargar);
	    JMenuItem itemCrearDirectorio= new JMenuItem("Crear directorio");
	    itemCrearDirectorio.setActionCommand("MKDIRLOCAL");
	    itemCrearDirectorio.addActionListener(v);
	    this.add(itemCrearDirectorio);

		
	}
	 
	
	
	

}
