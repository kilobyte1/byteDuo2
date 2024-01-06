package com.example.byteduo.Model

import org.mindrot.jbcrypt.BCrypt

//This part initializes a BCrypt hashing instance with default parameters.
// BCrypt is a widely used and secure password hashing algorithm.

object PasswordHasher {
    fun hashPassword(password: String): String {
        // Use BCrypt library to hash the password
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun checkPassword(plainPassword: String, hashedPassword: String): Boolean {
        // Use BCrypt library to check if the plain password matches the hashed password
        return BCrypt.checkpw(plainPassword, hashedPassword)
    }

}
