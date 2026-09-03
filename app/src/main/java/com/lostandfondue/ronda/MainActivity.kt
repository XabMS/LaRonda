package com.lostandfondue.ronda

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.lostandfondue.ronda.databinding.ActivityMainBinding
import java.util.Locale

// Puntos que otorga cada combinación del juego (ver botones de cada equipo).
private const val PUNTOS_RONDA = 1
private const val PUNTOS_PARRANDA = 3
private const val PUNTOS_CARACOL = 4
private const val PUNTOS_MAJO = 1
private const val PUNTOS_BIEN_DA = 1

/**
 * Marcador de un equipo: refleja en sus vistas (el número y el texto
 * "Malas"/"Buenas" bajo él) el estado de una [Puntuacion], que es quien lleva
 * las reglas del juego.
 *
 * Se instancia una vez por equipo (ver [MainActivity.equipo1] y
 * [MainActivity.equipo2]) en vez de duplicar la lógica de puntuación dos
 * veces, una por equipo, como estaba antes.
 *
 * `internal` (y no `private`) sólo para poder cubrirlo con tests de Robolectric.
 *
 * @param nombreEquipo nombre usado al avisar de la victoria (p.ej. "Equipo 1");
 *   puede cambiar en caliente desde la pantalla "Cambiar nombres".
 * @param contadorView TextView grande donde se pinta el número de puntos.
 * @param textoView TextView donde se pinta "Malas" o "Buenas".
 * @param onGana se invoca la única jugada en que este equipo gana la partida.
 */
internal class Marcador(
    var nombreEquipo: String,
    private val contadorView: TextView,
    private val textoView: TextView,
    private val cardView: MaterialCardView,
    private val onGana: (String) -> Unit,
) {
    private val puntuacion = Puntuacion()

    val puntos: Int get() = puntuacion.puntos
    val esBuenas: Boolean get() = puntuacion.esBuenas

    init {
        // Pinta el estado inicial (0, Malas) en las vistas al crear el marcador.
        actualizarVistas()
    }

    /** Suma [puntos] al marcador y avisa si con esta jugada gana el equipo. */
    fun sumar(puntos: Int) {
        val gana = puntuacion.sumar(puntos)
        actualizarVistas()
        if (gana) onGana(nombreEquipo)
    }

    /** Resta 1 punto (botón "-1"). */
    fun restar() {
        puntuacion.restar()
        actualizarVistas()
    }

    /** Vuelve a dejar el marcador a 0 Malas (usado en "Nueva partida"). */
    fun reset() {
        puntuacion.reset()
        actualizarVistas()
    }

    /** Repinta el marcador con una partida guardada (ver [MainActivity.onSaveInstanceState]). */
    fun restaurar(puntos: Int, esBuenas: Boolean) {
        puntuacion.restaurar(puntos, esBuenas)
        actualizarVistas()
    }

    /** Refleja el estado de [puntuacion] en las vistas: número, texto y color. */
    private fun actualizarVistas() {
        contadorView.text = String.format(Locale.getDefault(), "%d", puntuacion.puntos)
        textoView.text = textoView.context.getString(
            if (puntuacion.esBuenas) R.string.fase_buenas else R.string.fase_malas
        )
        val color = if (puntuacion.esBuenas) R.color.buenas else R.color.malas
        contadorView.setTextColor(ContextCompat.getColor(contadorView.context, color))
        // Tinte muy ligero de la caja del marcador según la fase (ver colors.xml).
        val cardTint = if (puntuacion.esBuenas) R.color.cardTintBuenas else R.color.cardTintMalas
        cardView.setCardBackgroundColor(ContextCompat.getColor(cardView.context, cardTint))
    }
}

// Claves del Bundle de onSaveInstanceState (marcador de cada equipo).
private const val ESTADO_PUNTOS_1 = "puntos1"
private const val ESTADO_BUENAS_1 = "buenas1"
private const val ESTADO_PUNTOS_2 = "puntos2"
private const val ESTADO_BUENAS_2 = "buenas2"

