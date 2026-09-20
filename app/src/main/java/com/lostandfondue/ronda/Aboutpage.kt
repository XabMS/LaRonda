package com.lostandfondue.ronda

import android.os.Bundle

/**
 * Pantalla "Acerca de": texto estático con créditos, sin lógica propia.
 * Se llega aquí desde el menú de [MainActivity] (opción "Acerca de").
 */
class Aboutpage : PantallaConToolbar() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutpage)
        configurarToolbar(findViewById(R.id.toolbar))
    }
}
