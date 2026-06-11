package com.example.materials

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Material(
    val id: String = "",
    val title: String = "",
    val fileName: String = "",
    val url: String = "",
    val uploaderEmail: String = ""
)

class MaterialsViewModel(app: Application) : AndroidViewModel(app) {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    val items = mutableStateListOf<Material>()
    var loading by mutableStateOf(true); private set
    var uploading by mutableStateOf(false); private set
    var uploadProgress by mutableStateOf(0); private set
    var message by mutableStateOf<String?>(null)

    init { listenForMaterials() }

    fun clearMessage() { message = null }

    private fun listenForMaterials() {
        db.collection("materials")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                loading = false
                if (error != null) { message = "تعذّر تحميل المواد"; return@addSnapshotListener }
                if (snapshot == null) return@addSnapshotListener
                items.clear()
                for (doc in snapshot.documents) {
                    items.add(
                        Material(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            fileName = doc.getString("fileName") ?: "",
                            url = doc.getString("url") ?: "",
                            uploaderEmail = doc.getString("uploaderEmail") ?: ""
                        )
                    )
                }
            }
    }

    /** يرفع ملفًا اختاره الأستاذ من الجهاز ثم يسجّله في قائمة المواد */
    fun upload(uri: Uri, title: String, uploaderEmail: String) {
        uploading = true; uploadProgress = 0; message = null
        viewModelScope.launch {
            try {
                val resolver = getApplication<Application>().contentResolver
                val name = queryDisplayName(uri) ?: "ملف"
                val bytes = withContext(Dispatchers.IO) {
                    resolver.openInputStream(uri)?.use { it.readBytes() }
                } ?: throw IllegalStateException("تعذّر قراءة الملف")

                val safe = name.replace(Regex("[^A-Za-z0-9._-]"), "_")
                val ref = storage.reference.child("materials/${System.currentTimeMillis()}_$safe")

                ref.putBytes(bytes)
                    .addOnProgressListener { snap ->
                        val total = snap.totalByteCount.coerceAtLeast(1)
                        uploadProgress = ((100 * snap.bytesTransferred) / total).toInt()
                    }
                    .continueWithTask { task ->
                        if (!task.isSuccessful) throw task.exception ?: Exception("فشل الرفع")
                        ref.downloadUrl
                    }
                    .addOnSuccessListener { downloadUri ->
                        val data = hashMapOf(
                            "title" to (title.ifBlank { name }),
                            "fileName" to name,
                            "url" to downloadUri.toString(),
                            "uploaderEmail" to uploaderEmail,
                            "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                        )
                        db.collection("materials").add(data)
                            .addOnSuccessListener {
                                uploading = false; message = "تم رفع الملف بنجاح"
                            }
                            .addOnFailureListener {
                                uploading = false; message = "رُفع الملف لكن تعذّر تسجيله"
                            }
                    }
                    .addOnFailureListener {
                        uploading = false; message = "فشل رفع الملف"
                    }
            } catch (e: Exception) {
                uploading = false; message = e.message ?: "فشل الرفع"
            }
        }
    }

    private fun queryDisplayName(uri: Uri): String? {
        val resolver = getApplication<Application>().contentResolver
        resolver.query(uri, null, null, null, null)?.use { c ->
            val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx >= 0 && c.moveToFirst()) return c.getString(idx)
        }
        return null
    }
}