/**
 * Pantalla principal: marcador de los dos equipos y botones de puntuación.
 * Cada equipo tiene su propio [Marcador] ([equipo1], [equipo2]); las reglas de
 * puntuación viven en [Puntuacion].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var equipo1: Marcador
    private lateinit var equipo2: Marcador

    // Diálogo abierto, si lo hay (ver [mostrar] y [onDestroy]).
    private var dialogo: AlertDialog? = null

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

        // Mismo aviso de victoria para los dos equipos, parametrizado por nombre.
        val onGana: (String) -> Unit = ::mostrarFinPartida
        equipo1 = Marcador(
            getString(R.string.Equipo_1), binding.Contador1, binding.Texto1,
            binding.CardMarcador1, onGana,
        )
        equipo2 = Marcador(
            getString(R.string.Equipo_2), binding.Contador2, binding.Texto2,
            binding.CardMarcador2, onGana,
        )
        // Recupera la partida si la Activity se está recreando (giro de
        // pantalla, cambio de modo claro/oscuro, vuelta tras matar el proceso).
        savedInstanceState?.let(::restaurarPartida)

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

    // Los nombres de los equipos son un ajuste persistente (ver Ajustes.kt): se
    // releen cada vez que la pantalla vuelve al frente para recoger un cambio
    // hecho en "Cambiar nombres" sin recrear la Activity ni tocar el marcador.
    override fun onResume() {
        super.onResume()
        val (nombre1, nombre2) = leerNombresEquipos()
        binding.Equipo1.text = nombre1
        binding.Equipo2.text = nombre2
        equipo1.nombreEquipo = nombre1
        equipo2.nombreEquipo = nombre2
    }

    // Guarda el marcador de los dos equipos: sin esto, girar la pantalla o
    // cambiar a modo oscuro recrea la Activity y la partida vuelve a 0-0.
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ESTADO_PUNTOS_1, equipo1.puntos)
        outState.putBoolean(ESTADO_BUENAS_1, equipo1.esBuenas)
        outState.putInt(ESTADO_PUNTOS_2, equipo2.puntos)
        outState.putBoolean(ESTADO_BUENAS_2, equipo2.esBuenas)
    }

    /** Contrapartida de [onSaveInstanceState]: repinta la partida guardada. */
    private fun restaurarPartida(estado: Bundle) {
        equipo1.restaurar(estado.getInt(ESTADO_PUNTOS_1), estado.getBoolean(ESTADO_BUENAS_1))
        equipo2.restaurar(estado.getInt(ESTADO_PUNTOS_2), estado.getBoolean(ESTADO_BUENAS_2))
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
        R.id.menuCambiarNombres -> {
            startActivity(Intent(this, CambiarNombres::class.java))
            true
        }
        R.id.menuNuevaPartida -> confirmarNuevaPartida()
        else -> super.onOptionsItemSelected(item)
    }

    /** Pide confirmación antes de reiniciar la partida (botón "Nueva partida"). */
    private fun confirmarNuevaPartida(): Boolean {
        mostrar(
            AlertDialog.Builder(this)
                .setTitle(R.string.nueva_partida_titulo)
                .setCancelable(false)
                .setPositiveButton(R.string.nueva_partida_si) { _, _ -> nuevaPartida() }
                .setNegativeButton(R.string.nueva_partida_no) { dialog, _ -> dialog.cancel() }
        )
        return true
    }

    /**
     * Anuncia el equipo ganador y ofrece empezar otra partida. Salta una sola
     * vez, en la jugada que gana (ver [Puntuacion.sumar]); "Seguir" deja el
     * marcador tal cual por si hay que corregir algo a mano.
     */
    private fun mostrarFinPartida(nombreEquipo: String) {
        mostrar(
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.equipo_gana, nombreEquipo))
                .setMessage(R.string.fin_partida_pregunta)
                .setPositiveButton(R.string.menu_nueva_partida) { _, _ -> nuevaPartida() }
                .setNegativeButton(R.string.fin_partida_seguir) { dialog, _ -> dialog.cancel() }
        )
    }

    /** Muestra un diálogo, cerrando antes el anterior si quedaba alguno abierto. */
    private fun mostrar(builder: AlertDialog.Builder) {
        dialogo?.dismiss()
        dialogo = builder.show()
    }

    override fun onDestroy() {
        // Un diálogo abierto sobre una Activity ya destruida provoca un
        // WindowLeaked (p.ej. al girar la pantalla con el diálogo delante).
        dialogo?.dismiss()
        dialogo = null
        super.onDestroy()
    }

    /** Pone ambos marcadores a 0 y avisa con un Toast. */
    private fun nuevaPartida() {
        equipo1.reset()
        equipo2.reset()
        Toast.makeText(this, R.string.nueva_partida_aviso, Toast.LENGTH_SHORT).show()
    }
}
