# KidsTube ProGuard rules

# Moshi
-keepattributes *Annotation*
-keep class com.kidstube.core.network.dto.** { *; }
-keepclassmembers class com.kidstube.core.network.dto.** {
    <init>(...);
}
-keep class com.squareup.moshi.** { *; }
-keepclassmembers @com.squareup.moshi.JsonClass class * { *; }

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Room
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers class * extends androidx.room.RoomDatabase {
    abstract *;
}

# Kotlin coroutines
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# YouTubePlayer
-keep class com.pierfrancescosoffritti.androidyoutubeplayer.** { *; }
