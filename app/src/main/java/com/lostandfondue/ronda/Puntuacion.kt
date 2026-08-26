package com.lostandfondue.ronda

// --- Reglas de puntuación de "La Ronda" ---
// El marcador de cada equipo pasa por dos fases: "Malas" (0-11) y "Buenas" (0-9).
// Al superar las Malas se pasa a Buenas arrastrando el sobrante; al superar las
// Buenas, el equipo gana la partida.

// Puntuación a partir de la cual un equipo pasa de "Malas" a "Buenas".
const val MALAS_PARA_BUENAS = 11

// Puntuación de "Buenas" a partir de la cual el equipo gana la partida.
const val BUENAS_PARA_GANAR = 9

// Puntuación en la que se deja fijado el marcador al ganar (no sigue subiendo).
const val PUNTUACION_GANADORA = 10

/**
 * Puntuación de un equipo: los puntos y la fase (Malas/Buenas) en la que está,
 * con las reglas de cambio de fase y de victoria.
 *
 * Es deliberadamente independiente de Android (no conoce Views ni Context) para
 * poder cubrirla con tests de JVM; de pintarla en pantalla se encarga
 * `Marcador` en [MainActivity].
 */
class Puntuacion {

    var puntos = 0
        private set

    var esBuenas = false
        private set

    /**
     * La partida ya está ganada. Es un dato derivado y no un estado aparte, así
     * no puede quedar descolgado de [puntos] al guardar y restaurar la partida.
     */
    val haGanado: Boolean
        get() = esBuenas && puntos >= PUNTUACION_GANADORA

    /**
     * Suma [incremento] puntos y aplica el cambio de fase o la victoria.
     *
     * @return true sólo en la jugada que gana la partida, para que quien llame
     *   avise una única vez (antes cada pulsación posterior repetía el aviso).
     */
    fun sumar(incremento: Int): Boolean {
        // Partida ya ganada: el marcador no sigue subiendo ni se vuelve a avisar.
        if (haGanado) return false

        puntos += incremento
        if (!esBuenas) {
            // Fase Malas: si nos pasamos de MALAS_PARA_BUENAS, pasamos a Buenas
            // arrastrando el sobrante (p.ej. 12 Malas -> 1 Buena).
            if (puntos > MALAS_PARA_BUENAS) {
                esBuenas = true
                puntos -= MALAS_PARA_BUENAS
            }
        } else if (puntos > BUENAS_PARA_GANAR) {
            // Fase Buenas: al superar BUENAS_PARA_GANAR, el equipo gana y el
            // marcador se queda fijo en PUNTUACION_GANADORA.
            puntos = PUNTUACION_GANADORA
            return true
        }
        return false
    }

    /** Resta 1 punto (botón "-1"), deshaciendo el cambio de fase si hace falta. */
    fun restar() {
        puntos -= 1
        if (puntos < 1 && esBuenas) {
            // Bajamos de 1 en Buenas -> volvemos a Malas, dejando el marcador
            // en el máximo de esa fase (el "paso atrás" del cambio de fase).
            esBuenas = false
            puntos = MALAS_PARA_BUENAS
        } else if (puntos < 1) {
            // Bajamos de 1 en Malas -> no hay fase anterior, se queda en 0.
            puntos = 0
        }
    }

    /** Vuelve a dejar la puntuación a 0 Malas (usado en "Nueva partida"). */
    fun reset() {
        puntos = 0
        esBuenas = false
    }

    /**
     * Fija un estado ya conocido sin pasar por las reglas de fase: se usa al
     * recrearse la Activity (giro de pantalla, cambio a modo oscuro...) para
     * recuperar la partida guardada en el Bundle.
     */
    fun restaurar(puntos: Int, esBuenas: Boolean) {
        this.puntos = puntos
        this.esBuenas = esBuenas
    }
}
