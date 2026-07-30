diff --git a/app/src/main/java/com/aiagents/app/MainActivity.kt b/app/src/main/java/com/aiagents/app/MainActivity.kt
index 1dd8fbc..0000000 100644
--- a/app/src/main/java/com/aiagents/app/MainActivity.kt
+++ b/app/src/main/java/com/aiagents/app/MainActivity.kt
@@
 package com.aiagents.app
 
 import android.os.Bundle
+import com.aiagents.platform.PlatformFile
+import com.aiagents.platform.PlatformStorage
 import androidx.activity.ComponentActivity
 import androidx.activity.compose.setContent
 import androidx.activity.enableEdgeToEdge
@@
     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
-        enableEdgeToEdge()
+        enableEdgeToEdge()
+        // initialize platform-specific storage/file utilities
+        PlatformFile.appContext = applicationContext
+        PlatformStorage.init(applicationContext)
         setContent {
