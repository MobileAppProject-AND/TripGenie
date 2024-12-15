package com.example.tripgenie.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tripgenie.data.model.ActivityType
import com.example.tripgenie.data.model.BasicInfo
import com.example.tripgenie.data.model.Gender
import com.example.tripgenie.data.model.Hobby
import com.example.tripgenie.data.model.PreferredEnvironment
import com.example.tripgenie.data.model.TravelPreferences
import com.example.tripgenie.data.model.TravelPurpose
import com.example.tripgenie.data.repository.UserRepository
import com.example.tripgenie.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private val userRepository = UserRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    // TODO: UserRepository() 클래스를 사용하여 사용자 정보를 가져와서 화면에 표시 및 업데이트 @박보경
    private fun updateUserInfo() {
        val userId = auth.currentUser?.uid ?: return

        // TODO: xml 과 연결 후, 기본 정보 업데이트 @박보경
        val basicInfo = BasicInfo().apply {
            name = "John Doe"
            age = 25
            gender = Gender.MALE
            groupSize = 2
        }

        userRepository.updateUserBasicInfo(userId, basicInfo)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "기본 정보가 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "업데이트 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        // 여행 선호도 업데이트
        val travelPreferences = TravelPreferences().apply {
            travelPurpose = TravelPurpose.LEISURE
            preferredEnvironment = PreferredEnvironment.BEACH
            preferredActivities = ActivityType.SIGHTSEEING
            hobbies = Hobby.PHOTOGRAPHY
        }

        userRepository.updateUserTravelPreferences(userId, travelPreferences)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "여행 선호도가 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "업데이트 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}