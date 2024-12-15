package com.example.tripgenie.data.repository

import com.example.tripgenie.data.model.BasicInfo
import com.example.tripgenie.data.model.TravelPreferences
import com.example.tripgenie.data.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    fun getUser(uid: String): Task<DocumentSnapshot> {
        return usersCollection.document(uid).get()
    }

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

    // 북마크 추가
    fun addBookmarkedCity(uid: String, city: String): Task<Void> {
        // FieldValue.arrayUnion()을 사용하여 배열에 요소를 추가
        // 이미 존재하는 경우 중복 추가되지 않음
        return usersCollection.document(uid)
            .update("bookmarkedCities", FieldValue.arrayUnion(city))
    }

    // 북마크 제거
    fun removeBookmarkedCity(uid: String, city: String): Task<Void> {
        // FieldValue.arrayRemove()를 사용하여 배열에서 요소를 제거
        return usersCollection.document(uid)
            .update("bookmarkedCities", FieldValue.arrayRemove(city))
    }

    // 유저의 북마크 목록 가져오기
    fun getUserBookmarks(uid: String): Task<DocumentSnapshot> {
        return usersCollection.document(uid)
            .get()
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
}