package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AnalysisDao
import com.example.data.local.dao.ModelMetadataDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.AnalysisEntity
import com.example.data.local.entity.ModelMetadataEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

@Database(
    entities = [
        UserEntity::class,
        AnalysisEntity::class,
        ModelMetadataEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun analysisDao(): AnalysisDao
    abstract fun modelMetadataDao(): ModelMetadataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kidney_ai_diagnosis.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial research model metadata and initial verified academic account
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            database.modelMetadataDao().insertOrUpdate(
                                ModelMetadataEntity()
                            )
                            // Initial research account for instant evaluation
                            val salt = UUID.randomUUID().toString()
                            val initialPasswordHash = hashPassword("KidneyAI@2026!", salt)
                            database.userDao().insertUser(
                                UserEntity(
                                    fullName = "Dr. Vinoth Kumar",
                                    email = "vinothvinoth33881@gmail.com",
                                    passwordHash = initialPasswordHash,
                                    salt = salt,
                                    role = "Lead AI Medical Researcher",
                                    institution = "AI & Data Science Research Lab"
                                )
                            )
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        fun hashPassword(password: String, salt: String): String {
            val md = MessageDigest.getInstance("SHA-256")
            val bytes = md.digest((password + salt).toByteArray(Charsets.UTF_8))
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}
