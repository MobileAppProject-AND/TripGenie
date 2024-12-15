package com.example.tripgenie.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tripgenie.R
import com.example.tripgenie.data.model.Travel
import com.example.tripgenie.databinding.FragmentBookmarkListBinding

class BookmarkListFragment : Fragment() {
    private lateinit var binding: FragmentBookmarkListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkListBinding.inflate(inflater, container, false)

        // 더미 북마크 상세정보 이동 버튼 바인딩
        binding.goToDetailButton.setOnClickListener {
            goToTravelDetailFragment()
        }

        return binding.root
    }

    private fun goToTravelDetailFragment() {
        val fragment = BookmarkDetailFragment()
        val dummyTravel = Travel(
            country = "South Korea",
            city = "Seoul",
            rating = 4.5f,
            description = "The capital city of South Korea",
            imageUrl = "https://example.com/seoul.jpg",
            activities = listOf("Shopping", "Sightseeing", "Eating"),
            minDate = "2022-01-01",
            maxDate = "2022-12-31",
            price = 1000
        )

        // 여행 정보를 Fragment로 전달
        val args = Bundle()
        args.putParcelable("travel", dummyTravel)
        fragment.arguments = args

        // 프래그먼트 전환
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}