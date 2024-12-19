package com.example.tripgenie.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
            goToBookmarkDetailFragment(travel)
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

    private fun goToBookmarkDetailFragment(travel: Travel) {
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

