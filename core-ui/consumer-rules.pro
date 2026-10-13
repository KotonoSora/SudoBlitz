# DataStore (Persistence)
-keep class androidx.datastore.preferences.core.** { *; }
-dontwarn androidx.datastore.**

# Core data + shop-adjacent models
-keep class local.core.ui.data.** { *; }

# Room (run-history database)
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-dontwarn androidx.room.**
