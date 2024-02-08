package prueba;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Arrays;
import javax.swing.tree.TreeModel;

public class FileTreeModel implements TreeModel {

    private File root;

    public FileTreeModel(File root) {
        this.root = new Archivo(root);
    }

    @Override
    public void addTreeModelListener(javax.swing.event.TreeModelListener l) {
 
    }

    @Override
    public Object getChild(Object parent, int index) {
        File f = (Archivo) parent;
        return new Archivo(f.listFiles(new FilenameFilter() {
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        })[index]);
    }

    @Override
    public int getChildCount(Object parent) {
        File f = (File) parent;
        if (!f.isDirectory()) {
            return 0;
        } else {
            if (f.list(new FilenameFilter() {
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        }) != null) {
                return f.list(new FilenameFilter() {
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        }).length;
            } else {
                return 0;
            }
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        Archivo par = (Archivo) parent;
        Archivo ch = (Archivo) child;
        return Arrays.asList(par.listFiles()).indexOf(ch);
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public boolean isLeaf(Object node) {
        File f = (File) node;
        return !f.isDirectory();
    }

    @Override
    public void removeTreeModelListener(javax.swing.event.TreeModelListener l) {
        //do nothing
    }

    @Override
    public void valueForPathChanged(javax.swing.tree.TreePath path, Object newValue) {
   
    }

}
