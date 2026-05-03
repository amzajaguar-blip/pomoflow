# ProGuard rules for Pomodoro Pro

# Keep all classes in the app package
-keep class com.example.pomodorotimer.** { *; }

# Keep ViewModel classes
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }

# Keep Material3 classes
-keep class androidx.compose.material3.** { *; }

# Keep Kotlin classes
-keep class kotlin.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Keep Google Play Services
-keep class com.google.android.gms.** { *; }
-keep class com.google.android.gms.ads.** { *; }

# Keep SharedPreferences
-keep class android.content.SharedPreferences { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Optimization
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose

# Renaming
-allowaccessmodification
-repackageclasses ''

# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep enum classes (Compose usa enum)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}
