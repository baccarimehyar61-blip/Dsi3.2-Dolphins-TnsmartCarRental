
package isetb.tp2.locationvoitures

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ReservationAdapter(
    private val reservations: List<Reservation1>,
    private val onModifier: (Reservation1) -> Unit,
    private val onSupprimer: (Reservation1) -> Unit
) : RecyclerView.Adapter<ReservationAdapter.ResViewHolder>() {

    class ResViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val img: ImageView = v.findViewById(R.id.imgCar)
        val dateDebut: TextView = v.findViewById(R.id.dateDebut)
        val dateFin: TextView = v.findViewById(R.id.dateFin)
        val etat: TextView = v.findViewById(R.id.EtatRes)
        val cardEtat: MaterialCardView = v.findViewById(R.id.cardEtat)
        val btnModifier: Button = v.findViewById(R.id.btnModifier)
        val btnSupprimer: Button = v.findViewById(R.id.btnSupprimer)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ResViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservation, parent, false)

        return ResViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: ResViewHolder,
        position: Int
    ) {

        val r = reservations[position]
        val ctx = holder.itemView.context


        val resId = ctx.resources.getIdentifier(
            "image${r.vehiculeId}",
            "drawable",
            ctx.packageName
        )

        holder.img.setImageResource(
            if (resId != 0) resId else R.drawable.image1
        )


        holder.dateDebut.text = "Début : ${r.dateDebut}"
        holder.dateFin.text = "Fin : ${r.dateFin}"


        holder.etat.text = r.EtatRes

        when (r.EtatRes) {
            "ACCEPTEE" -> holder.cardEtat.setCardBackgroundColor(
                android.graphics.Color.parseColor("#66BB6A")
            )

            "REFUSEE" -> holder.cardEtat.setCardBackgroundColor(
                android.graphics.Color.parseColor("#E57373")
            )

            else -> holder.cardEtat.setCardBackgroundColor(
                android.graphics.Color.parseColor("#FFB74D")
            )
        }


        holder.btnModifier.setOnClickListener {
            onModifier(r)
        }


        holder.btnSupprimer.setOnClickListener {
            onSupprimer(r)
        }
    }

    override fun getItemCount(): Int {
        return reservations.size
    }
}
