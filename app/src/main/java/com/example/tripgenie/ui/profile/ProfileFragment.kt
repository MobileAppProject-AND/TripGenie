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
import com.example.tripgenie.data.model.User
import com.example.tripgenie.data.repository.UserRepository
import com.example.tripgenie.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private val userRepository = UserRepository()
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        // User 정보 초기화
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return binding.root
        userRepository.getUser(userId)
            .addOnSuccessListener { document ->
                user = document.toObject(User::class.java) ?: return@addOnSuccessListener
                // 사용자 정보 표시
                binding.name.text = user.basicInfo.name
                binding.email.text = user.email
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "쿼리를 위한 유저 정보 조회 실패", Toast.LENGTH_SHORT).show()
                user = User()
            }

        // ✅프로필 정보
        // 프로필 저장 버튼 리스너
        binding.btnEditProfile.setOnClickListener {
            updateProfile()
        }
        setupGenderSpinner()
        setupGroupSizeSpinner()


        // ✅여행 스타일
        // 여행 스타일 저장 버튼 리스너
        binding.btnEditTravelStyle.setOnClickListener {
            updateTripStyle()
        }
        setupTravelPurposeSpinner()
        setupPreferredEnvironmentSpinner()
        setupPreferredActivitiesSpinner()
        setupHobbiesSpinner()

        return binding.root
    }

    // ✅프로필 정보
    // 프로필 업데이트
    private fun updateProfile() {
        val userId = auth.currentUser?.uid ?: return

        val basicInfo = BasicInfo().apply {
            name = user.basicInfo?.name ?: "unknown"
            age = user.basicInfo?.age ?: 0
            gender = user.basicInfo?.gender ?: Gender.UNDISCLOSED
            groupSize = 1
        }
        userRepository.updateUserBasicInfo(userId, basicInfo)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "프로필이 수정되었습니다!🥳", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "업데이트 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupGenderSpinner() { }
    private fun setupGroupSizeSpinner() { }



    // TODO: UserRepository() 클래스를 사용하여 사용자 정보를 가져와서 화면에 표시 및 업데이트 @박보경


    // 여행 스타일 업데이트
    private fun updateTripStyle() {
        val userId = auth.currentUser?.uid ?: return
        val travelPreferences = TravelPreferences().apply {
            travelPurpose = TravelPurpose.LEISURE
            preferredEnvironment = PreferredEnvironment.BEACH
            preferredActivities = ActivityType.SIGHTSEEING
            hobbies = Hobby.PHOTOGRAPHY
        }

        userRepository.updateUserTravelPreferences(userId, travelPreferences)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "여행 스타일이 수정되었습니다!🥳", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "업데이트 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    // 여행 목적 스피너
    private fun setupTravelPurposeSpinner() { }
    // 선호하는 여행 환경 스피너
    private fun setupPreferredEnvironmentSpinner() { }
    // 선호하는 활동 스피너
    private fun setupPreferredActivitiesSpinner() { }
    // 취미 스피너
    private fun setupHobbiesSpinner() { }

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