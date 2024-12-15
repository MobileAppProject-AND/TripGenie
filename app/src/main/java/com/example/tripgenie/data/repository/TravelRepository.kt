package com.example.tripgenie.data.repository

import android.util.Log
import com.example.tripgenie.data.model.Travel
import com.example.tripgenie.data.model.User
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

class TravelRepository {
    // ------------------ CREATE ------------------

    // gemini API를 이용한 여행 정보 생성 요청
    suspend fun requestSearch(
        departure: String,
        destination: String,
        startDate: String,
        endDate: String,
        user: User,
        topK: Int,
        generativeModel: GenerativeModel
    ): List<Travel> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = generateTravelRecommendationPrompt(
                    departure,
                    destination,
                    startDate,
                    endDate,
                    user,
                    topK,
                )
                val response = generativeModel.generateContent(prompt)

                // 응답 텍스트 확인
                val responseText = response.text ?: throw Exception("Empty response from Gemini")
                Log.d("Response", responseText)

                // Travel 리스트 생성
                parseTravelResponse(responseText)
            } catch (e: Exception) {
                Log.e("TravelRepository", "Error in requestSearch: ${e.message}")
                emptyList() // 실패 시 빈 리스트 반환
            }
        }
    }

    private fun generateTravelRecommendationPrompt(
        departure: String,
        destination: String,
        startDate: String,
        endDate: String,
        user: User,
        topK: Int
    ): String {
        val basicInfo = user.basicInfo
        val travelPreferences = user.travelPreferences

        return """
        You are an expert travel planner. Based on the following user details, recommend up to $topK travel destinations. 
        Please ensure the recommendations are diverse and cater to the user's preferences. For example, if the user prefers beaches and adventure, prioritize destinations known for these experiences.
        price is in Korean Won (KRW).
        
        Each recommendation should match the exact JSON format provided below and return **only the JSON array** without additional text. 
        Do not include any disclaimers or explanations.

        Travel Details:
        - Departure: $departure
        - Destination: $destination
        - Start Date: $startDate
        - End Date: $endDate
        
        User Details:
        - Age: ${basicInfo.age}
        - Gender: ${basicInfo.gender}
        - Group Size: ${basicInfo.groupSize}

        Travel Preferences:
        - Purpose: ${travelPreferences.travelPurpose}
        - Preferred Environment: ${travelPreferences.preferredEnvironment}
        - Preferred Activities: ${travelPreferences.preferredActivities}
        - Hobbies: ${travelPreferences.hobbies}

        Output Format:
        [
            {
                "country": "string",
                "city": "string",
                "rating": float,
                "description": "string",
                "imageUrl": "string",
                "activities": ["string", "string", ...],
                "minDate": "yyyy-MM-dd",
                "maxDate": "yyyy-MM-dd",
                "price": int
            },
            ...
        ]
    """.trimIndent()
    }

    private fun parseTravelResponse(responseText: String): List<Travel> {
        val travelList = mutableListOf<Travel>()

        try {
            // JSON 배열 부분만 추출
            val jsonStart = responseText.indexOf("[")
            val jsonEnd = responseText.lastIndexOf("]") + 1

            if (jsonStart == -1 || jsonEnd == 0) {
                throw Exception("No valid JSON array found in response")
            }

            val jsonArrayString = responseText.substring(jsonStart, jsonEnd)
            val jsonArray = JSONArray(jsonArrayString)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                // JSON 데이터를 Travel 객체로 변환
                val travel = Travel(
                    country = jsonObject.optString("country", ""),
                    city = jsonObject.optString("city", ""),
                    rating = jsonObject.optDouble("rating", 0.0).toFloat(),
                    description = jsonObject.optString("description", ""),
                    imageUrl = jsonObject.optString("imageUrl", ""),
                    activities = parseActivities(jsonObject.optJSONArray("activities")),
                    minDate = jsonObject.optString("minDate", ""),
                    maxDate = jsonObject.optString("maxDate", ""),
                    price = jsonObject.optInt("price", 0)
                )
                travelList.add(travel)
            }
        } catch (e: Exception) {
            Log.e("TravelRepository", "Error parsing response: ${e.message}")
        }

        return travelList
    }

    private fun parseActivities(jsonArray: JSONArray?): List<String> {
        val activities = mutableListOf<String>()
        jsonArray?.let {
            for (i in 0 until it.length()) {
                activities.add(it.optString(i, ""))
            }
        }
        return activities
    }
}