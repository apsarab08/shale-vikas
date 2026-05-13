# Shaale-Vikas (Jetpack Compose) — Setup Guide

## Tech Stack
- **Language:** Kotlin 100%
- **UI:** Jetpack Compose (NO XML layouts)
- **Architecture:** MVVM (ViewModel + StateFlow)
- **Backend:** Firebase (Auth + Realtime Database + Storage)
- **Navigation:** Compose Navigation
- **Images:** Coil
- **Session:** DataStore Preferences
- **Concurrency:** Kotlin Coroutines + Flow

---

## Project Structure

```
ShaaleVikasCompose/
├── app/src/main/kotlin/com/shaalevikas/app/
│   ├── MainActivity.kt              ← Single activity
│   ├── model/
│   │   └── Models.kt                ← NeedItem, Pledge, AlumniUser
│   ├── repository/
│   │   └── FirebaseRepository.kt   ← All Firebase calls
│   ├── viewmodel/
│   │   └── AppViewModel.kt         ← Shared ViewModel, StateFlows
│   ├── utils/
│   │   └── SessionManager.kt       ← DataStore session
│   └── ui/
│       ├── Navigation.kt            ← Route strings
│       ├── AppNavGraph.kt           ← NavHost with all routes
│       ├── theme/
│       │   ├── Color.kt
│       │   └── Theme.kt
│       ├── components/
│       │   └── Components.kt        ← Reusable Compose components
│       └── screens/
│           ├── SplashAndRoleScreens.kt
│           ├── AuthScreens.kt       ← Alumni login/register + Admin login
│           ├── AlumniHomeScreen.kt  ← Bottom nav host
│           ├── NeedsTab.kt          ← Live needs + pledge dialog
│           ├── DonorsAndImpactTabs.kt
│           ├── NeedDetailScreen.kt
│           ├── AdminHomeScreen.kt   ← Admin dashboard
│           └── AddEditNeedScreen.kt ← Add/edit need + image picker
```

---

## Step 1: Firebase Setup

1. Go to https://console.firebase.google.com
2. Create project → `ShaaleVikasCompose`
3. Enable **Authentication** → Email/Password
4. Enable **Realtime Database** → Start in test mode
5. Enable **Storage** → Start in test mode
6. Add Android app → package: `com.shaalevikas.app`
7. Download `google-services.json` → place in `app/` folder

---

## Step 2: Open in Android Studio

1. Open Android Studio **Hedgehog** or newer (required for Compose)
2. Open → select `ShaaleVikasCompose` folder
3. Wait for Gradle sync
4. If asked about JDK, use JDK 17

---

## Step 3: Create Admin Account

1. Firebase Console → Authentication → Add user
   - Email: `headmaster@zpschool.edu`
   - Password: `Admin@1234`
2. Copy the UID
3. Firebase → Realtime Database → Data → Add manually:
```json
{
  "admins": {
    "YOUR_UID_HERE": true
  }
}
```

---

## Step 4: Seed Sample Data

Import this JSON into your Firebase Realtime Database:

```json
{
  "needs": {
    "need001": {
      "id": "need001",
      "title": "Toilet Block Repair",
      "description": "Leaking roof in toilet block. Affects 240 students daily.",
      "priority": "Urgent",
      "targetAmount": 35000,
      "raisedAmount": 21000,
      "completed": false,
      "photoUrl": "",
      "afterPhotoUrl": "",
      "addedBy": "admin",
      "createdAt": 1700000000000
    },
    "need002": {
      "id": "need002",
      "title": "5 Sets of Wall Paint",
      "description": "Classroom walls are peeling. Need 5 sets of paint.",
      "priority": "Medium",
      "targetAmount": 8000,
      "raisedAmount": 3200,
      "completed": false,
      "photoUrl": "",
      "afterPhotoUrl": "",
      "addedBy": "admin",
      "createdAt": 1700000001000
    },
    "need003": {
      "id": "need003",
      "title": "6 Broken Classroom Desks",
      "description": "Students sitting on floor. Need 6 replacement desks.",
      "priority": "Planned",
      "targetAmount": 12000,
      "raisedAmount": 1800,
      "completed": false,
      "photoUrl": "",
      "afterPhotoUrl": "",
      "addedBy": "admin",
      "createdAt": 1700000002000
    }
  },
  "admins": {},
  "pledges": {},
  "alumni": {}
}
```

---

## Step 5: Firebase Rules

Paste into Realtime Database → Rules:

```json
{
  "rules": {
    "needs": {
      ".read": "auth != null",
      ".write": "auth != null && root.child('admins').child(auth.uid).val() === true",
      "$needId": {
        "raisedAmount": { ".write": "auth != null" }
      }
    },
    "pledges": {
      ".read": "auth != null",
      ".write": "auth != null"
    },
    "alumni": {
      ".read": "auth != null",
      "$uid": { ".write": "auth != null && auth.uid === $uid" }
    },
    "admins": {
      ".read": "auth != null && root.child('admins').child(auth.uid).val() === true",
      ".write": false
    }
  }
}
```

Storage rules:
```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /need_photos/{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

---

## App Flow

```
Splash (auto-detects session)
    ├── No session → Role Select
    │       ├── Alumni → Login/Register → Alumni Home
    │       │              ├── Needs Tab (live data + pledge)
    │       │              ├── Donors Tab (hall of fame)
    │       │              └── Impact Tab (before/after)
    │       │                      └── Tap any need → Need Detail
    │       └── Headmaster → Admin Login → Admin Dashboard
    │                              ├── View all needs + stats
    │                              ├── Add Need (form + photo)
    │                              ├── Edit / Delete Need
    │                              └── Mark Complete + After Photo
    └── Has session → Goes straight to correct home
```

---

## Troubleshooting

| Problem | Solution |
|---|---|
| `google-services.json` error | Put it inside the `app/` folder, not root |
| Build error: `accompanist` not found | Remove SystemUiController usage from MainActivity if not needed |
| Compose version mismatch | Use AGP 8.2+ and Kotlin 1.9.x |
| Firebase auth fails | Check SHA-1 is added in Firebase console |
| Photos not showing | Check Coil dependency and internet permission |
