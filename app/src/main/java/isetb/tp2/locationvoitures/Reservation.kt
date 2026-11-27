
package isetb.tp2.locationvoitures

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Reservation : AppCompatActivity() {

    private val reservations = mutableListOf<Reservation1>()
    private lateinit var adapter: ReservationAdapter
    private var clientId = -1

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
        setContentView(R.layout.activity_reservation)

        clientId = intent.getIntExtra("clientId", -1)

        val rv = findViewById<RecyclerView>(R.id.rvRes)

        adapter = ReservationAdapter(
            reservations,
            { r -> modifier(r) },
            { r -> supprimerReservation(r) }
        )

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }

    private fun loadReservations() {

        if (clientId == -1) {
            Toast.makeText(
                this,
                "Connectez-vous pour voir vos réservations",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val where = JSONObject().put("clientId", clientId)
        val filter = JSONObject().put("where", where).toString()

        api.getReservationsByClient(filter).enqueue(
            object : Callback<List<Reservation1>> {

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
                            this@Reservation,
                            "Erreur : ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<Reservation1>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@Reservation,
                        "Erreur : ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    Log.e("API", "loadReservations", t)
                }
            }
        )
    }

    private fun supprimerReservation(r: Reservation1) {

        val id = r.id

        if (id == null || id == -1) {
            Toast.makeText(
                this,
                "Identifiant de réservation invalide",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        api.deleteReservation(id).enqueue(
            object : Callback<Void> {

                override fun onResponse(
                    call: Call<Void>,
                    response: Response<Void>
                ) {

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@Reservation,
                            "Réservation supprimée avec succès",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadReservations()

                    } else {

                        Toast.makeText(
                            this@Reservation,
                            "Erreur de suppression : ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<Void>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@Reservation,
                        "Erreur : ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    Log.e("API", "supprimerReservation", t)
                }
            }
        )
    }

    private fun modifier(r: Reservation1) {

        val i = Intent(this, formulaire::class.java)

        i.putExtra("reservationId", r.id ?: -1)
        i.putExtra("vehiculeId", r.vehiculeId)
        i.putExtra("clientId", r.clientId)
        i.putExtra("dateDebut", r.dateDebut)
        i.putExtra("dateFin", r.dateFin)
        i.putExtra("etat", r.EtatRes)
        i.putExtra("prix", r.prix ?: 0.0)

        startActivity(i)
    }

    override fun onResume() {
        super.onResume()
        loadReservations()
    }
}
