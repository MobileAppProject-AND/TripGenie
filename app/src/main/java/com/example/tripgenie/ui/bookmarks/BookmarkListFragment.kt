package com.example.tripgenie.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tripgenie.R
import com.example.tripgenie.data.model.Travel
import com.example.tripgenie.data.repository.UserRepository
import com.example.tripgenie.databinding.FragmentBookmarkListBinding
import com.google.firebase.auth.FirebaseAuth

class BookmarkListFragment : Fragment() {
    private lateinit var binding: FragmentBookmarkListBinding
    private val userRepository = UserRepository()

    private lateinit var travelAdapter: TravelAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkListBinding.inflate(inflater, container, false)

        setupRecyclerView()
        fetchBookmarks()

        return binding.root
    }

    private fun setupRecyclerView() {
        travelAdapter = TravelAdapter(emptyList()) { travel ->
            goToTravelDetailFragment(travel)
        }
        binding.bookmarkRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.bookmarkRecyclerView.adapter = travelAdapter
    }

    private fun fetchBookmarks() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        userRepository.getUserBookmarks(userId)
            .addOnSuccessListener { bookmarkList ->
                if (bookmarkList.isEmpty()) {
                    Toast.makeText(requireContext(), "북마크가 없습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    travelAdapter.updateData(bookmarkList)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "북마크를 가져오지 못했습니다: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun goToTravelDetailFragment(travel: Travel) {
        val fragment = BookmarkDetailFragment()

        // 여행 정보를 Fragment로 전달
        val args = Bundle()
        args.putParcelable("travel", travel)
        fragment.arguments = args

        // 프래그먼트 전환
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}

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