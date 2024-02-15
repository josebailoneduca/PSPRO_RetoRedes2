package ftpcliente.vista.modelos;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import javax.swing.tree.TreeModel;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;



public class ArbolArchivosModel implements TreeModel {

    private ArchArbol root;
    private FilenameFilter filtroDirectorios;

    /**
     *
     * @param root
     */
    public ArbolArchivosModel(ArchArbol root) {
        this.root = root;
        filtroDirectorios= (File current, String name) -> new File(current, name).isDirectory();
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
 
    }

    @Override
    public Object getChild(Object parent, int index) {
        ArchArbol f =(ArchArbol) parent;
        return new ArchArbol(f.listFiles(filtroDirectorios)[index]);
    }

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

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        ArchArbol par = (ArchArbol) parent;
        ArchArbol ch = (ArchArbol) child;
        return Arrays.asList(par.listFiles()).indexOf(ch);
    }

    @Override
    public Object getRoot() {
        return root;
    }

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
