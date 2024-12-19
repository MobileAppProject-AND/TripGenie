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
    private var _binding: FragmentSearchResultDetailBinding? = null
    private val binding get() = _binding!!

    private val userRepository = UserRepository()
    private var isBookmarked = false // 북마크 상태

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultDetailBinding.inflate(inflater, container, false)

        val travel =
            arguments?.getParcelable<Travel>("travel") ?: return binding.root

        // Travel 데이터를 UI에 표시
        displayTravelData(travel)

        // 초기 북마크 상태 확인
        checkBookmarkStatus(travel)

        // 북마크 버튼 이벤트 처리
        binding.bookmarkButton.setOnClickListener {
            handleBookmarkClick(travel)
        }

        // 뒤로 가기
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack() // 이전 화면으로 이동
        }

        return binding.root
    }

    private fun displayTravelData(travel: Travel) {
        binding.apply {
            country.text = travel.country
            city.text = travel.city
            rating.text = travel.rating.toString()
            price.text = travel.price.toString()
            description.text = travel.description
        }
    }

    private fun checkBookmarkStatus(travel: Travel) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        userRepository.getUser(userId).addOnSuccessListener { documentSnapshot ->
            val bookmarkedTravelIds = documentSnapshot.get("bookmarkedTravels") as? List<String>
            isBookmarked = bookmarkedTravelIds?.contains(travel.id) == true

            // 북마크 버튼 텍스트 업데이트
            updateBookmarkButtonUI()
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "북마크 상태를 확인할 수 없습니다: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateBookmarkButtonUI() {
        binding.bookmarkButton.text =
            if (isBookmarked) "북마크 삭제" else "북마크 추가"
    }

    private fun handleBookmarkClick(travel: Travel) {
        if (isBookmarked) {
            removeTravelFromBookmarkList(travel)
        } else {
            addTravelToBookmarkList(travel)
        }
    }

    private fun addTravelToBookmarkList(travel: Travel) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        userRepository.addBookmarkedTravel(userId, travel)
            .addOnSuccessListener {
                isBookmarked = true
                updateBookmarkButtonUI()
                Toast.makeText(requireContext(), "북마크가 추가되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "북마크 추가 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun removeTravelFromBookmarkList(travel: Travel) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        userRepository.removeBookmarkedTravel(userId, travel.id)
            .addOnSuccessListener {
                isBookmarked = false
                updateBookmarkButtonUI()
                Toast.makeText(requireContext(), "북마크가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "북마크 삭제 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
