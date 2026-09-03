package com.lostandfondue.ronda

import android.content.Context
import android.widget.Button
import androidx.test.core.app.ApplicationProvider
import com.google.android.material.textfield.TextInputEditText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

/**
 * [CambiarNombres]: precarga de los nombres actuales, guardado al aceptar y el
 * botón "Restablecer nombres".
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CambiarNombresTest {

    private lateinit var context: Context

    @Before
    fun limpiarPrefs() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("ajustes", Context.MODE_PRIVATE).edit().clear().commit()
    }

    private fun lanzar(): ActivityController<CambiarNombres> =
        Robolectric.buildActivity(CambiarNombres::class.java).setup()

    private fun ActivityController<CambiarNombres>.campo1() =
        get().findViewById<TextInputEditText>(R.id.EntradaEquipo1)

    private fun ActivityController<CambiarNombres>.campo2() =
        get().findViewById<TextInputEditText>(R.id.EntradaEquipo2)

    @Test
    fun `precarga los nombres por defecto cuando no hay nada guardado`() {
        val c = lanzar()
        assertEquals("Equipo 1", c.campo1().text.toString())
        assertEquals("Equipo 2", c.campo2().text.toString())
    }

    @Test
    fun `precarga los nombres personalizados ya guardados`() {
        context.guardarNombresEquipos("Los Tigres", "Las Panteras")
        val c = lanzar()
        assertEquals("Los Tigres", c.campo1().text.toString())
        assertEquals("Las Panteras", c.campo2().text.toString())
    }

    @Test
    fun `aceptar guarda los nombres y cierra la pantalla`() {
        val c = lanzar()
        c.campo1().setText("Los Tigres")
        c.campo2().setText("Las Panteras")
        c.get().findViewById<Button>(R.id.BotonAceptarNombres).performClick()

        assertEquals("Los Tigres" to "Las Panteras", context.leerNombresEquipos())
        assertTrue(c.get().isFinishing)
    }

    @Test
    fun `restablecer devuelve los campos a los nombres por defecto`() {
        context.guardarNombresEquipos("Los Tigres", "Las Panteras")
        val c = lanzar()
        c.get().findViewById<Button>(R.id.BotonRestablecerNombres).performClick()

        assertEquals("Equipo 1", c.campo1().text.toString())
        assertEquals("Equipo 2", c.campo2().text.toString())
        // Restablecer sólo toca los campos: hasta aceptar, lo guardado no cambia.
        assertEquals("Los Tigres" to "Las Panteras", context.leerNombresEquipos())
    }

    @Test
    fun `el campo no admite mas de quince caracteres`() {
        val c = lanzar()
        c.campo1().setText("123456789012345XXXXX")
        assertEquals(15, c.campo1().text!!.length)
    }
}
