package com.lostandfondue.ronda

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.lostandfondue.ronda.databinding.ActivityMainBinding
import java.util.Locale

// --- Reglas de puntuación de "La Ronda" ---
// El marcador de cada equipo pasa por dos fases: "Malas" (0-11) y "Buenas" (0-9).
// Al superar las Malas se pasa a Buenas arrastrando el sobrante; al superar las
// Buenas, el equipo gana la partida.

// Puntuación a partir de la cual un equipo pasa de "Malas" a "Buenas".
private const val MALAS_PARA_BUENAS = 11

// Puntuación de "Buenas" a partir de la cual el equipo gana la partida.
private const val BUENAS_PARA_GANAR = 9

// Puntuación en la que se deja fijado el marcador al ganar (no sigue subiendo).
private const val PUNTUACION_GANADORA = 10

// Puntos que otorga cada combinación del juego (ver botones de cada equipo).
private const val PUNTOS_RONDA = 1
private const val PUNTOS_PARRANDA = 3
private const val PUNTOS_CARACOL = 4
private const val PUNTOS_MAJO = 1
private const val PUNTOS_BIEN_DA = 1

/**
 * Marcador de un equipo: guarda su puntuación y fase (Malas/Buenas), y se
 * encarga de reflejar ese estado en sus propias vistas (el número y el
 * texto "Malas"/"Buenas" bajo él).
 *
 * Se instancia una vez por equipo (ver [MainActivity.equipo1] y
 * [MainActivity.equipo2]) en vez de duplicar la lógica de puntuación dos
 * veces, una por equipo, como estaba antes.
 *
 * @param nombreEquipo nombre usado en el Toast al ganar (p.ej. "EQUIPO 1").
 * @param contadorView TextView grande donde se pinta el número de puntos.
 * @param textoView TextView donde se pinta "Malas" o "Buenas".
 * @param onGana se invoca cuando este equipo alcanza la puntuación ganadora.
 */
private class Marcador(
    private val nombreEquipo: String,
    private val contadorView: TextView,
    private val textoView: TextView,
    private val cardView: MaterialCardView,
    private val onGana: (String) -> Unit,
) {
    private var puntuacion = 0
    private var esBuenas = false

    init {
        // Pinta el estado inicial (0, Malas) en las vistas al crear el marcador.
        actualizarVistas()
    }

    /** Suma [puntos] al marcador y comprueba si toca cambiar de fase o ganar. */
    fun sumar(puntos: Int) {
        puntuacion += puntos
        if (!esBuenas) {
            // Fase Malas: si nos pasamos de MALAS_PARA_BUENAS, pasamos a Buenas
            // arrastrando el sobrante (p.ej. 12 Malas -> 1 Buena).
            if (puntuacion > MALAS_PARA_BUENAS) {
                esBuenas = true
                puntuacion -= MALAS_PARA_BUENAS
            }
        } else if (puntuacion > BUENAS_PARA_GANAR) {
            // Fase Buenas: al superar BUENAS_PARA_GANAR, el equipo gana y el
            // marcador se queda fijo en PUNTUACION_GANADORA.
            puntuacion = PUNTUACION_GANADORA
            onGana(nombreEquipo)
        }
        actualizarVistas()
    }

    /** Resta 1 punto (botón "-1"), deshaciendo el cambio de fase si hace falta. */
    fun restar() {
        puntuacion -= 1
        if (puntuacion < 1 && esBuenas) {
            // Bajamos de 1 en Buenas -> volvemos a Malas, dejando el marcador
            // en el máximo de esa fase (el "paso atrás" del cambio de fase).
            esBuenas = false
            puntuacion = MALAS_PARA_BUENAS
        } else if (puntuacion < 1) {
            // Bajamos de 1 en Malas -> no hay fase anterior, se queda en 0.
            puntuacion = 0
        }
        actualizarVistas()
    }

    /** Vuelve a dejar el marcador a 0 Malas (usado en "Nueva partida"). */
    fun reset() {
        puntuacion = 0
        esBuenas = false
        actualizarVistas()
    }

    /** Refleja puntuacion/esBuenas en las vistas: número, texto y color. */
    private fun actualizarVistas() {
        contadorView.text = String.format(Locale.getDefault(), "%d", puntuacion)
        textoView.text = if (esBuenas) "Buenas" else "Malas"
        val color = if (esBuenas) R.color.buenas else R.color.malas
        contadorView.setTextColor(ContextCompat.getColor(contadorView.context, color))
        // Tinte muy ligero de la caja del marcador según la fase (ver colors.xml).
        val cardTint = if (esBuenas) R.color.cardTintBuenas else R.color.cardTintMalas
        cardView.setCardBackgroundColor(ContextCompat.getColor(cardView.context, cardTint))
    }
}

