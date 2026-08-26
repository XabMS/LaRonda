package com.lostandfondue.ronda

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.os.Bundle
import android.view.View

/**
 * Pantalla "Acerca de": texto estático con créditos, sin lógica propia.
 * Se llega aquí desde el menú de [MainActivity] (opción "Acerca de").
 */
class Aboutpage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutpage)
        // Necesario desde targetSdk 36: sin esto el contenido se dibuja
        // detrás de la barra de estado (ver InsetsExt.kt).
        findViewById<View>(android.R.id.content).applySystemBarInsetsAsPadding()
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))
        // Sin esto el Toolbar no ofrece salida: sólo se podía volver con el
        // gesto/botón "atrás" del sistema.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // La flecha del Toolbar cierra esta pantalla y devuelve a [MainActivity]
    // tal y como estaba (sin recrearla, para no perder el marcador).
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
