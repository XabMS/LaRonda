package com.lostandfondue.ronda

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var contador1 = 0
    var contador2 = 0
    var cont1Str = ""
    var cont2Str = ""
    var texto1 = "Malas"
    var texto2 = "Malas"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val BotonRonda1 = findViewById<Button>(R.id.BotonRonda1) //crear uno para cada boton
        val BotonParranda1 = findViewById<Button>(R.id.BotonParranda1)
        val BotonCaracol1 = findViewById<Button>(R.id.BotonCaracol1)
        val BotonMajo1 = findViewById<Button>(R.id.BotonMajo1)
        val BotonBienda1 = findViewById<Button>(R.id.BotonBienda1)
        val BotonResta1 = findViewById<Button>(R.id.BotonResta1)
        val BotonSuma1 = findViewById<Button>(R.id.BotonSuma1)

        val BotonRonda2 = findViewById<Button>(R.id.BotonRonda2) //crear uno para cada boton
        val BotonParranda2 = findViewById<Button>(R.id.BotonParranda2)
        val BotonCaracol2 = findViewById<Button>(R.id.BotonCaracol2)
        val BotonMajo2 = findViewById<Button>(R.id.BotonMajo2)
        val BotonBienda2 = findViewById<Button>(R.id.BotonBienda2)
        val BotonResta2 = findViewById<Button>(R.id.BotonResta2)
        val BotonSuma2 = findViewById<Button>(R.id.BotonSuma2)

        //funcion que comprueba si vale mas de 11, cambia el texto de buenas/malas y reajusta el contador si hace falta
        fun comparador(contador: Int, texto: String, equipo: Int): Int {
            if (equipo == 1){
                contador1 = contador
                texto1 = texto
                if (texto1 == "Malas") {
                    if (contador > 11) {
                        texto1 = "Buenas"
                        Texto1.text = texto1
                        Contador1.setTextColor(Color.parseColor("#4caf50"))
                        contador1 -= 11
                        return contador1
                    } else {
                        return contador1
                    }

                } else {
                    if (contador > 9) {
                        contador1 = 10
                        Toast.makeText(this@MainActivity, "EQUIPO 1 GANA", Toast.LENGTH_SHORT).show()
                    } else {
                        return contador1
                    }
                }
                return contador1
            }
            else {
                contador2 = contador
                texto2 = texto
                if (texto2 == "Malas") {
                    if (contador > 11) {
                        texto2 = "Buenas"
                        Texto2.text = texto2
                        Contador2.setTextColor(Color.parseColor("#4caf50"))
                        contador2 = contador2 - 11
                        return contador2
                    } else {
                        return contador2
                    }

                } else {
                    if (contador > 9) {
                        contador2 = 10
                        Toast.makeText(this@MainActivity, "EQUIPO 2 GANA", Toast.LENGTH_SHORT).show()
                    } else {
                        return contador2
                    }
                }
                return contador2
            }
        }

        //funcion para sumar x al equipo y
        fun suma_puntuacion(cantidad: Int, equipo: Int){
            if (equipo == 1) {
                contador1 += cantidad
                comparador(contador1, texto1,1)
                cont1Str = contador1.toString()
                Contador1.text = cont1Str
            } else {
                contador2 += cantidad
                comparador(contador2, texto2,2)
                cont2Str = contador2.toString()
                Contador2.text = cont2Str
            }
        }

        //funcion para restar 1 al equipo y
        fun resta_puntuacion(equipo: Int){
            if (equipo == 1) {
                contador1 -= 1
                if (contador1 < 1 && texto1 == "Buenas") {        //pasamos de buenas a malas
                    texto1 = "Malas"
                    Texto1.text = texto1
                    Contador1.setTextColor(Color.parseColor("#d32f2f"))
                    contador1 = 11
                }
                if (contador1 < 1 && texto1 == "Malas") {
                    contador1 = 0
                }
                cont1Str = contador1.toString()
                Contador1.text = cont1Str
            } else {
                contador2 -= 1
                if (contador2 < 1 && texto2 == "Buenas") {        //pasamos de buenas a malas
                    texto2 = "Malas"
                    Texto2.text = texto2
                    Contador2.setTextColor(Color.parseColor("#d32f2f"))
                    contador2 = 11
                }
                if (contador2 < 1 && texto2 == "Malas") {
                    contador2 = 0
                }
                comparador(contador2,texto2,2)
                cont2Str = contador2.toString()
                Contador2.text = cont2Str
            }
        }

        BotonRonda1.setOnClickListener {
            suma_puntuacion(1, 1)}
        BotonParranda1.setOnClickListener {
            suma_puntuacion(3, 1)}
        BotonCaracol1.setOnClickListener {
            suma_puntuacion(4, 1)}
        BotonMajo1.setOnClickListener {
            suma_puntuacion(1, 1)}
        BotonBienda1.setOnClickListener {
            suma_puntuacion(1, 1)}
        BotonResta1.setOnClickListener {
            resta_puntuacion(1)}
        BotonSuma1.setOnClickListener {
            suma_puntuacion(1, 1)}

        // EQUIPO 2

        BotonRonda2.setOnClickListener {
            suma_puntuacion(1, 2)}
        BotonParranda2.setOnClickListener {
            suma_puntuacion(3, 2)}
        BotonCaracol2.setOnClickListener {
            suma_puntuacion(4, 2)}
        BotonMajo2.setOnClickListener {
            suma_puntuacion(1, 2)}
        BotonBienda2.setOnClickListener {
            suma_puntuacion(1, 2)}
        BotonResta2.setOnClickListener {
            resta_puntuacion(2)}
        BotonSuma2.setOnClickListener {
            suma_puntuacion(1, 2)}
    }

    fun reset(): Boolean {
        contador1 = 0
        contador2 = 0
        cont1Str = contador1.toString()
        Contador1.text = cont1Str
        cont2Str = contador2.toString()
        Contador2.text = cont2Str
        texto1 = "Malas"
        texto2 = "Malas"
        Texto1.text = texto1
        Contador1.setTextColor(Color.parseColor("#d32f2f"))
        Texto2.text = texto2
        Contador2.setTextColor(Color.parseColor("#d32f2f"))
        Toast.makeText(this@MainActivity, "NUEVA PARTIDA", Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menuAcercaDe -> {
                val intent = Intent(this, Aboutpage::class.java).apply {                }
                startActivity(intent)
                true
            }
            R.id.menuReglas -> {
                val intent = Intent(this, Reglas::class.java).apply {                }
                startActivity(intent)
                true
            }
            R.id.menuNuevaPartida -> reset()


            else -> super.onOptionsItemSelected(item)
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.topbar, menu)
        return true
    }
}
