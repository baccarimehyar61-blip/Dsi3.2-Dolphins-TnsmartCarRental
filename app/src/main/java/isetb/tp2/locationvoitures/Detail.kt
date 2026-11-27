package isetb.tp2.locationvoitures

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class Detail : AppCompatActivity() {

    private var vehiculeId: Int = -1
    private var clientId: Int = -1

    private lateinit var image: ImageView
    private lateinit var modele: TextView
    private lateinit var prix: TextView
    private lateinit var description: TextView
    private lateinit var btnReserver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        image = findViewById(R.id.imgDetail)
        modele = findViewById(R.id.idModele)
        prix = findViewById(R.id.prixJour)
        description = findViewById(R.id.idDescription)
        btnReserver = findViewById(R.id.btnRes)

        vehiculeId = intent.getIntExtra("id", -1)
        clientId = intent.getIntExtra("clientId", -1)

        if (vehiculeId == -1) {
            Toast.makeText(this, "Id introuvable", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        modele.text = intent.getStringExtra("nom")
        description.text = intent.getStringExtra("description")
        prix.text = "${intent.getIntExtra("prix", 0)} DT / jour"
        image.setImageResource(intent.getIntExtra("image", 0))

        btnReserver.setOnClickListener {
            if (clientId == -1) {

                Toast.makeText(this, "Connectez-vous pour réserver", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Login::class.java))
            } else {

                val i = Intent(this, formulaire::class.java)
                i.putExtra("vehiculeId", vehiculeId)
                i.putExtra("clientId", clientId)
                i.putExtra("prix", intent.getIntExtra("prix", 0))
                startActivity(i)
            }
        }
    }
}