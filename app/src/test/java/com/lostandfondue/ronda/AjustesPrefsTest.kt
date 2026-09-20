package com.lostandfondue.ronda

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Round-trip de los ajustes persistentes (SharedPreferences) de [Ajustes.kt]:
 * los nombres de los equipos se guardan, se releen y caen al valor por defecto
 * cuando faltan. Necesita Robolectric por el [Context].
 */
@RunWith(RobolectricTestRunner::class)
class AjustesPrefsTest {

    private lateinit var context: Context

    @Before
    fun limpiarPrefs() {
        context = ApplicationProvider.getApplicationContext()
        context.prefsAjustes().edit().clear().commit()
    }

    @Test
    fun `sin nada guardado devuelve los nombres por defecto`() {
        assertEquals("Equipo 1" to "Equipo 2", context.leerNombresEquipos())
    }

    @Test
    fun `guardar y releer devuelve los nombres personalizados`() {
        context.guardarNombresEquipos("Tigres", "Panteras")
        assertEquals("Tigres" to "Panteras", context.leerNombresEquipos())
    }

    @Test
    fun `un nombre en blanco cae al valor por defecto solo en ese equipo`() {
        context.guardarNombresEquipos("Tigres", "   ")
        assertEquals("Tigres" to "Equipo 2", context.leerNombresEquipos())
    }

    @Test
    fun `los nombres se guardan recortados`() {
        context.guardarNombresEquipos("  Tigres  ", "Panteras")
        assertEquals("Tigres" to "Panteras", context.leerNombresEquipos())
    }

    @Test
    fun `guardar el nombre por defecto borra la preferencia`() {
        context.guardarNombresEquipos("Tigres", "Panteras")
        context.guardarNombresEquipos("Equipo 1", "Equipo 2")

        // Ni el literal "Equipo 1" ni el "Equipo 2" llegan a disco: el equipo
        // queda "sin nombre puesto" y el valor por defecto se resuelve al leer.
        val prefs = context.prefsAjustes()
        assertFalse(prefs.contains(CLAVE_NOMBRE_EQUIPO_1))
        assertFalse(prefs.contains(CLAVE_NOMBRE_EQUIPO_2))
        assertEquals("Equipo 1" to "Equipo 2", context.leerNombresEquipos())
    }

    @Test
    fun `un nombre en blanco borra la preferencia anterior`() {
        context.guardarNombresEquipos("Tigres", "Panteras")
        context.guardarNombresEquipos("   ", "Panteras")

        val prefs = context.prefsAjustes()
        assertFalse(prefs.contains(CLAVE_NOMBRE_EQUIPO_1))
        assertEquals("Equipo 1" to "Panteras", context.leerNombresEquipos())
    }

    @Test
    fun `guardar sobrescribe lo anterior`() {
        context.guardarNombresEquipos("Uno", "Dos")
        context.guardarNombresEquipos("Tres", "Cuatro")
        assertEquals("Tres" to "Cuatro", context.leerNombresEquipos())
    }
}
