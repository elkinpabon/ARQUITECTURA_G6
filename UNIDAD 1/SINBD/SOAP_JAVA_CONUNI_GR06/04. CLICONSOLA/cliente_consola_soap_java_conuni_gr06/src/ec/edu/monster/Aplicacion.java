package ec.edu.monster;

import ec.edu.monster.controlador.ControladorConsola;

/**
 * Punto de entrada de la aplicacion cliente consola.
 * Delegamos todo al ControladorConsola para respetar MVC.
 */
public class Aplicacion {

    public static void main(String[] args) {
        new ControladorConsola().ejecutar();
    }
}
