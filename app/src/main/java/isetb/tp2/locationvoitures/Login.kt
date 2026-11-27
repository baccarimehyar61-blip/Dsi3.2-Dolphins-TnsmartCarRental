
package isetb.tp2.locationvoitures

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Login : AppCompatActivity() {

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
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        tvRegister.setOnClickListener {
            startActivity(Intent(this, CreerCompte::class.java))
        }

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(
                    this,
                    "Remplissez tous les champs",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()
                || !email.lowercase().endsWith(".com")
            ) {

                Toast.makeText(
                    this,
                    "Email invalide (exemple : nom@gmail.com)",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                verifierUtilisateur(email, password)
            }
        }
    }


    private fun verifierUtilisateur(email: String, password: String) {

        val where = JSONObject()
            .put("email", email)
            .put("motDePasse", password)

        val filter = JSONObject()
            .put("where", where)
            .toString()

        api.getUtilisateurs(filter).enqueue(object :
            Callback<List<utilisateur>> {

            override fun onResponse(
                call: Call<List<utilisateur>>,
                response: Response<List<utilisateur>>
            ) {

                val liste = response.body()

                if (response.isSuccessful && !liste.isNullOrEmpty()) {

                    val u = liste[0]
                    val nomAffiche = u.prenom ?: u.email.substringBefore("@")

                    val intent = Intent(
                        this@Login,
                        Clientmain::class.java
                    )

                    intent.putExtra("clientId", u.id ?: -1)
                    intent.putExtra("clientNom", nomAffiche)

                    startActivity(intent)
                    finish()

                } else if (response.isSuccessful) {


                    verifierAgence(email, password)

                } else {

                    Toast.makeText(
                        this@Login,
                        "Erreur : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<List<utilisateur>>,
                t: Throwable
            ) {

                Toast.makeText(
                    this@Login,
                    "Erreur de connexion : ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun verifierAgence(email: String, password: String) {

        val where = JSONObject()
            .put("email", email)
            .put("motDePasse", password)

        val filter = JSONObject()
            .put("where", where)
            .toString()

        api.getAgences(filter).enqueue(object :
            Callback<List<agence>> {

            override fun onResponse(
                call: Call<List<agence>>,
                response: Response<List<agence>>
            ) {

                val liste = response.body()

                if (response.isSuccessful && !liste.isNullOrEmpty()) {

                    val a = liste[0]


                    val intent = Intent(
                        this@Login,
                        Agencemain::class.java
                    )

                    intent.putExtra("agenceId", a.id ?: -1)

                    startActivity(intent)
                    finish()

                } else if (response.isSuccessful) {

                    Toast.makeText(
                        this@Login,
                        "Email ou mot de passe incorrect",
                        Toast.LENGTH_LONG
                    ).show()

                } else {

                    Toast.makeText(
                        this@Login,
                        "Erreur : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<List<agence>>,
                t: Throwable
            ) {

                Toast.makeText(
                    this@Login,
                    "Erreur de connexion : ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
