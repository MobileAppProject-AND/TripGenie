package com.example.tripgenie

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tripgenie.LoginActivity
import com.example.tripgenie.databinding.ActivityMainBinding
import com.example.tripgenie.ui.bookmarks.BookmarkListFragment
import com.example.tripgenie.ui.profile.ProfileFragment
import com.example.tripgenie.ui.search.SearchFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase Auth 초기화
        auth = FirebaseAuth.getInstance()

        // 로그인 상태 체크
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        if (savedInstanceState == null) {
            // 초기 Fragment 설정
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SearchFragment())
                .commit()
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SearchFragment())
                        .commit()
                    true
                }

                R.id.navigation_bookmarks -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, BookmarkListFragment())
                        .commit()
                    true
                }

                R.id.navigation_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }

        binding.bottomNavigation.selectedItemId = R.id.navigation_search
    }
}