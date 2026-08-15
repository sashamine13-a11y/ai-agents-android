# Shared KMM module for AIAgents

This module contains common business logic (domain/data) that can be used by Android and Desktop frontends.

Folder layout:
- src/commonMain/kotlin - shared code
- src/androidMain/kotlin - Android-specific implementations
- src/desktopMain/kotlin - Desktop-specific implementations

Add your domain and data packages here and move code from app/src/main/java/com/aiagents/app/domain and data into commonMain.
