package com.clmg.applicationflowdaily.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clmg.applicationflowdaily.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess = _registerSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Nuevos estados para el usuario actual
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _userLoading = MutableStateFlow(false)
    val userLoading: StateFlow<Boolean> = _userLoading.asStateFlow()

    init {
        // Cargar usuario actual si existe sesión activa
        if (isUserLoggedIn()) {
            loadCurrentUser()
        }
    }

    /** -----------------------------------------
     *  🔐 LOGIN CON FIREBASE AUTH
     *  ----------------------------------------- */
    fun login(email: String, password: String) {
        _loading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()

                // Cargar datos del usuario después del login
                loadCurrentUser()

                _loading.value = false
                _loginSuccess.value = true

            } catch (e: Exception) {
                _loading.value = false
                _errorMessage.value = when {
                    e.message?.contains("password is invalid") == true ->
                        "Contraseña incorrecta"
                    e.message?.contains("no user record") == true ->
                        "No existe una cuenta con este email"
                    e.message?.contains("badly formatted") == true ->
                        "Email inválido"
                    else -> e.message ?: "Error desconocido al iniciar sesión"
                }
            }
        }
    }


    /** -----------------------------------------
     *  📝 REGISTRO CON FIREBASE AUTH + FIRESTORE
     *  ----------------------------------------- */
    fun register(name: String, email: String, phone: String, password: String) {
        _loading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // 1. Crear usuario en Firebase Auth
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = authResult.user?.uid ?: throw Exception("No se pudo obtener el ID del usuario")

                // 2. Crear fecha de registro formateada
                val dateFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
                val memberSince = "Miembro desde ${dateFormat.format(Date())}"

                // 3. Guardar datos adicionales en Firestore
                val userData = hashMapOf(
                    "uid" to userId,
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "memberSince" to memberSince,
                    "profileImageUrl" to "",
                    "createdAt" to System.currentTimeMillis()
                )

                firestore.collection("users")
                    .document(userId)
                    .set(userData)
                    .await()

                // 4. Cargar usuario recién creado
                loadCurrentUser()

                _loading.value = false
                _registerSuccess.value = true

            } catch (e: Exception) {
                _loading.value = false
                _errorMessage.value = when {
                    e.message?.contains("email address is already in use") == true ->
                        "Este email ya está registrado"
                    e.message?.contains("password is too short") == true ->
                        "La contraseña debe tener al menos 6 caracteres"
                    e.message?.contains("badly formatted") == true ->
                        "Email inválido"
                    else -> e.message ?: "Error desconocido al registrarse"
                }
            }
        }
    }


    /** -----------------------------------------
     *  👤 CARGAR DATOS DEL USUARIO ACTUAL
     *  ----------------------------------------- */
    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                _userLoading.value = true
                val currentFirebaseUser = auth.currentUser

                if (currentFirebaseUser != null) {
                    val userId = currentFirebaseUser.uid

                    val document = firestore.collection("users")
                        .document(userId)
                        .get()
                        .await()

                    if (document.exists()) {
                        // Convertir documento a objeto User
                        _currentUser.value = User(
                            uid = document.getString("uid") ?: userId,
                            name = document.getString("name") ?: "Usuario",
                            email = document.getString("email") ?: currentFirebaseUser.email ?: "",
                            phone = document.getString("phone") ?: "",
                            memberSince = document.getString("memberSince") ?: "Miembro desde hoy",
                            profileImageUrl = document.getString("profileImageUrl") ?: ""
                        )
                    } else {
                        // Si no existe documento en Firestore, crear uno básico
                        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
                        val memberSince = "Miembro desde ${dateFormat.format(Date())}"

                        val user = User(
                            uid = userId,
                            name = currentFirebaseUser.displayName ?: "Usuario",
                            email = currentFirebaseUser.email ?: "",
                            phone = "",
                            memberSince = memberSince,
                            profileImageUrl = ""
                        )

                        // Guardar en Firestore
                        firestore.collection("users")
                            .document(userId)
                            .set(
                                hashMapOf(
                                    "uid" to user.uid,
                                    "name" to user.name,
                                    "email" to user.email,
                                    "phone" to user.phone,
                                    "memberSince" to user.memberSince,
                                    "profileImageUrl" to user.profileImageUrl,
                                    "createdAt" to System.currentTimeMillis()
                                )
                            )
                            .await()

                        _currentUser.value = user
                    }
                } else {
                    _currentUser.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar usuario: ${e.message}"
                _currentUser.value = null
            } finally {
                _userLoading.value = false
            }
        }
    }


    /** -----------------------------------------
     *  ✏️ ACTUALIZAR PERFIL DEL USUARIO
     *  ----------------------------------------- */
    fun updateUserProfile(name: String, phone: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _errorMessage.value = null

                val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")

                val updates = hashMapOf<String, Any>(
                    "name" to name,
                    "phone" to phone
                )

                firestore.collection("users")
                    .document(userId)
                    .update(updates)
                    .await()

                // Recargar datos del usuario
                loadCurrentUser()

                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _errorMessage.value = "Error al actualizar perfil: ${e.message}"
            }
        }
    }


    /** -----------------------------------------
     *  🔄 Helpers
     *  ----------------------------------------- */
    fun resetLoginSuccess() {
        _loginSuccess.value = false
    }

    fun resetRegisterSuccess() {
        _registerSuccess.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _loginSuccess.value = false
        _registerSuccess.value = false
    }
}