/**
 * Pantalla principal: marcador de los dos equipos y botones de puntuación.
 * Cada equipo tiene su propio [Marcador] ([equipo1], [equipo2]) que gestiona
 * toda la lógica de puntuación de ese equipo.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var equipo1: Marcador
    private lateinit var equipo2: Marcador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Necesario desde targetSdk 36: sin esto el contenido se dibuja
        // detrás de la barra de estado / la ActionBar (ver InsetsExt.kt).
        binding.root.applySystemBarInsetsAsPadding()
        // El Toolbar de esta pantalla hace de ActionBar (menú incluido);
        // ya no usamos la ActionBar clásica del sistema.
        setSupportActionBar(binding.toolbar)

        // Mismo Toast de victoria para los dos equipos, parametrizado por nombre.
        val onGana: (String) -> Unit = { nombreEquipo ->
            Toast.makeText(this, "$nombreEquipo GANA", Toast.LENGTH_SHORT).show()
        }
        equipo1 = Marcador("EQUIPO 1", binding.Contador1, binding.Texto1, binding.CardMarcador1, onGana)
        equipo2 = Marcador("EQUIPO 2", binding.Contador2, binding.Texto2, binding.CardMarcador2, onGana)

        // Botones del equipo 1: cada uno suma los puntos de su combinación,
        // salvo "-1" que resta y "+1" que ajusta manualmente el marcador.
        binding.BotonRonda1.setOnClickListener { equipo1.sumar(PUNTOS_RONDA) }
        binding.BotonParranda1.setOnClickListener { equipo1.sumar(PUNTOS_PARRANDA) }
        binding.BotonCaracol1.setOnClickListener { equipo1.sumar(PUNTOS_CARACOL) }
        binding.BotonMajo1.setOnClickListener { equipo1.sumar(PUNTOS_MAJO) }
        binding.BotonBienda1.setOnClickListener { equipo1.sumar(PUNTOS_BIEN_DA) }
        binding.BotonResta1.setOnClickListener { equipo1.restar() }
        binding.BotonSuma1.setOnClickListener { equipo1.sumar(1) }

        // Mismos botones para el equipo 2.
        binding.BotonRonda2.setOnClickListener { equipo2.sumar(PUNTOS_RONDA) }
        binding.BotonParranda2.setOnClickListener { equipo2.sumar(PUNTOS_PARRANDA) }
        binding.BotonCaracol2.setOnClickListener { equipo2.sumar(PUNTOS_CARACOL) }
        binding.BotonMajo2.setOnClickListener { equipo2.sumar(PUNTOS_MAJO) }
        binding.BotonBienda2.setOnClickListener { equipo2.sumar(PUNTOS_BIEN_DA) }
        binding.BotonResta2.setOnClickListener { equipo2.restar() }
        binding.BotonSuma2.setOnClickListener { equipo2.sumar(1) }
    }

    // Infla el menú de la ActionBar (Acerca de / Reglas / Nueva partida).
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.topbar, menu)
        return true
    }

    // Gestiona los taps en las opciones del menú de la ActionBar.
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menuAcercaDe -> {
            startActivity(Intent(this, Aboutpage::class.java))
            true
        }
        R.id.menuReglas -> {
            startActivity(Intent(this, Reglas::class.java))
            true
        }
        R.id.menuNuevaPartida -> confirmarNuevaPartida()
        else -> super.onOptionsItemSelected(item)
    }

    /** Pide confirmación antes de reiniciar la partida (botón "Nueva partida"). */
    private fun confirmarNuevaPartida(): Boolean {
        AlertDialog.Builder(this)
            .setTitle("¿Seguro que quieres reiniciar?")
            .setCancelable(false)
            .setPositiveButton("Sí") { _, _ -> nuevaPartida() }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            .show()
        return true
    }

    /** Pone ambos marcadores a 0 y avisa con un Toast. */
    private fun nuevaPartida() {
        equipo1.reset()
        equipo2.reset()
        Toast.makeText(this, "NUEVA PARTIDA", Toast.LENGTH_SHORT).show()
    }
}
