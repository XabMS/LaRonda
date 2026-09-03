package com.lostandfondue.ronda

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Round-trip de los ajustes persistentes (SharedPreferences) de [Ajustes.kt]:
 * los nombres de los equipos se guardan, se releen y caen al valor por defecto
 * cuando faltan. Necesita Robolectric por el [Context].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AjustesPrefsTest {

    private lateinit var context: Context

    @Before
    fun limpiarPrefs() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("ajustes", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun `sin nada guardado devuelve los nombres por defecto`() {
        assertEquals("Equipo 1" to "Equipo 2", context.leerNombresEquipos())
    }

    @Test
    fun `guardar y releer devuelve los nombres personalizados`() {
        context.guardarNombresEquipos("Los Tigres", "Las Panteras")
        assertEquals("Los Tigres" to "Las Panteras", context.leerNombresEquipos())
    }

    @Test
    fun `un nombre en blanco cae al valor por defecto solo en ese equipo`() {
        context.guardarNombresEquipos("Los Tigres", "   ")
        assertEquals("Los Tigres" to "Equipo 2", context.leerNombresEquipos())
    }

    @Test
    fun `los nombres se guardan recortados`() {
        context.guardarNombresEquipos("  Los Tigres  ", "Las Panteras")
        assertEquals("Los Tigres" to "Las Panteras", context.leerNombresEquipos())
    }

    @Test
    fun `guardar sobrescribe lo anterior`() {
        context.guardarNombresEquipos("Uno", "Dos")
        context.guardarNombresEquipos("Tres", "Cuatro")
        assertEquals("Tres" to "Cuatro", context.leerNombresEquipos())
    }
}
