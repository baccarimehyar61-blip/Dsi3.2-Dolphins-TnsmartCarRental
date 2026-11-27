package isetb.tp2.locationvoitures

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar

class formulaire : AppCompatActivity() {

    private var vehiculeId = -1
    private var clientId = -1
    private var prix = 0.0

    private var reservationId = -1
    private var etat = "EN_ATTENTE"

    private lateinit var dateDebut: EditText
    private lateinit var dateFin: EditText
    private lateinit var etNumeroPermis: EditText
    private lateinit var etAdresse: EditText
    private lateinit var btnValiderReservation: Button

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
        setContentView(R.layout.activity_formulaire)

        dateDebut = findViewById(R.id.dateDebut)
        dateFin = findViewById(R.id.dateFin)
        etNumeroPermis = findViewById(R.id.etNumeroPermis)
        etAdresse = findViewById(R.id.etAdresse)
        btnValiderReservation = findViewById(R.id.btnvRes)

        vehiculeId = intent.getIntExtra("vehiculeId", -1)
        clientId = intent.getIntExtra("clientId", -1)
        reservationId = intent.getIntExtra("reservationId", -1)

        dateDebut.setOnClickListener { choisirDate(dateDebut) }
        dateFin.setOnClickListener { choisirDate(dateFin) }

        if (reservationId != -1) {

            etat = intent.getStringExtra("etat") ?: "EN_ATTENTE"
            prix = intent.getDoubleExtra("prix", 0.0)
            dateDebut.setText(intent.getStringExtra("dateDebut"))
            dateFin.setText(intent.getStringExtra("dateFin"))
            btnValiderReservation.text = "Enregistrer"
            chargerClient()
        } else {

            prix = intent.getIntExtra("prix", 0).toDouble()
        }

        btnValiderReservation.setOnClickListener { valider() }
    }

    private fun chargerClient() {
        api.getClientById(clientId).enqueue(object : Callback<client> {
            override fun onResponse(call: Call<client>, response: Response<client>) {
                response.body()?.let {
                    etNumeroPermis.setText(it.numeroPermis ?: "")
                    etAdresse.setText(it.adresse ?: "")
                }
            }

            override fun onFailure(call: Call<client>, t: Throwable) {
                erreur("Erreur : ${t.message}")
            }
        })
    }

    private fun choisirDate(champ: EditText) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, annee, mois, jour ->
                champ.setText(String.format("%04d-%02d-%02d", annee, mois + 1, jour))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun valider() {
        val debut = dateDebut.text.toString()
        val fin = dateFin.text.toString()
        val numeroPermis = etNumeroPermis.text.toString().trim()
        val adresse = etAdresse.text.toString().trim()

        if (debut.isEmpty() || fin.isEmpty() || numeroPermis.isEmpty() || adresse.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        val c = client(id = clientId, numeroPermis = numeroPermis, adresse = adresse)

        if (reservationId != -1) {

            modifierClient(c) { modifierReservation(debut, fin) }
        } else {

            enregistrerClient(c) { addReservation(debut, fin) }
        }
    }

    private fun enregistrerClient(c: client, suite: () -> Unit) {
        api.getClientById(c.id).enqueue(object : Callback<client> {
            override fun onResponse(call: Call<client>, response: Response<client>) {
                if (response.isSuccessful) {
                    suite()
                } else {
                    api.addClient(c).enqueue(object : Callback<client> {
                        override fun onResponse(call: Call<client>, response: Response<client>) {
                            if (response.isSuccessful) suite()
                            else erreur("Erreur client : ${response.code()}")
                        }

                        override fun onFailure(call: Call<client>, t: Throwable) {
                            erreur("Erreur : ${t.message}")
                        }
                    })
                }
            }

            override fun onFailure(call: Call<client>, t: Throwable) {
                erreur("Erreur : ${t.message}")
            }
        })
    }

    private fun addReservation(debut: String, fin: String) {
        val reservation = Reservation1(
            dateDebut = debut,
            dateFin = fin,
            clientId = clientId,
            vehiculeId = vehiculeId,
            prix = prix
        )

        api.addReservation(reservation).enqueue(object : Callback<Reservation1> {
            override fun onResponse(call: Call<Reservation1>, response: Response<Reservation1>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@formulaire, "Réservation ajoutée", Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(this@formulaire, Reservation::class.java)
                            .putExtra("clientId", clientId)
                    )
                    finish()
                } else {
                    erreur("Erreur : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Reservation1>, t: Throwable) {
                erreur("Erreur : ${t.message}")
            }
        })
    }


    private fun modifierClient(c: client, suite: () -> Unit) {
        api.updateClient(c.id, c).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) suite()
                else erreur("Erreur client : ${response.code()}")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                erreur("Erreur : ${t.message}")
            }
        })
    }

    private fun modifierReservation(debut: String, fin: String) {
        val r = Reservation1(
            id = reservationId,
            dateDebut = debut,
            dateFin = fin,
            EtatRes = etat,
            clientId = clientId,
            vehiculeId = vehiculeId,
            prix = prix
        )

        api.updateReservation(reservationId, r).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@formulaire, "Réservation modifiée", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    erreur("Erreur : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                erreur("Erreur : ${t.message}")
            }
        })
    }

    private fun erreur(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}