package com.example.academictrackerapp.prisca.model.database


data class Goal(
    var id: String = "",
    val userId: String = "",
    val image: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val dueDate: String = "",
    val reminderId: String = "",
    val habitIds: List<String> = emptyList(),
    val taskIds: List<String> = emptyList(),
    //val noteId: List<String> = emptyList(),
    val note: String = "",
    val isAchieved: Boolean = false
) {

        fun toMap(): Map<String, Any> {
            return mapOf(
                "id" to id,
                "userId" to userId,
                "image" to image,
                "title" to title,
                "description" to description,
                "category" to category,
                "dueDate" to dueDate,
                "reminderId" to reminderId,
                //"noteId" to noteId,
                "note" to note,
                "habitIds" to habitIds,
                "taskIds" to taskIds,
                "isAchieved" to isAchieved
            )
        }
}
