package isetb.tp2.locationvoitures

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Agencemain : AppCompatActivity() {

    private val reservations = mutableListOf<Reservation1>()
    private lateinit var adapter: AgenceAdapter

    private val BASE_URL = "http://10.0.2.2:3000/"

    private val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agencemain)

        val rv = findViewById<RecyclerView>(R.id.rvResA)
        adapter = AgenceAdapter(reservations) { r, nouvelEtat ->
            changerEtat(r, nouvelEtat)
        }
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }

    private fun loadReservations() {
        api.getReservations().enqueue(object : Callback<List<Reservation1>> {
            override fun onResponse(
                call: Call<List<Reservation1>>,
                response: Response<List<Reservation1>>
            ) {
                if (response.isSuccessful) {
                    reservations.clear()
                    reservations.addAll(response.body() ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        this@Agencemain,
                        "Erreur : ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Reservation1>>, t: Throwable) {
                Toast.makeText(this@Agencemain, "Erreur : ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("API", "loadReservations", t)
            }
        })
    }


    private fun changerEtat(r: Reservation1, nouvelEtat: String) {
        val id = r.id ?: return
        val maj = r.copy(EtatRes = nouvelEtat)

        api.updateReservation(id, maj).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val texte = if (nouvelEtat == "ACCEPTEE") "Réservation acceptée" else "Réservation refusée"
                    Toast.makeText(this@Agencemain, texte, Toast.LENGTH_SHORT).show()
                    loadReservations()
                } else {
                    Toast.makeText(
                        this@Agencemain,
                        "Erreur : ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@Agencemain, "Erreur : ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadReservations()
    }
}