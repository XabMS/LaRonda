package com.lostandfondue.ronda

import android.content.Context
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.test.core.app.ApplicationProvider
import com.google.android.material.textfield.TextInputEditText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.shadows.ShadowDialog

/**
 * [CambiarNombres]: precarga de los nombres actuales, guardado al aceptar y el
 * botón "Restablecer nombres".
 */
@RunWith(RobolectricTestRunner::class)
class CambiarNombresTest {

    private lateinit var context: Context

    @Before
    fun limpiarPrefs() {
        context = ApplicationProvider.getApplicationContext()
        context.prefsAjustes().edit().clear().commit()
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
        context.guardarNombresEquipos("Tigres", "Panteras")
        val c = lanzar()
        assertEquals("Tigres", c.campo1().text.toString())
        assertEquals("Panteras", c.campo2().text.toString())
    }

    @Test
    fun `aceptar guarda los nombres y cierra la pantalla`() {
        val c = lanzar()
        c.campo1().setText("Tigres")
        c.campo2().setText("Panteras")
        c.get().findViewById<Button>(R.id.BotonAceptarNombres).performClick()

        assertEquals("Tigres" to "Panteras", context.leerNombresEquipos())
        assertTrue(c.get().isFinishing)
    }

    @Test
    fun `restablecer devuelve los campos a los nombres por defecto`() {
        context.guardarNombresEquipos("Tigres", "Panteras")
        val c = lanzar()
        c.get().findViewById<Button>(R.id.BotonRestablecerNombres).performClick()

        assertEquals("Equipo 1", c.campo1().text.toString())
        assertEquals("Equipo 2", c.campo2().text.toString())
        // Restablecer sólo toca los campos: hasta aceptar, lo guardado no cambia.
        assertEquals("Tigres" to "Panteras", context.leerNombresEquipos())
    }

    @Test
    fun `restablecer y aceptar deja los equipos sin nombre guardado`() {
        context.guardarNombresEquipos("Tigres", "Panteras")
        val c = lanzar()
        c.get().findViewById<Button>(R.id.BotonRestablecerNombres).performClick()
        c.get().findViewById<Button>(R.id.BotonAceptarNombres).performClick()

        assertEquals("Equipo 1" to "Equipo 2", context.leerNombresEquipos())
        // Restablecer borra la preferencia; no guarda el literal "Equipo 1".
        val prefs = context.prefsAjustes()
        assertFalse(prefs.contains(CLAVE_NOMBRE_EQUIPO_1))
        assertFalse(prefs.contains(CLAVE_NOMBRE_EQUIPO_2))
    }

    @Test
    fun `abrir y aceptar sin tocar nada no guarda los nombres por defecto`() {
        val c = lanzar()
        c.get().findViewById<Button>(R.id.BotonAceptarNombres).performClick()

        val prefs = context.prefsAjustes()
        assertFalse(prefs.contains(CLAVE_NOMBRE_EQUIPO_1))
        assertFalse(prefs.contains(CLAVE_NOMBRE_EQUIPO_2))
    }

    @Test
    fun `la flecha atras cierra sin preguntar si no se ha tocado nada`() {
        val c = lanzar()
        c.get().onSupportNavigateUp()

        assertNull(ShadowDialog.getLatestDialog())
        assertTrue(c.get().isFinishing)
    }

    @Test
    fun `la flecha atras pregunta si hay nombres sin aceptar`() {
        val c = lanzar()
        c.campo1().setText("Tigres")
        c.get().onSupportNavigateUp()

        val dialogo = ShadowDialog.getLatestDialog()
        assertTrue(dialogo is AlertDialog && dialogo.isShowing)
        // Hasta contestar, la pantalla sigue ahí con lo escrito.
        assertFalse(c.get().isFinishing)
    }

    @Test
    fun `salir descarta lo escrito y no lo guarda`() {
        val c = lanzar()
        c.campo1().setText("Tigres")
        c.get().onSupportNavigateUp()
        (ShadowDialog.getLatestDialog() as AlertDialog)
            .getButton(AlertDialog.BUTTON_POSITIVE).performClick()
        shadowOf(Looper.getMainLooper()).idle()

        assertTrue(c.get().isFinishing)
        assertFalse(context.prefsAjustes().contains(CLAVE_NOMBRE_EQUIPO_1))
    }

    @Test
    fun `seguir editando no cierra la pantalla`() {
        val c = lanzar()
        c.campo1().setText("Tigres")
        c.get().onSupportNavigateUp()
        (ShadowDialog.getLatestDialog() as AlertDialog)
            .getButton(AlertDialog.BUTTON_NEGATIVE).performClick()
        shadowOf(Looper.getMainLooper()).idle()

        assertFalse(c.get().isFinishing)
        assertEquals("Tigres", c.campo1().text.toString())
    }

    @Test
    fun `vaciar un campo que ya estaba por defecto no cuenta como cambio`() {
        val c = lanzar()
        c.campo1().setText("")
        c.get().onSupportNavigateUp()

        assertNull(ShadowDialog.getLatestDialog())
        assertTrue(c.get().isFinishing)
    }

    @Test
    fun `el campo no admite mas de nueve caracteres`() {
        val c = lanzar()
        c.campo1().setText("123456789XXXXX")
        assertEquals(9, c.campo1().text!!.length)
    }
}
