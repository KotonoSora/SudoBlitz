# NumGrid ProGuard Rules

# -----------------------------------------------------------------------------------
# General Rules
# -----------------------------------------------------------------------------------

# Preserve line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# Preserve Annotations and Signatures for Retrofit, Room, and Moshi
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# -----------------------------------------------------------------------------------
# Jetpack Compose
# -----------------------------------------------------------------------------------
# Compose rules are generally included in the library, but keeping some common ones.
-keepclassmembers class androidx.compose.ui.platform.ComposeView {
   public *;
}

# -----------------------------------------------------------------------------------
# Room
# -----------------------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# -----------------------------------------------------------------------------------
# Retrofit / OkHttp
# -----------------------------------------------------------------------------------
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# -----------------------------------------------------------------------------------
# Moshi (for JSON parsing)
# -----------------------------------------------------------------------------------
# Retain generic type information for use by Moshi’s adapters.
-keep class com.squareup.moshi.* { *; }
-keep class kotlin.reflect.jvm.internal.** { *; }
-keep @com.squareup.moshi.JsonQualifier interface *
-keep @com.squareup.moshi.JsonClass class * {
    <init>(...);
}

# -----------------------------------------------------------------------------------
# Coroutines
# -----------------------------------------------------------------------------------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-dontwarn kotlinx.coroutines.**

# -----------------------------------------------------------------------------------
# Google Play Billing
# -----------------------------------------------------------------------------------
-keep class com.android.billingclient.** { *; }
-dontwarn com.android.billingclient.**

# -----------------------------------------------------------------------------------
# Coil (Image Loading)
# -----------------------------------------------------------------------------------
-keep class coil.** { *; }
-keep class coil.RealImageLoader
-keepclassmembers class * extends coil.decode.Decoder {
    public <init>(...);
}
-keepclassmembers class * extends coil.fetch.Fetcher {
    public <init>(...);
}
-keepclassmembers class * extends coil.transition.Transition {
    public <init>(...);
}

# -----------------------------------------------------------------------------------
# NumGrid Models
# -----------------------------------------------------------------------------------
# Ensure your data models are not obfuscated to avoid issues with Room or Moshi
-keepclassmembers class com.jn.numgrid.domain.** { *; }
