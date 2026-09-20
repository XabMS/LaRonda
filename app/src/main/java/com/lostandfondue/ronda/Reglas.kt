package com.lostandfondue.ronda

import android.os.Bundle

/**
 * Pantalla "Reglas": texto estático con las reglas del juego, sin lógica
 * propia. Se llega aquí desde el menú de [MainActivity] (opción "Reglas").
 */
class Reglas : PantallaConToolbar() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reglas)
        configurarToolbar(findViewById(R.id.toolbar))
    }
}
