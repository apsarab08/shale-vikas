package com.shaalevikas.app.model

data class NeedItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val priority: String = "Planned",   // "Urgent" | "Medium" | "Planned"
    val targetAmount: Long = 0L,
    val raisedAmount: Long = 0L,
    val photoUrl: String = "",
    val afterPhotoUrl: String = "",
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val addedBy: String = ""
) {
    fun progressPercent(): Int =
        if (targetAmount == 0L) 0
        else (raisedAmount * 100 / targetAmount).toInt().coerceIn(0, 100)

    fun priorityOrder(): Int = when (priority) {
        "Urgent" -> 0
        "Medium" -> 1
        else     -> 2
    }
}

data class Pledge(
    val id: String = "",
    val needId: String = "",
    val needTitle: String = "",
    val alumniUid: String = "",
    val alumniName: String = "",
    val alumniCity: String = "",
    val amount: Long = 0L,
    val timestamp: Long = System.currentTimeMillis()
)

data class AlumniUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val city: String = "",
    val graduationYear: String = "",
    val phone: String = "",
    val pledgeCount: Int = 0,
    val totalPledged: Long = 0L
) {
    fun initials(): String {
        val parts = name.trim().split(" ")
        return if (parts.size >= 2)
            "${parts[0].first()}${parts[1].first()}".uppercase()
        else
            parts.firstOrNull()?.first()?.uppercase().orEmpty()
    }
}

enum class UserRole { ALUMNI, ADMIN, NONE }
