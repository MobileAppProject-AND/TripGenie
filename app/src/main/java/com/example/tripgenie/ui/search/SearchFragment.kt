package com.example.tripgenie.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.tripgenie.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // UI 요소 초기화
        searchEditText = binding.searchEditText
        searchButton = binding.searchButton

        // 검색 버튼 클릭 이벤트 처리
        searchButton.setOnClickListener {
            val searchQuery = searchEditText.text.toString()
            performSearch(searchQuery)
        }
    }

    private fun performSearch(query: String) {
        // 검색 로직 구현
        // 검색 결과를 표시하는 Fragment로 전환
    }
}