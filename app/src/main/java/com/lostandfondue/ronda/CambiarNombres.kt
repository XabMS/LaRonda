package com.lostandfondue.ronda

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lostandfondue.ronda.databinding.ActivityCambiarNombresBinding

/**
 * Pantalla "Cambiar nombres": dos campos (máx. 15 caracteres) para poner un
 * nombre personalizado a cada equipo y un botón "Aceptar nombres".
 *
 * Se llega aquí desde el menú de [MainActivity]. Al aceptar se guardan los
 * nombres en SharedPreferences (ver [guardarNombresEquipos]) y se cierra la
 * pantalla; [MainActivity] los recoge en su onResume. "Restablecer nombres"
 * devuelve los campos a "Equipo 1" / "Equipo 2" (aún hay que pulsar "Aceptar
 * nombres" para guardarlo). La flecha "atrás" del Toolbar cierra sin guardar.
 */
class CambiarNombres : AppCompatActivity() {

    private lateinit var binding: ActivityCambiarNombresBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCambiarNombresBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Necesario desde targetSdk 36: sin esto el contenido se dibuja
        // detrás de la barra de estado (ver InsetsExt.kt).
        findViewById<View>(android.R.id.content).applySystemBarInsetsAsPadding()
        setSupportActionBar(binding.toolbar)
        // Sin esto el Toolbar no ofrece salida: sólo se podía volver con el
        // gesto/botón "atrás" del sistema.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Precargar los nombres actuales para poder retocar sólo uno.
        val (nombre1, nombre2) = leerNombresEquipos()
        binding.EntradaEquipo1.setText(nombre1)
        binding.EntradaEquipo2.setText(nombre2)

        binding.BotonRestablecerNombres.setOnClickListener {
            binding.EntradaEquipo1.setText(getString(R.string.Equipo_1))
            binding.EntradaEquipo2.setText(getString(R.string.Equipo_2))
        }

        binding.BotonAceptarNombres.setOnClickListener {
            guardarNombresEquipos(
                binding.EntradaEquipo1.text?.toString().orEmpty(),
                binding.EntradaEquipo2.text?.toString().orEmpty(),
            )
            Toast.makeText(this, R.string.cambiar_nombres_aviso, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // La flecha del Toolbar cierra esta pantalla y devuelve a [MainActivity]
    // tal y como estaba (sin recrearla, para no perder el marcador).
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
