package com.vibelock.app.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import com.vibelock.app.engine.UserState
import com.vibelock.app.data.JournalEntity

// Dummy Weekly Data Class
data class WeeklyData(
    val checkInCount: Int,
    val dominantVibe: String,
    val avgCheckInTime: String,
    val totalXP: Int,
    val streak: Int,
    val missionsCompleted: Int,
    val battlesWon: Int
)

data class JournalAnalysis(
    val energyLevel: String,
    val mood: String,
    val coreTheme: String,
    val auraTake: String,
    val suggestion: String
)

data class WrappedAnalysis(
    val themes: List<com.vibelock.app.data.WrappedTheme>,
    val aiMessage: String
)

class GeminiEngine {

    // WARNING: Replace this placeholder with a real Gemini API Key from aistudio.google.com
    private val apiKey = com.vibelock.app.BuildConfig.GEMINI_API_KEY

    private val model = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            temperature = 0.9f
            maxOutputTokens = 500
            topP = 0.95f
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE)
        )
    )

    suspend fun generateWeeklyVibeReport(data: WeeklyData): String {
        if (apiKey == "YOUR_GEMINI_API_KEY_HERE") return getFallbackReport(data)
        
        val prompt = """
        You are AURA's AI — a Gen Z personality analyst. 
        Write a WEEKLY AURA REPORT in exactly this format:
        
        User Stats This Week:
        - Check-ins: ${data.checkInCount}/7 days
        - Most used vibe: ${data.dominantVibe}
        - Average check-in time: ${data.avgCheckInTime}
        - XP earned: ${data.totalXP}
        - Streak status: ${data.streak} days
        - Missions completed: ${data.missionsCompleted}/21
        - Battles won: ${data.battlesWon}
        
        Rules for your response:
        1. First line: "Your Aura Archetype: [CREATIVE GEN-Z TITLE]" 
           (examples: "Night Demon Energy", "Certified Sigma Grinder", "Chaos Agent Mode", 
            "Early Bird Villain", "Soft Hours Enjoyer", "Based and Consistent")
        2. Then: 3-4 sentences analyzing their week in Gen Z language
           (use: no cap, fr, based, lowkey, slay, bussin, rizz, ngl, ate, cooked)
        3. End with: one line about what to focus on next week
        4. Keep total response under 100 words
        5. Be brutally honest but hype them up
        6. Reference their SPECIFIC vibe patterns
        7. NEVER use corporate language, NEVER say "Great job!"
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            response.text ?: getFallbackReport(data)
        } catch (e: Exception) {
            getFallbackReport(data)
        }
    }

    private fun getFallbackReport(data: WeeklyData): String {
        return when {
            data.checkInCount == 7 -> 
                "Your Aura Archetype: Zero-Miss Legend\nNo cap, 7/7 days this week? That's not discipline, that's an obsession (respectfully). Your ${data.dominantVibe} vibe ran the whole week and honestly ate. Next week: same energy, try for a faster check-in time."
            data.checkInCount >= 5 ->
                "Your Aura Archetype: Consistent But Human\nYou showed up ${data.checkInCount} out of 7 days which is lowkey impressive ngl. The ${data.dominantVibe} era continues. Next week: chase that streak like it owes you money."
            else ->
                "Your Aura Archetype: Comeback Arc Starting\nRough week, we get it. Only ${data.checkInCount} check-ins but the fact you're still here? Based. Your aura needs more charges. Next week: lock in fr fr."
        }
    }

    suspend fun generateDailyReminder(streak: Int, isRoast: Boolean = false): String {
        if (apiKey == "YOUR_GEMINI_API_KEY_HERE") return getFallbackReminder(streak, isRoast)
        
        val tone = if (isRoast) "Roast Mode 🔥 (Sarcastic, witty, brutal, mocking their lack of grind)" else "Hype Mode 💜 (Encouraging, supportive, positive)"
        
        val prompt = """
        You are AURA's AI. Write a 1-sentence PUSH NOTIFICATION to remind the user to "Lock their Vibe" today.
        Their current streak is: $streak days.
        Tone: $tone
        Keep it Gen Z, under 15 words. No emojis, just text.
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            response.text?.trim() ?: getFallbackReminder(streak, isRoast)
        } catch (e: Exception) {
            getFallbackReminder(streak, isRoast)
        }
    }

    private fun getFallbackReminder(streak: Int, isRoast: Boolean): String {
        if (isRoast) {
            return if (streak > 0) "Don't lose your $streak-day streak by being lazy today." else "Zero aura. Lock in before you completely fall off."
        }
        return if (streak > 0) {
            "Bro, $streak-day streak on the line. Lock your vibe right now fr."
        } else {
            "Aura at zero. Time to lock in and start the grind today."
        }
    }

    suspend fun generateStoryQuote(tier: String): String {
        if (apiKey == "YOUR_GEMINI_API_KEY_HERE") return getFallbackStoryQuote(tier)

        val prompt = """
        You are AURA's AI. Generate a short, edgy, single-sentence cyberpunk/Gen-Z aesthetic quote 
        about aura, vibes, or leveling up. It is for a user who is currently in the '$tier' tier.
        Keep it under 15 words. No quotation marks.
        Example: "Your aura speaks before you even enter the room."
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            response.text?.trim()?.removeSurrounding("\"") ?: getFallbackStoryQuote(tier)
        } catch (e: Exception) {
            getFallbackStoryQuote(tier)
        }
    }

    private fun getFallbackStoryQuote(tier: String): String {
        val quotes = listOf(
            "Your aura speaks before you even enter the room.",
            "Silence the noise. Lock in your energy.",
            "Vibes unmatched. Energy protected.",
            "Not just existing. Ascending.",
            "They can copy the style, but not the aura."
        )
        return quotes.random()
    }

    suspend fun generateVibeJournalAnalysis(
        vibe: String, 
        userText: String, 
        userState: UserState, 
        recentEntries: List<JournalEntity>, 
        isRoast: Boolean
    ): JournalAnalysis {
        if (apiKey == "YOUR_GEMINI_API_KEY_HERE") {
            return JournalAnalysis(
                energyLevel = "High intent, low physical energy",
                mood = "Determined but drained",
                coreTheme = "Pushing through resistance",
                auraTake = "Tu thaka hua hai but rukna nahi chahta — yahi grind ka asli test hai. Aaj ek kaam kar jo kal ke liye easier bana de.",
                suggestion = "• Ek priority task choose karo, sirf ek\n• 25 min focused kaam, 5 min break\n• Raat ko phone band kar ke so jaao"
            )
        }

        val baseTone = if (isRoast) "Roast Mode 🔥 (Sarcastic, witty, brutal, reality check)" else "Hype Mode 💜 (Encouraging, supportive, positive)"
        
        // Build Context Payload
        val recentHistory = recentEntries.take(5).joinToString("\n") { 
            "- ${it.selectedVibe}: ${it.rawText}" 
        }
        
        val contextPayload = """
        USER CONTEXT PAYLOAD:
        - Current Level: ${userState.level}
        - Current Streak: ${userState.currentStreak} days
        - Highest Streak: ${userState.highestStreak} days
        - Recent Journal Entries (Last 5):
        ${if (recentHistory.isEmpty()) "No recent entries." else recentHistory}
        """.trimIndent()

        val prompt = """
        You are AURA's AI Mood Companion.
        
        $contextPayload
        
        The user just selected the vibe "$vibe" today and wrote:
        "$userText"
        
        INSTRUCTIONS:
        1. Actively reference their context! If their streak is high, mention it. If their recent entries were all "Chaos" and today is "Chill", point out the shift. Make them feel like you remember them.
        2. TONE: $baseTone. 
        3. CRISIS DETECTION: If they have selected Sad/Chaos for consecutive days AND their text contains negative/crisis keywords (tired, done, give up, hurt, etc.), drop the persona immediately. Be genuinely empathetic, supportive, and suggest they take a break or talk to a friend. 
        4. Use Hinglish naturally.
        
        Analyze the input and provide the output in exact JSON format matching this structure, with no markdown wrappers or backticks around the JSON string:
        {
            "energyLevel": "Brief description of energy",
            "mood": "Brief description of mood",
            "coreTheme": "Core theme of the entry",
            "auraTake": "A highly personalized, proactive paragraph addressing the user directly based on their entry AND past history. (Hinglish allowed)",
            "suggestion": "2-3 bullet points with actionable advice. Use • for bullets."
        }
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            var jsonText = response.text ?: "{}"
            jsonText = jsonText.replace("```json", "").replace("```", "").trim()
            val jsonObject = org.json.JSONObject(jsonText)
            JournalAnalysis(
                energyLevel = jsonObject.optString("energyLevel", "Neutral"),
                mood = jsonObject.optString("mood", "Unclear"),
                coreTheme = jsonObject.optString("coreTheme", "Everyday Grind"),
                auraTake = jsonObject.optString("auraTake", "Keep pushing forward."),
                suggestion = jsonObject.optString("suggestion", "• Take a break\n• Stay hydrated")
            )
        } catch (e: Exception) {
            JournalAnalysis(
                energyLevel = "Unknown",
                mood = "Unknown",
                coreTheme = "Error parsing vibe",
                auraTake = "Aura Matrix encountered a glitch. But your vibe is recorded. Keep pushing.",
                suggestion = "• Keep your head up\n• Try again later"
            )
        }
    }

    suspend fun generateWrappedAnalysis(entries: List<JournalEntity>, isRoast: Boolean): WrappedAnalysis {
        if (apiKey == "YOUR_GEMINI_API_KEY_HERE" || entries.isEmpty()) {
            return WrappedAnalysis(
                themes = listOf(
                    com.vibelock.app.data.WrappedTheme("Hustle", 40),
                    com.vibelock.app.data.WrappedTheme("Rest", 25),
                    com.vibelock.app.data.WrappedTheme("Goals", 35)
                ),
                aiMessage = if (isRoast) "You missed 8 days bro. 74% consistency. Decent, not legendary. Next month decides your fate."
                            else "You survived the month. Consistency takes time, and you're getting there. Keep building that aura."
            )
        }

        val baseTone = if (isRoast) "Roast Mode 💀 (Sarcastic, witty, brutal, reality check)" else "Hype Mode 🔥 (Encouraging, supportive, positive)"
        
        val recentHistory = entries.takeLast(30).joinToString("\n") { 
            "- ${it.selectedVibe}: ${it.rawText}" 
        }

        val prompt = """
        You are AURA's AI. The user is requesting their "Vibe Wrapped" (a Spotify-Wrapped style monthly recap).
        
        Here are their journal entries from this month:
        $recentHistory
        
        INSTRUCTIONS:
        1. Extract the top 3 emotional or topic themes from these entries (e.g., "Hustle", "Anxiety", "Goals").
        2. Assign a rough percentage to each theme so they add up to 100%.
        3. Write a cinematic, impactful paragraph ("aiMessage") summarizing their month in $baseTone. Use Hinglish naturally.
        
        Return EXACT JSON FORMAT:
        {
            "themes": [
                {"title": "Theme 1", "percentage": 40},
                {"title": "Theme 2", "percentage": 30},
                {"title": "Theme 3", "percentage": 30}
            ],
            "aiMessage": "Your summary paragraph here."
        }
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            var jsonText = response.text ?: "{}"
            jsonText = jsonText.replace("```json", "").replace("```", "").trim()
            val jsonObject = org.json.JSONObject(jsonText)
            
            val themesArray = jsonObject.optJSONArray("themes")
            val themes = mutableListOf<com.vibelock.app.data.WrappedTheme>()
            if (themesArray != null) {
                for (i in 0 until themesArray.length()) {
                    val t = themesArray.getJSONObject(i)
                    themes.add(com.vibelock.app.data.WrappedTheme(t.optString("title"), t.optInt("percentage")))
                }
            } else {
                themes.add(com.vibelock.app.data.WrappedTheme("Unknown", 100))
            }
            
            WrappedAnalysis(
                themes = themes,
                aiMessage = jsonObject.optString("aiMessage", "Error generating recap.")
            )
        } catch (e: Exception) {
            WrappedAnalysis(
                themes = listOf(com.vibelock.app.data.WrappedTheme("Error parsing themes", 100)),
                aiMessage = "Aura Matrix encountered a glitch summarizing your month."
            )
        }
    }
}
