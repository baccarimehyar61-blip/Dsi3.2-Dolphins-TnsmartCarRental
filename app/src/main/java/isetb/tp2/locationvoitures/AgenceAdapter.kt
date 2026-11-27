package isetb.tp2.locationvoitures

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.ViewCompat


class AgenceAdapter(
    private val reservations: List<Reservation1>,
    private val onDecision: (Reservation1, String) -> Unit
) : RecyclerView.Adapter<AgenceAdapter.ResViewHolder>() {

    class ResViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgCar)
        val dateDebut: TextView = v.findViewById(R.id.dateDebut)
        val dateFin: TextView = v.findViewById(R.id.dateFin)
        val btnAccepter: Button = v.findViewById(R.id.btnAccepter)
        val btnRefuser: Button = v.findViewById(R.id.btnRefuser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservation_agence, parent, false)
        return ResViewHolder(v)
    }

    override fun onBindViewHolder(holder: ResViewHolder, position: Int) {
        val r = reservations[position]
        val ctx = holder.itemView.context


        val resId = ctx.resources.getIdentifier("image${r.vehiculeId}", "drawable", ctx.packageName)
        holder.img.setImageResource(if (resId != 0) resId else R.drawable.image1)

        holder.dateDebut.text = "Début : ${r.dateDebut}"
        holder.dateFin.text = "Fin : ${r.dateFin}"

        val gris = ColorStateList.valueOf(Color.parseColor("#BDBDBD"))




        when (r.EtatRes) {
            "ACCEPTEE" -> {
                holder.btnRefuser.isEnabled = false
                ViewCompat.setBackgroundTintList(holder.btnRefuser, gris)
            }
            "REFUSEE" -> {
                holder.btnAccepter.isEnabled = false
                ViewCompat.setBackgroundTintList(holder.btnAccepter, gris)
            }
        }

        holder.btnAccepter.setOnClickListener { onDecision(r, "ACCEPTEE") }
        holder.btnRefuser.setOnClickListener { onDecision(r, "REFUSEE") }
    }

    override fun getItemCount(): Int = reservations.size
}