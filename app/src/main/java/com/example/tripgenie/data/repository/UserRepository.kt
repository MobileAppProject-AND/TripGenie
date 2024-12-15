package com.example.tripgenie.data.repository

import com.example.tripgenie.data.model.BasicInfo
import com.example.tripgenie.data.model.Travel
import com.example.tripgenie.data.model.TravelPreferences
import com.example.tripgenie.data.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val travelsCollection = db.collection("travels")

    // ------------------ CREATE ------------------

    // 사용자 정보 저장
    fun saveUser(user: User): Task<Void> {
        return usersCollection.document(user.uid).set(user)
    }

    fun createNewUser(uid: String, email: String, displayName: String): User {
        return User(
            uid = uid,
            email = email,
            basicInfo = BasicInfo().apply {
                name = displayName
            }
        )
    }

    // 북마크 추가
    fun addBookmarkedTravel(uid: String, travel: Travel): Task<Void> {
        // travels 컬렉션에 travel 정보 추가 후 자동 생성된 ID 가져오기
        return travelsCollection.add(travel)
            .continueWithTask { task ->
                if (task.isSuccessful) {
                    val newTravelId =
                        task.result?.id ?: throw Exception("Failed to get new travel ID")
                    // user 문서의 bookmarkedCities 배열에 ID 추가
                    usersCollection.document(uid)
                        .update("bookmarkedCities", FieldValue.arrayUnion(newTravelId))
                } else {
                    throw task.exception ?: Exception("Failed to add travel to travels collection")
                }
            }
    }

    // ------------------ READ ------------------

    // 사용자 정보 가져오기
    fun getUser(uid: String): Task<DocumentSnapshot> {
        return usersCollection.document(uid).get()
    }

    // 유저의 북마크 목록 가져오기
    fun getUserBookmarks(uid: String): Task<DocumentSnapshot> {
        return usersCollection.document(uid)
            .get()
    }

    // ------------------ UPDATE ------------------

    // 기본 정보 업데이트
    fun updateUserBasicInfo(uid: String, basicInfo: BasicInfo): Task<Void> {
        return usersCollection.document(uid)
            .update("basicInfo", basicInfo)
    }

    // 여행 선호도 업데이트
    fun updateUserTravelPreferences(uid: String, travelPreferences: TravelPreferences): Task<Void> {
        return usersCollection.document(uid)
            .update("travelPreferences", travelPreferences)
    }

    // 여러 정보를 한 번에 업데이트
    fun updateUserInfo(
        uid: String,
        basicInfo: BasicInfo? = null,
        travelPreferences: TravelPreferences? = null
    ): Task<Void> {
        val updates = mutableMapOf<String, Any>()

        basicInfo?.let { updates["basicInfo"] = it }
        travelPreferences?.let { updates["travelPreferences"] = it }

        return if (updates.isNotEmpty()) {
            usersCollection.document(uid).update(updates)
        } else {
            throw IllegalArgumentException("At least one parameter must be non-null")
        }
    }

    // ------------------ DELETE ------------------

    // 북마크 제거
    fun removeBookmarkedTravel(uid: String, travelId: String): Task<Void> {
        // FieldValue.arrayRemove()를 사용하여 배열에서 요소를 제거
        return usersCollection.document(uid)
            .update("bookmarkedCities", FieldValue.arrayRemove(travelId))
    }
}