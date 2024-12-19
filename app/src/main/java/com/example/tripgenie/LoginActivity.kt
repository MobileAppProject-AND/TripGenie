package com.example.tripgenie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tripgenie.data.repository.UserRepository
import com.example.tripgenie.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase Auth 초기화
        auth = FirebaseAuth.getInstance()

        // Google Sign In 초기화
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 로그인 버튼 클릭 리스너
        binding.signInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                val errorMessage = when (e.statusCode) {
                    GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "로그인이 취소되었습니다."
                    GoogleSignInStatusCodes.SIGN_IN_FAILED -> "로그인에 실패했습니다."
                    GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> "로그인이 이미 진행중입니다."
                    GoogleSignInStatusCodes.INVALID_ACCOUNT -> "유효하지 않은 계정입니다."
                    GoogleSignInStatusCodes.SIGN_IN_REQUIRED -> "로그인이 필요합니다."
                    else -> "알 수 없는 에러가 발생했습니다. 에러 코드: ${e.statusCode}"
                }

                Log.e("LoginActivity", "Google sign in failed", e)
                Log.e("LoginActivity", "Error code: ${e.statusCode}")

                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        handleUserData(firebaseUser)
                    }
                } else {
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun handleUserData(firebaseUser: FirebaseUser) {
        userRepository.getUser(firebaseUser.uid)
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    // 새로운 사용자 생성
                    val newUser = userRepository.createNewUser(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        displayName = firebaseUser.displayName ?: "-"
                    )

                    userRepository.saveUser(newUser)
                        .addOnSuccessListener {
                            Log.d("LoginActivity", "User data saved successfully")
                            navigateToMainActivity()
                        }
                        .addOnFailureListener { e ->
                            Log.e("LoginActivity", "Error saving user data", e)
                            Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT)
                                .show()
                        }
                } else {
                    // 기존 사용자는 바로 메인으로 이동
                    navigateToMainActivity()
                }
            }
            .addOnFailureListener { e ->
                Log.d("LoginActivity", "Auth User: ${auth.currentUser?.uid}")
                Log.e("LoginActivity", "Error checking user existence", e)
                Toast.makeText(this, "Failed to check user data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}