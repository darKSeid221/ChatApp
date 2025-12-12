# Chat App [In-Progress]

Offline First Android App to show single screen chat screen with chats, ability to send and recieve message and send queued chats.

<img width="200" height="400" alt="Screenshot 2025-12-12 at 8 48 47 AM" src="https://github.com/user-attachments/assets/c89306cb-1018-4ea7-a35b-d0d9eb2620f7" />


## 🏗️ Architecture

**MVVM + Clean Architecture**

```
Presentation → ViewModel → Domain → Data
```

## 🛠️ Tech Stack

- Kotlin, Compose
- Coroutines & Flow
- Room, Dagger, Websocket
- Min SDK: 24 | Target SDK: 35

## 🚀 Getting Started
1. Clone repository
2. Open in Android Studio
3. Sync Gradle
4. Run app

```bash
./gradlew assembleDebug  # Build
./gradlew test          # Test
```

## 🌿 Branching Strategy

**Rules:**
- ✅ Always checkout from `development` branch
- ✅ Create feature branches: `feature/feature-name`
- ❌ Never push to `main` (protected)

**Workflow:**
```bash
git checkout development
git pull origin development
git checkout -b feature/your-feature-name
# Make changes, commit, push
git push origin feature/your-feature-name
# Create PR to development
```

## 🎯 MVVM Best Practices

- **View**: UI only (XML Screens)
- **ViewModel**: Business logic, state management (StateFlow/LiveData)
- **Repository**: Single source of truth
- Use `viewModelScope` for coroutines
- Unit test ViewModels

## 🤝 Contributing

1. Follow MVVM architecture
2. Create feature branch from `development`
3. Write clean, tested code
4. Follow code style guidelines

---

