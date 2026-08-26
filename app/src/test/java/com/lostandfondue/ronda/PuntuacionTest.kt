package com.lostandfondue.ronda

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Reglas de puntuación de "La Ronda": paso de Malas a Buenas, victoria y
 * marcha atrás del botón "-1". Ver [Puntuacion].
 */
class PuntuacionTest {

    /** Suma [veces] puntos de uno en uno, como pulsar "+1" repetidamente. */
    private fun Puntuacion.sumarDeUnoEnUno(veces: Int) = repeat(veces) { sumar(1) }

    @Test
    fun `empieza a cero en malas`() {
        val p = Puntuacion()
        assertEquals(0, p.puntos)
        assertFalse(p.esBuenas)
        assertFalse(p.haGanado)
    }

    @Test
    fun `once puntos siguen siendo malas`() {
        val p = Puntuacion()
        p.sumarDeUnoEnUno(MALAS_PARA_BUENAS)
        assertEquals(MALAS_PARA_BUENAS, p.puntos)
        assertFalse(p.esBuenas)
    }

    @Test
    fun `al pasar de once se cambia a buenas arrastrando el sobrante`() {
        val p = Puntuacion()
        p.sumarDeUnoEnUno(MALAS_PARA_BUENAS)
        p.sumar(1)
        assertTrue(p.esBuenas)
        assertEquals(1, p.puntos)
    }

    @Test
    fun `una jugada grande arrastra todo el sobrante a buenas`() {
        val p = Puntuacion()
        p.sumarDeUnoEnUno(MALAS_PARA_BUENAS)
        p.sumar(4) // Caracol: 11 malas + 4 -> 15, que son 4 buenas
        assertTrue(p.esBuenas)
        assertEquals(4, p.puntos)
    }

    @Test
    fun `gana al superar las nueve buenas y avisa una sola vez`() {
        val p = Puntuacion()
        p.sumarDeUnoEnUno(MALAS_PARA_BUENAS + BUENAS_PARA_GANAR) // 9 buenas
        assertEquals(BUENAS_PARA_GANAR, p.puntos)
        assertFalse(p.haGanado)

        assertTrue("la jugada que gana debe avisar", p.sumar(1))
        assertTrue(p.haGanado)
        assertEquals(PUNTUACION_GANADORA, p.puntos)
    }

    @Test
    fun `una vez ganada el marcador no sube ni se vuelve a avisar`() {
        val p = Puntuacion()
        p.sumarDeUnoEnUno(MALAS_PARA_BUENAS + BUENAS_PARA_GANAR + 1)
        assertTrue(p.haGanado)

        assertFalse("no debe volver a avisar de la victoria", p.sumar(4))
        assertEquals(PUNTUACION_GANADORA, p.puntos)
        assertTrue(p.esBuenas)
    }

    @Test
    fun `restar tras ganar deshace la victoria`() {
        val p = Puntuacion()
        p.sumarDeUnoEnUno(MALAS_PARA_BUENAS + BUENAS_PARA_GANAR + 1)
        p.restar()
        assertFalse(p.haGanado)
        assertEquals(BUENAS_PARA_GANAR, p.puntos)
        assertTrue(p.esBuenas)
    }

    @Test
    fun `restar por debajo de una buena vuelve a once malas`() {
        val p = Puntuacion()
        p.sumarDeUnoEnUno(MALAS_PARA_BUENAS + 1) // 1 buena
        p.restar()
        assertFalse(p.esBuenas)
        assertEquals(MALAS_PARA_BUENAS, p.puntos)
    }

    @Test
    fun `restar y volver a sumar es simetrico en el cambio de fase`() {
        val p = Puntuacion()
        p.sumarDeUnoEnUno(MALAS_PARA_BUENAS + 1)
        p.restar()
        p.sumar(1)
        assertTrue(p.esBuenas)
        assertEquals(1, p.puntos)
    }

    @Test
    fun `restar en malas nunca baja de cero`() {
        val p = Puntuacion()
        repeat(3) { p.restar() }
        assertEquals(0, p.puntos)
        assertFalse(p.esBuenas)
    }

    @Test
    fun `reset deja el marcador a cero malas`() {
        val p = Puntuacion()
        p.sumarDeUnoEnUno(MALAS_PARA_BUENAS + 3)
        p.reset()
        assertEquals(0, p.puntos)
        assertFalse(p.esBuenas)
        assertFalse(p.haGanado)
    }

    @Test
    fun `restaurar recupera la partida tal cual`() {
        val p = Puntuacion()
        p.restaurar(puntos = 7, esBuenas = true)
        assertEquals(7, p.puntos)
        assertTrue(p.esBuenas)
        assertFalse(p.haGanado)
    }

    @Test
    fun `restaurar una partida ganada la reconoce como ganada`() {
        val p = Puntuacion()
        p.restaurar(puntos = PUNTUACION_GANADORA, esBuenas = true)
        assertTrue(p.haGanado)
    }
}
