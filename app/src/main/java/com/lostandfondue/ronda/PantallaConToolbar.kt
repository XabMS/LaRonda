package com.lostandfondue.ronda

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/**
 * Base de las pantallas secundarias ([Reglas], [Aboutpage], [CambiarNombres]):
 * todas montan un MaterialToolbar como ActionBar con flecha "atrás" y todas
 * tienen que aplicar los insets a mano.
 *
 * Antes cada una repetía el mismo onCreate y el mismo onSupportNavigateUp
 * palabra por palabra, tres copias que había que arreglar por separado (pasó
 * justo eso con el inset del teclado de [CambiarNombres]).
 */
abstract class PantallaConToolbar : AppCompatActivity() {

    /**
     * Llamar desde onCreate, después de setContentView.
     *
     * @param toolbar el MaterialToolbar del layout; pasa a hacer de ActionBar,
     *   con el título que dice android:label en el manifiesto.
     * @param incluirTeclado true en pantallas con campos de texto, para que el
     *   teclado no tape lo que haya abajo (ver [applySystemBarInsetsAsPadding]).
     * @param onImeVisibilityChanged aviso de que el teclado se abre o se cierra.
     */
    protected fun configurarToolbar(
        toolbar: Toolbar,
        incluirTeclado: Boolean = false,
        onImeVisibilityChanged: ((visible: Boolean) -> Unit)? = null,
    ) {
        // Necesario desde targetSdk 36: sin esto el contenido se dibuja
        // detrás de la barra de estado (ver InsetsExt.kt).
        findViewById<View>(android.R.id.content)
            .applySystemBarInsetsAsPadding(incluirTeclado, onImeVisibilityChanged)
        setSupportActionBar(toolbar)
        // Sin esto el Toolbar no ofrece salida: sólo se podría volver con el
        // gesto/botón "atrás" del sistema.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * La flecha del Toolbar cierra esta pantalla y devuelve a [MainActivity]
     * tal y como estaba (sin recrearla, para no perder el marcador).
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
