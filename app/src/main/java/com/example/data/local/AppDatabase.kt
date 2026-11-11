package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.MusicDao
import com.example.data.local.entity.FavoriteTrackEntity
import com.example.data.local.entity.FollowedArtistEntity
import com.example.data.local.entity.PlaylistEntity
import com.example.data.local.entity.PlaylistTrackEntity
import com.example.data.local.entity.RecentlyPlayedEntity
import com.example.data.local.entity.UserProfileEntity

@Database(
    entities = [
        FavoriteTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class,
        RecentlyPlayedEntity::class,
        FollowedArtistEntity::class,
        UserProfileEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fr_music_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
