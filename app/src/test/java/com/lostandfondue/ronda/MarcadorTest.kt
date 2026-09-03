package com.lostandfondue.ronda

import android.content.Context
import android.view.ContextThemeWrapper
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.google.android.material.card.MaterialCardView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * [Marcador] (clase de [MainActivity]): comprueba que el estado de [Puntuacion]
 * se refleja en las vistas (número y texto "Malas"/"Buenas") y que el aviso de
 * victoria salta una única vez. Necesita Robolectric por las Views.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MarcadorTest {

    private lateinit var contador: TextView
    private lateinit var texto: TextView
    private val ganadores = mutableListOf<String>()
    private lateinit var marcador: Marcador

    @Before
    fun setUp() {
        val context: Context = ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(), R.style.AppTheme,
        )
        contador = TextView(context)
        texto = TextView(context)
        val card = MaterialCardView(context)
        ganadores.clear()
        marcador = Marcador("Equipo 1", contador, texto, card) { ganadores.add(it) }
    }

    @Test
    fun `estado inicial es cero malas`() {
        assertEquals("0", contador.text.toString())
        assertEquals("Malas", texto.text.toString())
        assertEquals(0, marcador.puntos)
        assertFalse(marcador.esBuenas)
    }

    @Test
    fun `sumar refleja el numero en la vista`() {
        marcador.sumar(3)
        assertEquals("3", contador.text.toString())
        assertEquals("Malas", texto.text.toString())
    }

    @Test
    fun `al pasar de once se cambia a buenas en la vista`() {
        marcador.sumar(11)
        assertEquals("Malas", texto.text.toString())
        marcador.sumar(1)
        assertEquals("Buenas", texto.text.toString())
        assertEquals("1", contador.text.toString())
    }

    @Test
    fun `avisa una sola vez al ganar y deja el marcador en diez`() {
        marcador.sumar(11) // 11 malas
        marcador.sumar(9)  // 9 buenas
        assertTrue(ganadores.isEmpty())

        marcador.sumar(1)  // gana
        assertEquals(listOf("Equipo 1"), ganadores)

        marcador.sumar(4)  // partida ya ganada: no repite el aviso
        assertEquals(listOf("Equipo 1"), ganadores)
        assertEquals("10", contador.text.toString())
        assertEquals("Buenas", texto.text.toString())
    }

    @Test
    fun `restar deshace el cambio de fase en la vista`() {
        marcador.sumar(12) // 1 buena
        marcador.restar()
        assertEquals("Malas", texto.text.toString())
        assertEquals("11", contador.text.toString())
    }

    @Test
    fun `reset vuelve a cero malas en la vista`() {
        marcador.sumar(12)
        marcador.reset()
        assertEquals("0", contador.text.toString())
        assertEquals("Malas", texto.text.toString())
    }

    @Test
    fun `restaurar repinta el estado dado`() {
        marcador.restaurar(puntos = 7, esBuenas = true)
        assertEquals("7", contador.text.toString())
        assertEquals("Buenas", texto.text.toString())
    }

    @Test
    fun `el nombre del equipo se puede cambiar y se usa al ganar`() {
        marcador.nombreEquipo = "Los Tigres"
        marcador.sumar(11)
        marcador.sumar(9)
        marcador.sumar(1)
        assertEquals(listOf("Los Tigres"), ganadores)
    }
}
