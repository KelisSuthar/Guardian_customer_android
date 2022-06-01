package com.app.guardian.model.FirebaseUser

data class FirebaseUserData(
    var email: String = "",
    var full_name: String = "",
    var id: String = "",
    var isOnline: String = "",
    var keyId: String = "1",
    var lastSeen: String = "",
    var role: String = ""
)