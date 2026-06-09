package com.example.secondgrad.screens.scoffold

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.secondgrad.SignViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
@Composable
fun CameraTranslationDialog(
    activity: ComponentActivity,
    onDismiss: () -> Unit,
    onSaveSuccess: (finalWord: String) -> Unit
) {
    val viewModel: SignViewModel = viewModel()

    val prediction by viewModel.prediction.collectAsState()
    val currentWord by viewModel.currentWord.collectAsState()
    val sessionId by viewModel.sessionId.collectAsState()
    val cameraError by viewModel.errorMessage.collectAsState()
    var cameraUiState by remember { mutableStateOf(CameraUiState.Initial) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun hasHandModelAsset(): Boolean {
        return try {
            val inputStream = context.assets.open("hand_landmarker.task")
            inputStream.close()
            true
        } catch (throwable: Throwable) {
            false
        }
    }

    LaunchedEffect(sessionId) {
        if (sessionId.length > 0) {
            cameraUiState = CameraUiState.Ready
        }
    }

    LaunchedEffect(cameraError) {
        val message = cameraError
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
            cameraUiState = CameraUiState.Initial
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .padding(8.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // البار العلوي
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "ترجمة لغة الإشارة الذكية",
                                color = Color(0xFF0F172A),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(40.dp))
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = "نظام متطور يعتمد على التحليل الفوري للمفاصل",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Right,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp, end = 20.dp).fillMaxWidth()
                        )
                    }
                }

                // صندوق عرض الكاميرا أو حالات التحميل
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF0B141A)),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(targetState = cameraUiState, label = "CameraState") { state ->
                        when (state) {
                            CameraUiState.Initial -> InitialStateView()
                            CameraUiState.CreatingSession -> LoadingStateView("جاري إنشاء جلسة والاتصال بالخادم...")
                            CameraUiState.Ready -> {
                                CameraPreview(
                                    activity = activity,
                                    signViewModel = viewModel,
                                    onError = { message ->
                                        viewModel.setCameraError(message)
                                    }
                                )
                            }
                            else -> LoadingStateView("جاري تشغيل الكاميرا...")
                        }
                    }
                }

                // الـ Layout السفلي الديناميكي
                Box(
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(targetState = cameraUiState, label = "DynamicContentTransition") { state ->
                        if (state == CameraUiState.Ready) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // عرض الحرف الحالي الراجع من الـ API
                                    Text(
                                        text = if (prediction.length == 0) "..." else prediction,
                                        color = Color(0xFF10B981),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "الرمز المكتشف حالياً:", color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Text(text = "الكلمة المترجمة المتراكمة:", color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

                                Spacer(modifier = Modifier.height(6.dp))

                                // صندوق عرض الكلمة المتراكمة (الراجعة من السيرفر)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .border(BorderStroke(1.dp, Color(0xFFE2E8F0)), RoundedCornerShape(12.dp))
                                        .background(Color.White, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (currentWord.length == 0) "ابدأ بتشكيل الكلمة إشارة تلو الأخرى..." else currentWord,
                                        color = if (currentWord.length == 0) Color.LightGray else Color(0xFF0F172A),
                                        fontSize = 15.sp,
                                        fontWeight = if (currentWord.length == 0) FontWeight.Normal else FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // أزرار التحكم المحلية (لو السيرفر مش بيهندلها تلقائي تقدري تحذفي أو تضيفي محلياً)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.resetWord() },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("إعادة ضبط", fontSize = 11.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                        }
                                    }

                                    Button(
                                        onClick = { viewModel.deleteLastCharacter() },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF5F5), contentColor = Color(0xFFEF4444))
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("حذف حرف", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(14.dp))
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.addSpace() },
                                        modifier = Modifier.weight(1.2f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("إضافة مسافة", fontSize = 11.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.SpaceBar, contentDescription = null, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // صف أزرار الحفظ والإلغاء
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    withContext(Dispatchers.IO) {
                                                        viewModel.finishSession()
                                                    }
                                                    cameraUiState = CameraUiState.Initial
                                                } catch (e: Exception) {
                                                    Log.e("CANCEL_SESSION_ERROR", e.message.toString())
                                                    cameraUiState = CameraUiState.Initial
                                                }
                                            }
                                        },
                                        modifier = Modifier.height(50.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF5F5)),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text("إلغاء", color = Color(0xFFEF4444), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    val finalWord = withContext(Dispatchers.IO) {
                                                        viewModel.finishSession()
                                                    }
                                                    onSaveSuccess(finalWord)
                                                } catch (e: Exception) {
                                                    Log.e("END_SESSION_ERROR", e.message.toString())
                                                    Toast.makeText(
                                                        context,
                                                        "مش قادرين نحفظ الجلسة",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(50.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF73C2B3)),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                            Text("حفظ الكلمة وإلغاء الجلسة", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        } else if (state == CameraUiState.Initial) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = onDismiss,
                                        modifier = Modifier.height(52.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text("إلغاء", color = Color(0xFF64748B), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            scope.launch {
                                                if (!hasHandModelAsset()) {
                                                    Toast.makeText(
                                                        context,
                                                        "ملف hand_landmarker.task غير موجود داخل assets",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    cameraUiState = CameraUiState.Initial
                                                    return@launch
                                                }

                                                cameraUiState = CameraUiState.CreatingSession

                                                val isSessionCreated = viewModel.createSessionForCamera()
                                                if (!isSessionCreated) {
                                                    cameraUiState = CameraUiState.Initial
                                                    return@launch
                                                }

                                                cameraUiState = CameraUiState.ConnectingServer
                                                delay(400)

                                                cameraUiState = CameraUiState.StartingCamera
                                                delay(400)

                                                cameraUiState = CameraUiState.Ready
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00875A)),
                                        shape = RoundedCornerShape(14.dp)
                                    ){
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                            Text("بدء ترجمة الإشارات", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(imageVector = Icons.Outlined.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}