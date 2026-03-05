# Vartovyi

Vartovyi (Вартовий) — Android app designed for Ukrainians living under constant drone (Shahed) threat. It monitors incoming Telegram notifications in real-time, analyzes message text against user-defined keywords (district, street, city), and triggers a loud full-screen alarm only when the threat is relevant to your specific area. All other messages are silently ignored, allowing you to sleep undisturbed until there is real danger nearby.
Key features:

- Intercepts Telegram notifications via NotificationListenerService
- Regex-based keyword matching with stop-word filtering
- Full-volume alarm that bypasses Do Not Disturb mode
- Lock-screen alert activity (like an incoming call)
- Foreground service with watchdog for reliable background operation
- Schedule support (e.g. active only 22:00–07:00)
- Optional channel name filtering

Tech stack: Kotlin, Jetpack Compose, Material 3, MVI, Koin, DataStore, Navigation Compose (type-safe routes), WorkManager.
