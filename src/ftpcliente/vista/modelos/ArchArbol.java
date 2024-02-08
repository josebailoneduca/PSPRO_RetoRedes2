/*
LICENCIA JOSE JAVIER BO
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
Lista de paquetes:
 */

package ftpcliente.vista.modelos;

import java.io.File;

/**
 *
 * @author Jose Javier Bailon Ortiz
 */
    public class ArchArbol extends File{

        public ArchArbol(File f) {
            super(f.getAbsolutePath());
        }
        public ArchArbol(String path) {
            super(path);
        }
        
        public ArchArbol (ArchArbol padre, String name){
            super (padre,name);
        }
        @Override
        public String toString() {
            return this.getName();
        }
        
    }