# Sudoku UI (screens, VMs, adapters)
-keep class local.sudoku.ui.** { *; }
-keep class local.sudoku.engine.** { *; }
# Room (via core-ui) — keep here too in case UI references entities
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
