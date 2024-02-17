package ftpcliente.vista.modelos;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import javax.swing.tree.TreeModel;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;


/**
 * TreeModel del arbol de directorios
 * 
 * @author Jose Javier Bailon Ortiz
 * @see ArchArbol
 */
public class ArbolArchivosModel implements TreeModel {

	/**
	 * Raiz del arbol
	 */
    private ArchArbol root;
    
    /**
     * Filtro para solo directorios
     */
    private FilenameFilter filtroDirectorios;

    /**
     * Constructor
     * 
     * @param root Objeto ArchArbol con la raiz el arbol
     * @see ArchArbol
     */
    public ArbolArchivosModel(ArchArbol root) {
        this.root = root;
        filtroDirectorios= (File current, String name) -> new File(current, name).isDirectory();
    }

    
    @Override
    public void addTreeModelListener(TreeModelListener l) {
 
    }

    /**
     * Devolver lo hijos de un nodo. Recoje los archivos de un directorio
     */
    @Override
    public Object getChild(Object parent, int index) {
        ArchArbol f =(ArchArbol) parent;
        return new ArchArbol(f.listFiles(filtroDirectorios)[index]);
    }

    /**
     * Devuelve el numero de hijos de un nodo. El numero de archivos de un directorio
     */
    @Override
    public int getChildCount(Object parent) {
        File f = (File) parent;
        //si es directorio 0
        if (!f.isDirectory()) {
            return 0;
        } else {
            String[] hijos =f.list(filtroDirectorios);
            //si no es directorio
            if (hijos != null) {
                return hijos.length;
            } else {
                return 0;
            }
        }
    }

    /**
     * Indice de un hijo 
     */
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        ArchArbol par = (ArchArbol) parent;
        ArchArbol ch = (ArchArbol) child;
        return Arrays.asList(par.listFiles()).indexOf(ch);
    }

    /**
     * Devuelve la raiz del arbol
     */
    @Override
    public Object getRoot() {
        return root;
    }

    /**
     * True si es hoja del arbol, false si no lo es
     */
    @Override
    public boolean isLeaf(Object node) {
        return !((File) node).isDirectory();
    }

    
    @Override
    public void removeTreeModelListener(TreeModelListener l) {

    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    	
    }

}
