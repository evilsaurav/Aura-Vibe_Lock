# ╔══════════════════════════════════════════════════════╗
# ║   AURA — MASTER ANTIGRAVITY PROMPT DOCUMENT         ║
# ║   Final Professional Build · Azure + Firebase       ║
# ║   AI Integration · 4D Design · Production Ready     ║
# ╚══════════════════════════════════════════════════════╝

## ⚡ IMPORTANT: ANTIGRAVITY API KEY INSTRUCTIONS
Before starting any Phase — Antigravity ko yeh instructions do:

Antigravity, is project ke liye tujhe kuch API keys chahiye honge:

1. FIREBASE (FREE — no credit card):
   → firebase.google.com par jao → New Project "AURA-App" banao
   → Authentication enable karo (Google + Phone)
   → Firestore Database enable karo (Production mode)
   → google-services.json download karo → /app folder mein daalo
   → Hume chahiye: google-services.json file contents

2. GOOGLE GEMINI AI (FREE — generous free tier):
   → aistudio.google.com par jao → "Get API Key" click karo
   → New API Key create karo
   → Hume chahiye: GEMINI_API_KEY (starts with "AIza...")

3. AZURE FOR STUDENTS ($100 FREE credit):
   → azure.microsoft.com/free/students par jao
   → Student email se sign up karo (no credit card needed)
   → Azure Notification Hubs create karo (Free tier: 1M pushes/month)
   → Hume chahiye: Azure Notification Hub Connection String

4. GOOGLE MAPS SDK (FREE for students):
   → console.cloud.google.com → Maps SDK for Android enable karo
   → API Key restrict karo to com.aura.app package
   → Hume chahiye: MAPS_API_KEY

Agar koi key available nahi hai toh mujhse poochho —
main mock data se kaam chalaunga aur key placeholder rakhunga.

---

# ═══════════════════════════════════════════════
# BACKEND ARCHITECTURE DECISION
# ═══════════════════════════════════════════════

## Azure Student Account Mein Kya Free Milega

| Service | Free Limit | AURA Use |
|---|---|---|
| Azure Notification Hubs | 1M pushes/month FREE | Push notifications |
| Azure Cosmos DB | 1000 RU/s + 25GB FREE | Global DB (optional) |
| Azure App Service | F1 tier FREE | REST API backend |
| Azure Functions | 1M executions/month FREE | Serverless triggers |
| Azure SignalR | 20 connections FREE | Real-time friend updates |
| Azure AI Services | F0 tier FREE | Sentiment/language |
| Azure OpenAI | $100 credit (student) | AI Vibe Reports |
| Azure Static Web Apps | FREE | Admin dashboard |

## Recommended Stack (Student-Friendly + FREE)

PRIMARY:
  Authentication   → Firebase Auth (Google + Phone OTP) [FREE]
  Real-time DB     → Firebase Firestore [FREE tier generous]
  Push Notif.      → Azure Notification Hubs [FREE 1M/month]
  AI Reports       → Google Gemini 2.0 Flash API [FREE]
  Maps/Globe       → Google Maps SDK [FREE with student account]
  
AZURE STUDENT ACCOUNT USE FOR:
  Push Notifications → Azure Notification Hubs (better than FCM for scale)
  Serverless Logic   → Azure Functions (weekly XP reset, battle results)
  Future Backend API → Azure App Service F1 tier
  
WHY THIS STACK:
  - Zero cost for student project
  - Firebase Firestore: 50,000 reads + 20,000 writes per day FREE
  - Gemini API: 15 requests/min + 1M tokens/day FREE (no credit card)
  - Azure Notif Hubs: 1M push notifications/month FREE

---

# ═══════════════════════════════════════════════
# PHASE A: AUTHENTICATION SYSTEM
# Complete Google + Phone OTP Login
# ═══════════════════════════════════════════════

## ANTIGRAVITY PROMPT A.1 — Project Setup & Dependencies

Project: AURA Android App (Kotlin + Jetpack Compose)
Task: Setup all dependencies for Authentication + Firebase + AI

=== ANTIGRAVITY: PEHLE POOCHHO ===
"Kya google-services.json file ready hai? 
 Agar nahi toh firebase.google.com par project banao,
 Authentication enable karo (Google + Phone), 
 Firestore enable karo, aur google-services.json download karo.
 Mujhe file do ya contents paste karo."

## ANTIGRAVITY PROMPT A.2 — Auth Screen UI (4D Premium Design)
...
## ANTIGRAVITY PROMPT A.3 — Firebase Auth ViewModel
...
# PHASE B: FIREBASE BACKEND INTEGRATION
...
# PHASE C: AI INTEGRATION (GEMINI API)
...
# PHASE D: PROFILE SETTINGS
...
# PHASE E: 4D GLOBAL FRIEND CIRCLE
...
