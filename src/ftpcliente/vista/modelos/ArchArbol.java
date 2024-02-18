/*
LICENCIA JOSE JAVIER BO
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
Lista de paquetes:
 */

package ftpcliente.vista.modelos;

import java.io.File;

/**
 * Estructura de datos para ser usada en un ArbolArchivosModel
 * 
 * @author Jose Javier Bailon Ortiz
 * @see ArbolArchivosModel
 */
    public class ArchArbol extends File{

    	/**
    	 * Constructor
    	 * @param f Archivo a partir del cual crearlo
    	 */
        public ArchArbol(File f) {
            super(f.getAbsolutePath());
        }
        
        /**
         * Constructor a partir de una ruta
         * @param path Ruta de la raiz
         */
        public ArchArbol(String path) {
            super(path);
        }
        
        /**
         *  Constructor a partir de un padre y el nombre de su hijo
         * @param padre  El padre
         * @param name El nombre del hijo
         */
        public ArchArbol (ArchArbol padre, String name){
            super (padre,name);
        }
        @Override
        public String toString() {
            return this.getName();
        }
        
    }