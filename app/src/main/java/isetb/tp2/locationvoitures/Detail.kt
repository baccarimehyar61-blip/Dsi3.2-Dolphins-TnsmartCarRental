package isetb.tp2.locationvoitures

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class Detail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val nom = intent.getStringExtra("nom")
        val description = intent.getStringExtra("description")
        val image = intent.getIntExtra("image",0)
        val prix = intent.getIntExtra("prix",0)

        findViewById<ImageView>(R.id.imgDetail).setImageResource(image)
        findViewById<TextView>(R.id.idModele).text = nom
        findViewById<TextView>(R.id.prixJour).text = "$prix DT / jour"
        findViewById<TextView>(R.id.idDescription).text = description

        findViewById<Button>(R.id.btnLocation).setOnClickListener {
            Toast.makeText(this,"Voiture ajoutée au panier",Toast.LENGTH_SHORT).show()
        }

    }
}