package com.example.tripgenie.ui.search

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.tripgenie.data.model.Travel
import com.example.tripgenie.data.repository.UserRepository
import com.example.tripgenie.databinding.FragmentSearchResultDetailBinding
import com.google.firebase.auth.FirebaseAuth

class SearchResultDetailFragment : Fragment() {
    private lateinit var binding: FragmentSearchResultDetailBinding
    private val userRepository = UserRepository()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchResultDetailBinding.inflate(inflater, container, false)

        // BookmarkListFragment 에서 전달받은 Travel 객체
        val travel =
            arguments?.getParcelable<Travel>("travel") ?: return binding.root

        travel.let {
            binding.country.text = it.country
            binding.city.text = it.city
            binding.rating.text = it.rating.toString()
            binding.price.text = it.price.toString()
            binding.description.text = it.description
        }

        // 버튼에 북마크 추가 이벤트 추가
        binding.addBookmarkButton.setOnClickListener {
            addTravelToBookmarkList(travel)
        }

        // 버튼에 북마크 삭제 이벤트 추가
        binding.deleteBookmarkButton.setOnClickListener {
            removeTravelFromBookmarkList(travel)
        }

        return binding.root
    }

    private fun addTravelToBookmarkList(travel: Travel) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return


        userRepository.addBookmarkedTravel(userId, travel)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "북마크가 추가되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "북마크 추가 실패: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun removeTravelFromBookmarkList(travel: Travel) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return


        userRepository.removeBookmarkedTravel(userId, travel.id)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "북마크가 삭제되었습니다..", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "북마크 삭제 실패: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}