package com.clmg.applicationflowdaily.data.firestore

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseModule {

    private const val TAG = "FirebaseModule"

    val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance().apply {
            Log.d(TAG, "✅ Firestore inicializado")
        }
    }

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance().apply {
            Log.d(TAG, "✅ Auth inicializado")
        }
    }

    /**
     * Obtiene el ID del usuario actual autenticado
     * @return String con el UID o null si no está autenticado
     */
    fun getCurrentUserId(): String? {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        Log.d(TAG, "=================================")
        Log.d(TAG, "🔑 getCurrentUserId() llamado")
        Log.d(TAG, "🔑 FirebaseAuth.currentUser: $currentUser")
        Log.d(TAG, "🔑 User ID: $userId")
        Log.d(TAG, "🔑 Email: ${currentUser?.email}")
        Log.d(TAG, "🔑 Autenticado: ${userId != null}")
        Log.d(TAG, "=================================")

        return userId
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    fun isUserAuthenticated(): Boolean {
        val isAuth = auth.currentUser != null
        Log.d(TAG, "✅ Usuario autenticado: $isAuth")
        return isAuth
    }

    /**
     * Obtiene el email del usuario actual
     */
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
}