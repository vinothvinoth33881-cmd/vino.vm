package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val salt: String,
    val role: String = "AI Medical Researcher",
    val institution: String = "Department of Computer Science & AI",
    val createdAt: Long = System.currentTimeMillis()
)
