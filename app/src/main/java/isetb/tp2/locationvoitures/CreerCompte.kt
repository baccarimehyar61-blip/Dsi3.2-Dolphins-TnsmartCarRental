package isetb.tp2.locationvoitures

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CreerCompte : AppCompatActivity() {

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
        setContentView(R.layout.activity_creer_compte)

        val etNom = findViewById<EditText>(R.id.etNom)
        val etPrenom = findViewById<EditText>(R.id.etPrenom)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etTel = findViewById<EditText>(R.id.etTel)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirm)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        btnRegister.setOnClickListener {
            val nom = etNom.text.toString().trim()
            val prenom = etPrenom.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val tel = etTel.text.toString().trim()
            val password = etPassword.text.toString()
            val confirm = etConfirm.text.toString()


            if (nom.isEmpty() || prenom.isEmpty()) {
                Toast.makeText(this, "Nom et prénom obligatoires", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.lowercase().endsWith(".com")) {
                Toast.makeText(this, "Email invalide (exemple : nom@gmail.com)", Toast.LENGTH_SHORT).show()
            } else if (tel.length < 8) {
                Toast.makeText(this, "Téléphone invalide", Toast.LENGTH_SHORT).show()
            }  else if (password != confirm) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
            } else {

                creerCompte(nom, prenom, email, tel, password)
            }
        }

        tvLogin.setOnClickListener { finish() }
    }

    private fun creerCompte(nom: String, prenom: String, email: String, tel: String, motDePasse: String) {

        val u = utilisateur(
            nom = nom,
            prenom = prenom,
            email = email,
            motDePasse = motDePasse,
            telephone = tel
        )

        api.addUtilisateur(u).enqueue(object : Callback<utilisateur> {
            override fun onResponse(call: Call<utilisateur>, response: Response<utilisateur>) {
                val id = response.body()?.id

                if (response.isSuccessful && id != null) {
                    val intent = Intent(this@CreerCompte, Clientmain::class.java)
                    intent.putExtra("clientId", id)
                    intent.putExtra("clientNom", prenom)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@CreerCompte,
                        "Email déjà utilisé ou erreur (${response.code()})",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<utilisateur>, t: Throwable) {
                Toast.makeText(this@CreerCompte, "Erreur : ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun verifierMotDePasse(password: String): String? {
        if (password.length < 6)
            return "Mot de passe : 6 caractères minimum"
        if (!password[0].isUpperCase())
            return "Le mot de passe doit commencer par une majuscule"
        if (!password.any { it.isDigit() })
            return "Le mot de passe doit contenir au moins un chiffre"
        if (!password.any { !it.isLetterOrDigit() })
            return "Le mot de passe doit contenir un caractère spécial"
        return null
    }
}