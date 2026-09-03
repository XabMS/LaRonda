package com.lostandfondue.ronda

import android.content.Context
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog

/**
 * [MainActivity]: pintado inicial, botones de puntuación, aviso de victoria,
 * navegación del menú y relectura de los nombres de equipo en onResume.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainActivityTest {

    private lateinit var controller: ActivityController<MainActivity>
    private val activity get() = controller.get()

    @Before
    fun setUp() {
        ApplicationProvider.getApplicationContext<Context>()
            .getSharedPreferences("ajustes", Context.MODE_PRIVATE).edit().clear().commit()
        controller = Robolectric.buildActivity(MainActivity::class.java).setup()
    }

    private fun texto(id: Int) = activity.findViewById<TextView>(id).text.toString()
    private fun pulsar(id: Int) = activity.findViewById<Button>(id).performClick()

    @Test
    fun `arranca con los nombres por defecto y el marcador a cero`() {
        assertEquals("Equipo 1", texto(R.id.Equipo1))
        assertEquals("Equipo 2", texto(R.id.Equipo2))
        assertEquals("0", texto(R.id.Contador1))
        assertEquals("0", texto(R.id.Contador2))
    }

    @Test
    fun `el boton de parranda suma tres al equipo uno`() {
        pulsar(R.id.BotonParranda1)
        assertEquals("3", texto(R.id.Contador1))
        assertEquals("0", texto(R.id.Contador2))
    }

    @Test
    fun `el boton menos uno no baja de cero`() {
        pulsar(R.id.BotonResta1)
        assertEquals("0", texto(R.id.Contador1))
    }

    @Test
    fun `al ganar se muestra un dialogo`() {
        repeat(21) { pulsar(R.id.BotonSuma1) } // 11 malas + 9 buenas + 1 = victoria
        assertEquals("10", texto(R.id.Contador1))
        val dialogo = ShadowDialog.getLatestDialog()
        assertTrue(dialogo is AlertDialog && dialogo.isShowing)
    }

    @Test
    fun `nueva partida pone los dos marcadores a cero`() {
        pulsar(R.id.BotonCaracol1)
        pulsar(R.id.BotonParranda2)
        shadowOf(activity).clickMenuItem(R.id.menuNuevaPartida)
        // El diálogo de confirmación: pulsar "Sí". AppCompat despacha el clic
        // del botón por un Handler, así que hay que dejar correr el looper.
        (ShadowDialog.getLatestDialog() as AlertDialog)
            .getButton(AlertDialog.BUTTON_POSITIVE).performClick()
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals("0", texto(R.id.Contador1))
        assertEquals("0", texto(R.id.Contador2))
    }

    @Test
    fun `la opcion Cambiar nombres abre esa pantalla`() {
        shadowOf(activity).clickMenuItem(R.id.menuCambiarNombres)
        val siguiente = shadowOf(activity).nextStartedActivity
        assertEquals(
            CambiarNombres::class.java.name,
            siguiente.component?.className,
        )
    }

    @Test
    fun `onResume recoge los nombres guardados en preferencias`() {
        activity.guardarNombresEquipos("Los Tigres", "Las Panteras")
        controller.pause().resume()
        assertEquals("Los Tigres", texto(R.id.Equipo1))
        assertEquals("Las Panteras", texto(R.id.Equipo2))
    }
}
