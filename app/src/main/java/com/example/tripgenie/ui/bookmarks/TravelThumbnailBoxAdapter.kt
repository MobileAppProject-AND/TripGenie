package com.example.tripgenie.ui.bookmarks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tripgenie.R
import com.example.tripgenie.data.model.Travel

class TravelAdapter(
    private var travelList: List<Travel>,
    private val onDetailClick: (Travel) -> Unit
) : RecyclerView.Adapter<TravelAdapter.TravelViewHolder>() {

    inner class TravelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val travelId: TextView = itemView.findViewById(R.id.travel_id)
        val travelCountry: TextView = itemView.findViewById(R.id.travel_country)
        val travelCity: TextView = itemView.findViewById(R.id.travel_city)
        val detailButton: Button = itemView.findViewById(R.id.go_to_detail_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_travel, parent, false)
        return TravelViewHolder(view)
    }

    override fun onBindViewHolder(holder: TravelViewHolder, position: Int) {
        val travel = travelList[position]
        holder.travelId.text = travel.id
        holder.travelCountry.text = travel.country
        holder.travelCity.text = travel.city

        holder.detailButton.setOnClickListener {
            onDetailClick(travel)
        }
    }

    override fun getItemCount(): Int = travelList.size

    fun updateData(newData: List<Travel>) {
        travelList = newData
        notifyDataSetChanged()
    }
}