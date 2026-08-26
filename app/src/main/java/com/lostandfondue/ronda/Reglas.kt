package com.lostandfondue.ronda

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.os.Bundle
import android.view.View

/**
 * Pantalla "Reglas": texto estático con las reglas del juego, sin lógica
 * propia. Se llega aquí desde el menú de [MainActivity] (opción "Reglas").
 */
class Reglas : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reglas)
        // Necesario desde targetSdk 36: sin esto el contenido se dibuja
        // detrás de la barra de estado (ver InsetsExt.kt).
        findViewById<View>(android.R.id.content).applySystemBarInsetsAsPadding()
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))
    }
}
