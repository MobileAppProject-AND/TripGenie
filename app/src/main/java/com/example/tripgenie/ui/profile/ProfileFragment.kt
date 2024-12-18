package com.example.tripgenie.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

    private var selectedGender: Gender = Gender.UNDISCLOSED
    private var selectedGroupSize: Int = 1
    private var selectedTravelPurpose: TravelPurpose = TravelPurpose.LEISURE
    private var selectedPreferredEnvironment: PreferredEnvironment = PreferredEnvironment.BEACH
    private var selectedPreferredActivities: ActivityType = ActivityType.SIGHTSEEING
    private var selectedHobbies: Hobby = Hobby.PHOTOGRAPHY

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
                binding.nameEdit.setText(user.basicInfo.name)
                binding.email.text = user.email
                binding.spinnerGender.setSelection(user.basicInfo.gender.ordinal)
                binding.spinnerGroupSize.setSelection(user.basicInfo.groupSize - 1)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "쿼리를 위한 유저 정보 조회 실패", Toast.LENGTH_SHORT).show()
                user = User()
            }

        // ✅프로필 정보
        binding.btnEditProfile.setOnClickListener {
            updateProfile()
        }
        setupGenderSpinner()
        setupGroupSizeSpinner()

        // ✅여행 스타일
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
    private fun updateProfile() {
        val userId = auth.currentUser?.uid ?: return
        val basicInfo = BasicInfo().apply {
            name = binding.nameEdit.text.toString()
            gender = selectedGender
            groupSize = selectedGroupSize
        }
        userRepository.updateUserBasicInfo(userId, basicInfo)
            .addOnSuccessListener {
                user.basicInfo = basicInfo
                Toast.makeText(requireContext(), "프로필이 수정되었습니다!🥳", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "업데이트 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Gender Spinner 설정
    private fun setupGenderSpinner() {
        val genderOptions = Gender.values().map { it.name }.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter
        binding.spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedGender = Gender.values()[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // Group Size Spinner 설정
    private fun setupGroupSizeSpinner() {
        val groupSizeOptions = listOf(1, 2, 3, 4, 5, 6)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, groupSizeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGroupSize.adapter = adapter
        binding.spinnerGroupSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedGroupSize = groupSizeOptions[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // 여행 스타일 업데이트
    private fun updateTripStyle() {
        val userId = auth.currentUser?.uid ?: return
        val travelPreferences = TravelPreferences().apply {
            travelPurpose = selectedTravelPurpose
            preferredEnvironment = selectedPreferredEnvironment
            preferredActivities = selectedPreferredActivities
            hobbies = selectedHobbies
        }
        userRepository.updateUserTravelPreferences(userId, travelPreferences)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "여행 스타일이 수정되었습니다!🥳", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "업데이트 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // 여행 목적 스피너 설정
    private fun setupTravelPurposeSpinner() {
        val travelPurposeOptions = TravelPurpose.values().map { it.name }.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, travelPurposeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTravelPurpose.adapter = adapter
        binding.spinnerTravelPurpose.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedTravelPurpose = TravelPurpose.values()[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // 선호하는 여행 환경 스피너 설정
    private fun setupPreferredEnvironmentSpinner() {
        val preferredEnvironmentOptions = PreferredEnvironment.values().map { it.name }.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, preferredEnvironmentOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPreferredEnvironment.adapter = adapter
        binding.spinnerPreferredEnvironment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedPreferredEnvironment = PreferredEnvironment.values()[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // 선호하는 활동 스피너 설정
    private fun setupPreferredActivitiesSpinner() {
        val preferredActivitiesOptions = ActivityType.values().map { it.name }.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, preferredActivitiesOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPreferredActivity.adapter = adapter
        binding.spinnerPreferredActivity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedPreferredActivities = ActivityType.values()[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // 취미 스피너 설정
    private fun setupHobbiesSpinner() {
        val hobbiesOptions = Hobby.values().map { it.name }.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, hobbiesOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHobby.adapter = adapter
        binding.spinnerHobby.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedHobbies = Hobby.values()[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
