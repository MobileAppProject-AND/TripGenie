package com.example.tripgenie.data.model

data class Travel(
    val country: String = "", // 국가
    val city: String = "", // 도시
    val rating: Float = 0f, // 평점
    val description: String = "", // 설명
    val imageUrl: String = "", // 이미지 URL
    val activities: List<String> = listOf(), // activity 목록
    val minDate: String = "", // 여행일자 최소
    val maxDate: String = "", // 여행일자 최대
    val price: Int = 0 // 가격
)