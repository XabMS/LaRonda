package com.lostandfondue.ronda

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.lostandfondue.ronda.databinding.ActivityCambiarNombresBinding

/**
 * Pantalla "Cambiar nombres": dos campos (máx. 15 caracteres) para poner un
 * nombre personalizado a cada equipo y un botón "Aceptar nombres".
 *
 * Se llega aquí desde el menú de [MainActivity]. Al aceptar se guardan los
 * nombres en SharedPreferences (ver [guardarNombresEquipos]) y se cierra la
 * pantalla; [MainActivity] los recoge en su onResume. "Restablecer nombres"
 * devuelve los campos a "Equipo 1" / "Equipo 2" (aún hay que pulsar "Aceptar
 * nombres" para guardarlo); al guardarlos, [guardarNombresEquipos] borra la
 * preferencia en vez de escribir el literal, así que el equipo vuelve a quedar
 * "sin nombre puesto". La flecha "atrás" del Toolbar sale sin guardar
 * (preguntando antes si hay algo escrito sin aceptar),
 * salvo mientras el teclado está abierto: entonces se convierte en una flecha
 * hacia abajo que sólo lo baja (ver [actualizarIconoNavegacion]), igual que el
 * indicador del sistema, para no cerrar la pantalla de un toque despistado.
 */
class CambiarNombres : PantallaConToolbar() {

    private lateinit var binding: ActivityCambiarNombresBinding

    // Lo mantiene al día el listener de insets (única fuente que ve el teclado).
    private var tecladoVisible = false

    // Los nombres tal y como estaban al abrir la pantalla: con qué comparar
    // para saber si hay algo escrito sin aceptar (ver [salirPreguntando]).
    private lateinit var nombresAlEntrar: Pair<String, String>

    // Diálogo abierto, si lo hay (ver [onDestroy]).
    private var dialogo: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCambiarNombresBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // A diferencia de las otras pantallas, ésta necesita el inset del
        // teclado: con edge-to-edge obligatorio la ventana ya no se redimensiona
        // sola y los botones de abajo quedarían tapados.
        configurarToolbar(
            binding.toolbar,
            incluirTeclado = true,
            onImeVisibilityChanged = ::actualizarIconoNavegacion,
        )

        // Precargar los nombres actuales para poder retocar sólo uno.
        val (nombre1, nombre2) = leerNombresEquipos()
        binding.EntradaEquipo1.setText(nombre1)
        binding.EntradaEquipo2.setText(nombre2)
        nombresAlEntrar = nombre1 to nombre2

        // El gesto/botón "atrás" del sistema pasa por el mismo sitio que la
        // flecha del Toolbar: sin esto cerraría la pantalla sin preguntar.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = salirPreguntando()
        })

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

    /**
     * Con el teclado abierto la flecha del Toolbar es una flecha hacia abajo y
     * sólo lo baja; con el teclado cerrado sale de la pantalla (preguntando
     * antes si hay cambios, ver [salirPreguntando]).
     */
    override fun onSupportNavigateUp(): Boolean {
        if (tecladoVisible) {
            WindowCompat.getInsetsController(window, binding.root)
                .hide(WindowInsetsCompat.Type.ime())
        } else {
            salirPreguntando()
        }
        return true
    }

    /**
     * Cierra la pantalla, pero si hay nombres escritos que no se han aceptado
     * pregunta primero: salir es la única forma de perderlos y no hay ninguna
     * pista en pantalla de que haga falta pulsar "Aceptar nombres".
     */
    private fun salirPreguntando() {
        if (nombresEnPantalla() == nombresAlEntrar) {
            finish()
            return
        }
        dialogo?.dismiss()
        dialogo = AlertDialog.Builder(this)
            .setTitle(R.string.cambiar_nombres_descartar_titulo)
            .setPositiveButton(R.string.cambiar_nombres_descartar_si) { _, _ -> finish() }
            .setNegativeButton(R.string.cambiar_nombres_descartar_no) { d, _ -> d.cancel() }
            .show()
    }

    /**
     * Lo que quedaría guardado si se aceptase ahora mismo. Pasa por
     * [nombreODefault] para comparar con el mismo criterio con el que se lee:
     * vaciar un campo no es un cambio si ese equipo ya estaba sin nombre.
     */
    private fun nombresEnPantalla(): Pair<String, String> = Pair(
        nombreODefault(
            binding.EntradaEquipo1.text?.toString(), getString(R.string.Equipo_1),
        ),
        nombreODefault(
            binding.EntradaEquipo2.text?.toString(), getString(R.string.Equipo_2),
        ),
    )

    override fun onDestroy() {
        // Un diálogo abierto sobre una Activity ya destruida provoca un
        // WindowLeaked (p.ej. al girar la pantalla con el diálogo delante).
        dialogo?.dismiss()
        dialogo = null
        super.onDestroy()
    }

    /** Cambia la flecha del Toolbar según esté el teclado abierto o cerrado. */
    private fun actualizarIconoNavegacion(visible: Boolean) {
        tecladoVisible = visible
        val barra = supportActionBar ?: return
        if (visible) {
            barra.setHomeAsUpIndicator(R.drawable.ic_ocultar_teclado)
            barra.setHomeActionContentDescription(R.string.cambiar_nombres_ocultar_teclado)
        } else {
            // null / 0 = "usa lo que trae el tema": la flecha "atrás" y su
            // descripción estándar, sin tener que duplicarlas aquí.
            barra.setHomeAsUpIndicator(null as Drawable?)
            barra.setHomeActionContentDescription(0)
        }
    }
}
