package com.example.academictrackerapp.katiana.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.academictrackerapp.MainActivity
import com.example.academictrackerapp.databinding.DashboardBinding
import com.example.academictrackerapp.prisca.model.database.Goal
import com.example.academictrackerapp.prisca.model.database.Task
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class Dashboard : Fragment() {

    private var _binding: DashboardBinding? = null
    private val binding get() = _binding!!
    private val userId: String? by lazy { MainActivity.auth.currentUser?.uid }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DashboardBinding.inflate(inflater, container, false)
        speakToAI()
        return binding.root
    }

    private fun speakToAI() {
        val firestore = FirebaseFirestore.getInstance()

        /*if (userId == null) {
            binding.insightsText.text = "Error: User not authenticated."
            return
        }*/

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Call Generative AI Model
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = "AIzaSyBqrTtlfMhcGzmvDdWcaEd5_WDy3FYO348" // Make sure this is a valid key
                )

                // Fetch data in parallel
                val goalsDeferred = async { fetchGoals(firestore) }
                val marksDeferred = async { fetchMarks(firestore) }
                val tasksDeferred = async { fetchTasks(firestore) }
                val timeTableDeferred = async { fetchTimetable(firestore) }

                // Store Data in the following variables
                val goals = goalsDeferred.await()
                val marks = marksDeferred.await()
                val tasks = tasksDeferred.await()
                val timetable = timeTableDeferred.await()
                val averageMarks = marks.average()
                val pendingTasks = tasks?.filter { !it.isFinished }?.size ?: 0
                val overdueTasks = tasks?.filter {
                    it.dueDate.isNotEmpty() && isUpcoming(it.dueDate)
                }?.size ?: 0

                // Call Generate functions
                val prompt = generateInsights(goals, marks, tasks, timetable)
                val prompt_Percentage = generateStudyPatternPercentage ()
                val prompt_Text = generateStudyPatternText ()
                val prompt_StudyTime = generateStudyTime()
                val prompt_MarksImprovement = generateMarksImprovement ()
                val prompt_PersonalisedInsights = generatePersonalisedInsights ()
                val prompt_StudyRecommendations = generateStudyRecommendations ()
                val prompt_SubjectsDescription = generateSubjectsDescription ()
                val prompt_TimeToStudy = generateTimeToStudy ()
                val prompt_AcademicPerformance = generateAcademicPerformance ()

                // Display generated AI response on the screen
                _binding?.insightsText?.text = generativeModel.generateContent(prompt).text
                //_binding?.completionRateText?.text = generativeModel.generateContent(prompt_Percentage).text
                //_binding?.completionRateChange?.text = generativeModel.generateContent(prompt_Text).text
                //_binding?.studyTimeText?.text = generativeModel.generateContent(prompt_StudyTime).text
                //_binding?.subjectScore?.text = generativeModel.generateContent(prompt_MarksImprovement).text
                //_binding?.insightsText?.text = generativeModel.generateContent(prompt_PersonalisedInsights).text
                //_binding?.recommendationsText?.text = generativeModel.generateContent(prompt_StudyRecommendations).text
                //_binding?.subjectDescription?.text = generativeModel.generateContent(prompt_SubjectsDescription).text
                //_binding?.timeToStudy?.text = generativeModel.generateContent(prompt_TimeToStudy).text
                //_binding?.historicalInsightsText?.text = generativeModel.generateContent(prompt_AcademicPerformance).text

            } catch (e: Exception) {
                e.printStackTrace()
                binding.insightsText.text = "Failed to generate insights: ${e.message}"
            }
        }
    }

    private fun generateStudyPatternPercentage (){

    }

    private fun generateStudyPatternText (){

    }

    private fun generateStudyTime(){

    }

    private fun generateMarksImprovement (){

    }

    private fun generatePersonalisedInsights (){

    }

    private fun generateStudyRecommendations (){

    }

    private fun generateSubjectsDescription (){

    }

    private fun generateTimeToStudy (){

    }

    private fun generateAcademicPerformance (){

    }

    private fun generateInsights(
        goals: List<Goal>,
        marks: List<Int>,
        tasks: List<Task>,
        timetable: List<Task>
    ): String {
        val achievedGoals = goals.count { it.isAchieved }
        val totalGoals = goals.size
        val upcomingGoals = goals.count { isUpcoming(it.dueDate) }

        val averageMarks = if (marks.isNotEmpty()) marks.average() else 0.0
        val pendingTasks = tasks.count { !it.isFinished }
        val overdueTasks = tasks.count {
            isUpcoming(it.dueDate)
        }

        return """
            Insights for You:
            - Achieved goals: $achievedGoals out of $totalGoals
            - Upcoming goals: $upcomingGoals
            - Average marks: $averageMarks
            - Pending tasks: $pendingTasks
            - Overdue tasks: $overdueTasks
            - Timetable: $timetable
        """.trimIndent()
    }

    private suspend fun fetchGoals(firestore: FirebaseFirestore): List<Goal> {
        return try {
            val snapshot = firestore.collection("goals")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.toObjects(Goal::class.java)
        } catch (e: Exception) {
            emptyList() // Handle query failure
        }
    }

    private suspend fun fetchTimetable(firestore: FirebaseFirestore): List<Task> {
        return try {
            val timetableSnapshot = firestore.collection("timeTable")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            timetableSnapshot.toObjects(Task::class.java)
        } catch (e: Exception) {
            emptyList() // Handle query failure
        }
    }

    private suspend fun fetchMarks(firestore: FirebaseFirestore): List<Int> {
        return try {
            val snapshot = firestore.collection("marks")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.documents.map { it.getString("marks")?.toIntOrNull() ?: 0 }
        } catch (e: Exception) {
            emptyList() // Handle query failure
        }
    }

    private suspend fun fetchTasks(firestore: FirebaseFirestore): List<Task> {
        return try {
            val snapshot = firestore.collection("tasks")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.toObjects(Task::class.java)
        } catch (e: Exception) {
            emptyList() // Handle query failure
        }
    }

    private fun isUpcoming(dueDate: String): Boolean {
        return if (dueDate.isNotEmpty()) {
            try {
                val formatter = DateTimeFormatter.ISO_DATE
                val goalDate = LocalDate.parse(dueDate, formatter)
                goalDate.isAfter(LocalDate.now())
            } catch (e: DateTimeParseException) {
                false
            }
        } else {
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}

