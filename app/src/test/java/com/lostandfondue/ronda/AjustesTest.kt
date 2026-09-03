package com.lostandfondue.ronda

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Regla de "nombre guardado o valor por defecto" de [nombreODefault], usada al
 * leer los nombres de los equipos (ver Ajustes.kt).
 */
class AjustesTest {

    @Test
    fun `sin nombre guardado usa el valor por defecto`() {
        assertEquals("Equipo 1", nombreODefault(null, "Equipo 1"))
    }

    @Test
    fun `nombre en blanco usa el valor por defecto`() {
        assertEquals("Equipo 2", nombreODefault("   ", "Equipo 2"))
    }

    @Test
    fun `un nombre guardado se devuelve recortado`() {
        assertEquals("Los Pepes", nombreODefault("  Los Pepes  ", "Equipo 1"))
    }
}
