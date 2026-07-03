# Aura Vibe Lock 🔮

Aura Vibe Lock is a Gen Z-focused gamified habit-tracking and journaling app built with modern Android (Jetpack Compose). It transforms the boring concept of daily check-ins into an addictive, RPG-style experience with dynamic 3D visuals, cinematic aesthetics, and personalized AI-driven "Aura" analysis.

## 🚀 Key Features

*   **Vibe Lock Engine (Gamification):** Users build their "Aura" by locking in their daily vibe. The custom gamification engine grants XP, streaks, and aura points based on consistency and check-in times.
*   **Dynamic Tier System:** Level up from "NOOB CIVILIAN" to "AURA GOD." Tiers unlock new neon themes, profile badges, and special interactions within the app.
*   **Gemini AI Analysis:** Powered by Google's Gemini AI, the app reads your daily journal entries and provides a highly personalized, sometimes brutally honest "Aura Take" and actionable suggestions (available in Hype or Roast mode).
*   **Vibe Wrapped (Aura Story):** A dynamic, Instagram-style 7-card shareable poster summarizing your monthly vibes, streaks, dominant emotions, and a custom generated AI quote.
*   **Background Notification Guardian:** A passive background worker (WorkManager) that monitors your check-in cadence. If you haven't locked your vibe in 12 hours, you get a custom notification to save your streak!
*   **Cyberpunk Aesthetics:** Built from the ground up utilizing neon gradients, glassmorphism UI patterns, particle effects, and heavy fluid animations for a state-of-the-art cinematic feel.
*   **Social & Global Features:** Connect with friends via Firebase, monitor the global leaderboard, and battle other users' auras based on XP and streak stats.

## 🛠 Tech Stack

*   **UI/UX:** Jetpack Compose (100% Kotlin), Material3, Custom Canvas Drawing (for Particles and Holographic borders).
*   **Local Storage:** Room Database (SQLite) for high-performance daily check-in data, XP tracking, and offline persistence.
*   **Cloud & Social:** Firebase Firestore (Realtime database) for global leaderboards, friend requests, and squad mechanics.
*   **AI Integration:** Google Generative AI SDK (Gemini 2.0 Flash model) for dynamic insights, quote generation, and vibe summarization.
*   **Background Work:** Android WorkManager for guaranteed background notifications (Streak Protection).

## 📥 Setup Instructions

1.  Clone the repository.
2.  Open the project in **Android Studio**.
3.  Add your own Google Gemini API key to `local.properties`:
    ```properties
    GEMINI_API_KEY=<YOUR_GEMINI_API_KEY_HERE>
    ```
4.  Sync Gradle and run the app!

## 📸 Screenshots


