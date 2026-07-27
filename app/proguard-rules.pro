# ProGuard rules
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keep class kotlinx.serialization.** { *; }
-keep class io.ktor.** { *; }
