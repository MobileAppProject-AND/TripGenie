package com.example.tripgenie.data.model

data class User(
    val uid: String = "",  // Google Auth의 UID를 저장
    val email: String = "", // Google 계정 이메일
    var basicInfo: BasicInfo = BasicInfo(),
    var travelPreferences: TravelPreferences = TravelPreferences(),
    var bookmarkedTravels: MutableList<String> = mutableListOf()
)

data class BasicInfo(
    var name: String = "-",
    var age: Int = 0,
    var gender: Gender = Gender.UNDISCLOSED,
    var groupSize: Int = 1,
)

data class TravelPreferences(
    var travelPurpose: TravelPurpose = TravelPurpose.NONE,
    var preferredEnvironment: PreferredEnvironment = PreferredEnvironment.NONE,
    var preferredActivities: ActivityType = ActivityType.NONE,
    var hobbies: Hobby = Hobby.NONE,
)

enum class Gender {
    MALE,
    FEMALE,
    OTHER,
    UNDISCLOSED,
}

enum class TravelPurpose {
    NONE,
    LEISURE,
    BUSINESS,
    EDUCATION,
    OTHER,
}

enum class PreferredEnvironment {
    NONE,
    URBAN,
    RURAL,
    MOUNTAIN,
    BEACH,
    DESERT,
    OTHER,
}

enum class ActivityType {
    NONE,
    SIGHTSEEING,
    SHOPPING,
    FOOD,
    NIGHTLIFE,
    NATURE,
    ADVENTURE,
    CULTURE,
    RELIGION,
    HISTORY,
    SPORTS,
    MUSIC,
    ART,
    READING,
    WRITING,
    PHOTOGRAPHY,
    COOKING,
    TRAVELING,
    OTHER,
}

enum class Hobby {
    NONE,
    SPORTS,
    MUSIC,
    ART,
    READING,
    WRITING,
    PHOTOGRAPHY,
    COOKING,
    TRAVELING,
    OTHER,
}