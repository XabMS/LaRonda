package com.lostandfondue.ronda

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

// --- Ajustes persistentes de la app ---
// A diferencia del marcador (que sólo vive en onSaveInstanceState y se pierde al
// matar el proceso), los nombres de los equipos son un ajuste: se guardan en
// SharedPreferences y sobreviven a cerrar la app. Los edita la pantalla
// "Cambiar nombres" (ver [CambiarNombres]) y los lee [MainActivity] en onResume.

private const val PREFS_AJUSTES = "ajustes"

// `internal` (no `private`) para que los tests usen estas claves en vez de
// repetir el literal: si se renombran, los tests dejan de compilar en vez de
// quedarse mirando un fichero de preferencias distinto y vacío.
internal const val CLAVE_NOMBRE_EQUIPO_1 = "nombre_equipo_1"
internal const val CLAVE_NOMBRE_EQUIPO_2 = "nombre_equipo_2"

/** El fichero de preferencias de la app. `internal` por lo mismo. */
internal fun Context.prefsAjustes(): SharedPreferences =
    getSharedPreferences(PREFS_AJUSTES, Context.MODE_PRIVATE)

// El máximo de 9 caracteres por nombre lo impone el propio campo de texto
// (android:maxLength en activity_cambiar_nombres.xml). Son 9 y no más porque
// es lo que caben en los TextView de 36sp del marcador (activity_main.xml):
// con nombres más largos la pantalla principal los cortaba con puntos
// suspensivos aunque el editor los diera por buenos.

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
    val prefs = prefsAjustes()
    return Pair(
        nombreODefault(prefs.getString(CLAVE_NOMBRE_EQUIPO_1, null), getString(R.string.Equipo_1)),
        nombreODefault(prefs.getString(CLAVE_NOMBRE_EQUIPO_2, null), getString(R.string.Equipo_2)),
    )
}

/**
 * Guarda los nombres de los dos equipos, recortando espacios sobrantes.
 *
 * Un nombre que queda vacío, o que coincide con el valor por defecto (lo que
 * ocurre al pulsar "Restablecer nombres" y aceptar), borra la preferencia en
 * vez de escribirla: así el equipo vuelve al estado "sin nombre puesto" y
 * [leerNombresEquipos] sigue resolviendo el valor por defecto desde
 * strings.xml. Si se guardara el literal "Equipo 1", ese equipo se quedaría
 * clavado en el texto de hoy aunque mañana cambie el recurso (p. ej. al
 * traducir la app).
 */
fun Context.guardarNombresEquipos(nombre1: String, nombre2: String) {
    prefsAjustes().edit {
        guardarNombre(CLAVE_NOMBRE_EQUIPO_1, nombre1, getString(R.string.Equipo_1))
        guardarNombre(CLAVE_NOMBRE_EQUIPO_2, nombre2, getString(R.string.Equipo_2))
    }
}

/**
 * Escribe el nombre, o borra la clave si no aporta nada sobre [porDefecto].
 *
 * Normaliza con [nombreODefault], el mismo criterio que se aplica al leer, para
 * no tener dos ideas distintas de qué es "el nombre" según por dónde se entre.
 */
private fun SharedPreferences.Editor.guardarNombre(
    clave: String,
    nombre: String,
    porDefecto: String,
) {
    val normalizado = nombreODefault(nombre, porDefecto)
    if (normalizado == porDefecto) remove(clave) else putString(clave, normalizado)
}
