package com.example.tripgenie.ui.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tripgenie.R
import com.example.tripgenie.data.model.Travel
import com.example.tripgenie.data.model.User
import com.example.tripgenie.data.repository.TravelRepository
import com.example.tripgenie.data.repository.UserRepository
import com.example.tripgenie.databinding.FragmentSearchBinding
import com.example.tripgenie.ui.bookmarks.TravelAdapter
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val userRepository = UserRepository()
    private val travelRepository = TravelRepository()
    private lateinit var generativeModel: GenerativeModel
    private lateinit var user: User
    private val topK: Int = 5

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        // GenerativeModel 초기화
        generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = getString(R.string.gemini_api_key)
        )

        // User 정보 초기화
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return binding.root
        userRepository.getUser(userId)
            .addOnSuccessListener { document ->
                user = document.toObject(User::class.java) ?: return@addOnSuccessListener
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "쿼리를 위한 유저 정보 조회 실패", Toast.LENGTH_SHORT).show()
                user = User()
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchButton.setOnClickListener {
            val departure = binding.departureEditText.text.toString()
            val destination = binding.destinationEditText.text.toString()
            val startDate = binding.startDateEditText.text.toString()
            val endDate = binding.endDateEditText.text.toString()

            if (departure.isEmpty() || destination.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(requireContext(), "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // keyboard 숨기기
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

            // focus 제거
            binding.departureEditText.clearFocus()
            binding.destinationEditText.clearFocus()
            binding.startDateEditText.clearFocus()
            binding.endDateEditText.clearFocus()

            // 비동기 검색 실행
            lifecycleScope.launch {
                performSearch(
                    departure,
                    destination,
                    startDate,
                    endDate,
                    user,
                    topK,
                    generativeModel
                )
            }
        }
    }

    private suspend fun performSearch(
        departure: String,
        destination: String,
        startDate: String,
        endDate: String,
        user: User,
        topK: Int,
        generativeModel: GenerativeModel
    ) {
        try {
            withContext(Dispatchers.Main) {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.dimView.visibility = View.VISIBLE
            }

            // TravelRepository에서 검색 결과 가져오기
            val searchedTravels: List<Travel> = travelRepository.requestSearch(
                departure,
                destination,
                startDate,
                endDate,
                user,
                topK,
                generativeModel
            )

            // 결과 처리
            withContext(Dispatchers.Main) {
                // 로딩 종료
                binding.loadingIndicator.visibility = View.GONE
                binding.dimView.visibility = View.GONE

                if (searchedTravels.isEmpty()) {
                    Toast.makeText(requireContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    showSearchResults(searchedTravels)
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                // 에러 발생 시에도 로딩 종료
                binding.loadingIndicator.visibility = View.GONE
                binding.dimView.visibility = View.GONE

                Toast.makeText(
                    requireContext(),
                    "검색 중 오류가 발생했습니다: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showSearchResults(travels: List<Travel>) {
        // RecyclerView 어댑터와 데이터 바인딩
        val travelAdapter = TravelAdapter(travels) { travel ->
            goToSearchResultDetailFragment(travel)
        }
        binding.travelRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.travelRecyclerView.adapter = travelAdapter
    }

    private fun goToSearchResultDetailFragment(travel: Travel) {
        val fragment = SearchResultDetailFragment()

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