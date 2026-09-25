package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AuthRepository(private val userDao: UserDao) {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser = _currentUser.asStateFlow()

    sealed class AuthResult {
        data class Success(val user: UserEntity) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }

    suspend fun login(email: String, password: String): AuthResult {
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isEmpty()) {
            return AuthResult.Error("Please enter your email address.")
        }
        if (!isValidEmail(trimmedEmail)) {
            return AuthResult.Error("Please enter a valid email address.")
        }
        if (password.isEmpty()) {
            return AuthResult.Error("Please enter your password.")
        }

        val user = userDao.getUserByEmail(trimmedEmail)
        if (user == null) {
            // Generic security message: do not expose whether email exists
            return AuthResult.Error("Invalid email or password.")
        }

        val computedHash = AppDatabase.hashPassword(password, user.salt)
        if (computedHash != user.passwordHash) {
            return AuthResult.Error("Invalid email or password.")
        }

        _currentUser.value = user
        return AuthResult.Success(user)
    }

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        termsAccepted: Boolean
    ): AuthResult {
        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim().lowercase()

        if (trimmedName.isEmpty()) {
            return AuthResult.Error("Please enter your full name.")
        }
        if (trimmedEmail.isEmpty()) {
            return AuthResult.Error("Please enter your email address.")
        }
        if (!isValidEmail(trimmedEmail)) {
            return AuthResult.Error("Please enter a valid email address.")
        }

        val passwordError = validatePasswordStrength(password)
        if (passwordError != null) {
            return AuthResult.Error(passwordError)
        }

        if (password != confirmPassword) {
            return AuthResult.Error("Passwords do not match.")
        }

        if (!termsAccepted) {
            return AuthResult.Error("You must accept the Terms & Conditions and Medical Research Disclaimer to proceed.")
        }

        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return AuthResult.Error("An account with this email address already exists.")
        }

        val salt = UUID.randomUUID().toString()
        val hash = AppDatabase.hashPassword(password, salt)
        val newUser = UserEntity(
            fullName = trimmedName,
            email = trimmedEmail,
            passwordHash = hash,
            salt = salt
        )

        val insertedId = userDao.insertUser(newUser)
        val createdUser = newUser.copy(id = insertedId)
        _currentUser.value = createdUser
        return AuthResult.Success(createdUser)
    }

    fun requestPasswordReset(email: String): String {
        val trimmed = email.trim().lowercase()
        if (trimmed.isEmpty()) {
            return "Please enter your email address."
        }
        if (!isValidEmail(trimmed)) {
            return "Please enter a valid email address."
        }
        // Generic response required per spec:
        return "If the email is associated with an account, password reset instructions will be sent."
    }

    suspend fun changePassword(oldPass: String, newPass: String): Boolean {
        val current = _currentUser.value ?: return false
        val oldHash = AppDatabase.hashPassword(oldPass, current.salt)
        if (oldHash != current.passwordHash) return false

        val passErr = validatePasswordStrength(newPass)
        if (passErr != null) return false

        val newSalt = UUID.randomUUID().toString()
        val newHash = AppDatabase.hashPassword(newPass, newSalt)
        val updated = current.copy(passwordHash = newHash, salt = newSalt)
        userDao.updateUser(updated)
        _currentUser.value = updated
        return true
    }

    fun logout() {
        _currentUser.value = null
    }

    companion object {
        fun isValidEmail(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun validatePasswordStrength(password: String): String? {
            if (password.length < 8) {
                return "Password must be at least 8 characters long."
            }
            if (!password.any { it.isUpperCase() }) {
                return "Password must contain at least one uppercase letter."
            }
            if (!password.any { it.isLowerCase() }) {
                return "Password must contain at least one lowercase letter."
            }
            if (!password.any { it.isDigit() }) {
                return "Password must contain at least one number."
            }
            return null
        }
    }
}
