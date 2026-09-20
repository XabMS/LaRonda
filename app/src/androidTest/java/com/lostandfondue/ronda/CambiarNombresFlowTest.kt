package com.lostandfondue.ronda

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Flujo completo de "Cambiar nombres" en un dispositivo/emulador real: desde el
 * menú de la pantalla principal, escribir nombres, aceptarlos y comprobar que
 * aparecen en el marcador. También el botón "Restablecer nombres".
 */
@RunWith(AndroidJUnit4::class)
class CambiarNombresFlowTest {

    private lateinit var scenario: ActivityScenario<MainActivity>

    private fun limpiarPrefs() {
        ApplicationProvider.getApplicationContext<Context>()
            .prefsAjustes()
            .edit().clear().commit()
    }

    @Before
    fun setUp() {
        limpiarPrefs()
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        cerrarPantallasAbiertas()
        scenario.close()
        limpiarPrefs()
    }

    /**
     * [ActivityScenario.close] sólo termina [MainActivity]. Si un assert falla
     * con "Cambiar nombres" delante, esa Activity sobrevive al test con lo que
     * hubiera escrito, y el siguiente `abrirCambiarNombres` busca el menú de la
     * pantalla principal sobre una pantalla que no lo tiene: un fallo se
     * llevaba por delante a los tests siguientes.
     */
    private fun cerrarPantallasAbiertas() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val monitor = ActivityLifecycleMonitorRegistry.getInstance()
            Stage.entries
                .flatMap { monitor.getActivitiesInStage(it) }
                .filter { it !is MainActivity && !it.isFinishing }
                .forEach { it.finish() }
        }
        instrumentation.waitForIdleSync()
    }

    private fun abrirCambiarNombres() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.menu_cambiar_nombres)).perform(click())
    }

    @Test
    fun cambiarNombres_apareceEnLaPantallaPrincipal() {
        abrirCambiarNombres()
        onView(withId(R.id.EntradaEquipo1)).perform(replaceText("Tigres"))
        onView(withId(R.id.EntradaEquipo2)).perform(replaceText("Panteras"))
        closeSoftKeyboard()
        onView(withId(R.id.BotonAceptarNombres)).perform(click())

        onView(withId(R.id.Equipo1)).check(matches(withText("Tigres")))
        onView(withId(R.id.Equipo2)).check(matches(withText("Panteras")))
    }

    @Test
    fun restablecerNombres_vuelveALosPorDefecto() {
        // Primero fija nombres personalizados.
        abrirCambiarNombres()
        onView(withId(R.id.EntradaEquipo1)).perform(replaceText("Tigres"))
        onView(withId(R.id.EntradaEquipo2)).perform(replaceText("Panteras"))
        closeSoftKeyboard()
        onView(withId(R.id.BotonAceptarNombres)).perform(click())

        // Vuelve a entrar, restablece y acepta. El closeSoftKeyboard() no es
        // adorno: los campos se autoenfocan al abrir la pantalla, y con el
        // teclado delante el clic de Espresso cae sobre una vista tapada.
        abrirCambiarNombres()
        closeSoftKeyboard()
        onView(withId(R.id.BotonRestablecerNombres)).perform(click())
        closeSoftKeyboard()
        onView(withId(R.id.BotonAceptarNombres)).perform(click())

        onView(withId(R.id.Equipo1)).check(matches(withText("Equipo 1")))
        onView(withId(R.id.Equipo2)).check(matches(withText("Equipo 2")))
    }
}
