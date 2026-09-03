package com.lostandfondue.ronda

import android.content.Context
import androidx.core.content.edit

// --- Ajustes persistentes de la app ---
// A diferencia del marcador (que sólo vive en onSaveInstanceState y se pierde al
// matar el proceso), los nombres de los equipos son un ajuste: se guardan en
// SharedPreferences y sobreviven a cerrar la app. Los edita la pantalla
// "Cambiar nombres" (ver [CambiarNombres]) y los lee [MainActivity] en onResume.

private const val PREFS_AJUSTES = "ajustes"
private const val CLAVE_NOMBRE_EQUIPO_1 = "nombre_equipo_1"
private const val CLAVE_NOMBRE_EQUIPO_2 = "nombre_equipo_2"

// El máximo de 15 caracteres por nombre lo impone el propio campo de texto
// (android:maxLength en activity_cambiar_nombres.xml).

/**
 * Devuelve el nombre guardado o [porDefecto] si no hay ninguno (nulo o en
 * blanco). Es puro (sin Context) para poder cubrir la regla con tests de JVM.
 */
internal fun nombreODefault(guardado: String?, porDefecto: String): String =
    guardado?.trim()?.takeIf { it.isNotEmpty() } ?: porDefecto

/**
 * Nombres actuales de los dos equipos: el personalizado si se ha fijado alguno,
 * o "Equipo 1" / "Equipo 2" por defecto. Nunca devuelve cadenas vacías.
 */
fun Context.leerNombresEquipos(): Pair<String, String> {
    val prefs = getSharedPreferences(PREFS_AJUSTES, Context.MODE_PRIVATE)
    return Pair(
        nombreODefault(prefs.getString(CLAVE_NOMBRE_EQUIPO_1, null), getString(R.string.Equipo_1)),
        nombreODefault(prefs.getString(CLAVE_NOMBRE_EQUIPO_2, null), getString(R.string.Equipo_2)),
    )
}

/**
 * Guarda los nombres de los dos equipos, recortando espacios sobrantes. Si un
 * nombre queda vacío se guarda como cadena vacía y [leerNombresEquipos]
 * devolverá el valor por defecto al leerlo.
 */
fun Context.guardarNombresEquipos(nombre1: String, nombre2: String) {
    getSharedPreferences(PREFS_AJUSTES, Context.MODE_PRIVATE).edit {
        putString(CLAVE_NOMBRE_EQUIPO_1, nombre1.trim())
        putString(CLAVE_NOMBRE_EQUIPO_2, nombre2.trim())
    }
}
