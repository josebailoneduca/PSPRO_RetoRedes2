/*
LICENCIA JOSE JAVIER BO
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
Lista de paquetes:
 */

package prueba;

import java.io.File;

/**
 *
 * @author Jose Javier Bailon Ortiz
 */
    class Archivo extends File{

        public Archivo(File f) {
            super(f.getAbsolutePath());
        }
        public Archivo(String path) {
            super(path);
        }
        @Override
        public String toString() {
            return this.getName();
        }
        
    }