# Chat App [In-Progress]

Offline First Android App to show single screen chat screen with chats, ability to send and recieve message and send queued chats.

<img width="200" height="400" alt="Screenshot 2025-12-12 at 9 50 53 PM" src="https://github.com/user-attachments/assets/e9796b02-39a6-4fa4-8210-fc78ad46279c" />

<img width="200" height="400" alt="Screenshot 2025-12-12 at 9 19 39 PM" src="https://github.com/user-attachments/assets/7c68d426-4fcc-4e4d-8ea8-f237f9e382d6" />

<img width="200" height="400" alt="Screenshot 2025-12-12 at 9 15 57 PM" src="https://github.com/user-attachments/assets/faa99519-d97f-4064-be05-c801dc1239cf" />

<img width="200" height="400" alt="Screenshot 2025-12-12 at 9 19 32 PM" src="https://github.com/user-attachments/assets/de5e18f3-87a8-427e-ae68-e49315bd47f1" />

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

