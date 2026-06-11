package com.example.materials

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialsScreen(isProfessor: Boolean, uploaderEmail: String) {
    val vm: MaterialsViewModel = viewModel()
    val context = LocalContext.current
    var pendingUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var titleInput by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) { pendingUri = uri; titleInput = ""; showDialog = true }
    }

    // رسائل النظام
    vm.message?.let { msg ->
        LaunchedEffect(msg) { }
        Snackbar(modifier = Modifier.padding(8.dp),
            action = { TextButton(onClick = { vm.clearMessage() }) { Text("حسنًا") } }) {
            Text(msg)
        }
    }

    Scaffold(
        floatingActionButton = {
            if (isProfessor) {
                ExtendedFloatingActionButton(
                    onClick = { picker.launch("*/*") },
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("رفع ملف") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            Text(
                "المواد التعليمية",
                fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(16.dp)
            )

            if (vm.uploading) {
                LinearProgressIndicator(
                    progress = { vm.uploadProgress / 100f },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
                Text("جارٍ الرفع… ${vm.uploadProgress}%",
                    fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }

            when {
                vm.loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                vm.items.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(
                        if (isProfessor) "لا توجد موادّ بعد. اضغط «رفع ملف» لإضافة أول مادة."
                        else "لا توجد موادّ منشورة بعد.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(vm.items) { m ->
                        MaterialCard(m) {
                            if (m.url.isNotBlank()) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, m.url.toUri()))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog && pendingUri != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("عنوان المادة") },
            text = {
                OutlinedTextField(
                    value = titleInput,
                    onValueChange = { titleInput = it },
                    label = { Text("اكتب عنوانًا (اختياري)") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.upload(pendingUri!!, titleInput, uploaderEmail)
                    showDialog = false
                }) { Text("رفع") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
private fun MaterialCard(m: Material, onOpen: () -> Unit) {
    Card(
        onClick = onOpen,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Description, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(m.title.ifBlank { m.fileName },
                    fontWeight = FontWeight.Bold, fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface)
                if (m.uploaderEmail.isNotBlank()) {
                    Text(m.uploaderEmail, fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
            Text("فتح", color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
