package com.example.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

/** أدوار المستخدمين */
object Roles {
    const val STUDENT = "student"
    const val PROFESSOR = "professor"
}

/** حالة الجلسة */
sealed class AuthState {
    object Loading : AuthState()
    object SignedOut : AuthState()
    data class SignedIn(val uid: String, val email: String, val role: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    var state by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    var busy by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        val current = auth.currentUser
        if (current == null) {
            state = AuthState.SignedOut
        } else {
            loadRoleAndSignIn(current.uid, current.email ?: "")
        }
    }

    fun clearError() { errorMessage = null }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "يرجى إدخال البريد وكلمة المرور"
            return
        }
        busy = true; errorMessage = null
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val u = result.user!!
                loadRoleAndSignIn(u.uid, u.email ?: email.trim())
            }
            .addOnFailureListener { e ->
                busy = false
                errorMessage = mapError(e.message)
            }
    }

    fun signUp(email: String, password: String) {
        if (email.isBlank() || password.length < 6) {
            errorMessage = "البريد مطلوب وكلمة المرور لا تقلّ عن 6 أحرف"
            return
        }
        busy = true; errorMessage = null
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val u = result.user!!
                // إنشاء وثيقة المستخدم بدور "طالب" افتراضيًا
                val data = mapOf("email" to (u.email ?: email.trim()), "role" to Roles.STUDENT)
                db.collection("users").document(u.uid).set(data)
                    .addOnCompleteListener {
                        loadRoleAndSignIn(u.uid, u.email ?: email.trim())
                    }
            }
            .addOnFailureListener { e ->
                busy = false
                errorMessage = mapError(e.message)
            }
    }

    fun signOut() {
        auth.signOut()
        state = AuthState.SignedOut
    }

    private fun loadRoleAndSignIn(uid: String, email: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role") ?: Roles.STUDENT
                if (!doc.exists()) {
                    db.collection("users").document(uid)
                        .set(mapOf("email" to email, "role" to Roles.STUDENT))
                }
                busy = false
                state = AuthState.SignedIn(uid, email, role)
            }
            .addOnFailureListener {
                // حتى لو فشل جلب الدور، نُدخله كطالب
                busy = false
                state = AuthState.SignedIn(uid, email, Roles.STUDENT)
            }
    }

    private fun mapError(msg: String?): String = when {
        msg == null -> "حدث خطأ، حاول مجددًا"
        msg.contains("password is invalid", true) ||
            msg.contains("credential is incorrect", true) -> "كلمة المرور غير صحيحة"
        msg.contains("no user record", true) -> "لا يوجد حساب بهذا البريد"
        msg.contains("already in use", true) -> "هذا البريد مستعمل بالفعل"
        msg.contains("badly formatted", true) -> "صيغة البريد غير صحيحة"
        msg.contains("network", true) -> "تحقّق من اتصال الإنترنت"
        else -> "تعذّر إتمام العملية: $msg"
    }
}
