package com.example.secondgrad.screens.scoffold

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.secondgrad.FeedbackRequest
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

private const val FEEDBACK_API_URL = "https://transguideapi.runasp.net/api/UserFeedbacks/SubmitFeedack"

@Composable
fun ContactUs() {
    val context = LocalContext.current
    val green = Color(0xFF2E7D32)

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }
    var tripDateTime by remember { mutableStateOf("") }
    var driverComment by remember { mutableStateOf("") }

    var selectedRating by remember { mutableStateOf("") }
    var selectedReason by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var submitTrigger by remember { mutableIntStateOf(0) }
    var pendingRequest by remember { mutableStateOf<FeedbackRequest?>(null) }

    val arabicTextStyle = TextStyle(textAlign = TextAlign.End)
    val ratingOptions = remember { buildRatingOptions() }
    val reasonOptions = remember { buildReasonOptions() }

    LaunchedEffect(submitTrigger) {
        val request = pendingRequest
        if (submitTrigger == 0 || request == null) {
            return@LaunchedEffect
        }

        isSending = true
        val resultMessage = submitFeedbackToApi(request)
        isSending = false
        pendingRequest = null

        Toast.makeText(context, resultMessage, Toast.LENGTH_LONG).show()

        if (resultMessage.startsWith("تم إرسال")) {
            name = ""
            email = ""
            phone = ""
            comments = ""
            tripDateTime = ""
            driverComment = ""
            selectedRating = ""
            selectedReason = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E7D32))
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp)
                .verticalScroll(rememberScrollState()),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "تقييم خدمة النقل / تواصل معنا",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "رأيك يهمنا، دعنا نعرف كيف كانت رحلتك!",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                BoldLabel("الاسم بالكامل")
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = {
                        Text(
                            text = "الاسم الثلاثي",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    textStyle = arabicTextStyle.copy(color = green),
                    colors = feedbackFieldColors(green),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                BoldLabel("البريد الإلكتروني")
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text(
                            text = "example@mail.com",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    textStyle = arabicTextStyle,
                    colors = feedbackFieldColors(green),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                BoldLabel("رقم الموبايل")
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = {
                        Text(
                            text = "01234567890",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    textStyle = arabicTextStyle,
                    colors = feedbackFieldColors(green),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                BoldLabel("تقييم الرحلة (من 1 إلى 5)")
                CustomDropdown(
                    label = "اختر تقييمك",
                    options = ratingOptions,
                    selectedOption = selectedRating,
                    onOptionSelected = { selectedRating = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                BoldLabel("سبب التواصل / التقييم")
                CustomDropdown(
                    label = "اختر سبب التقييم",
                    options = reasonOptions,
                    selectedOption = selectedReason,
                    onOptionSelected = { selectedReason = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                BoldLabel("وقت وتاريخ الرحلة")
                OutlinedTextField(
                    value = tripDateTime,
                    onValueChange = { tripDateTime = it },
                    placeholder = {
                        Text(
                            text = "mm/dd/yyyy",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Calendar"
                        )
                    },
                    textStyle = arabicTextStyle,
                    colors = feedbackFieldColors(green),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                BoldLabel("تفاصيل التقييم / الرسالة")
                OutlinedTextField(
                    value = comments,
                    onValueChange = { comments = it },
                    placeholder = {
                        Text(
                            text = "اكتب ملاحظاتك...",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    textStyle = arabicTextStyle,
                    colors = feedbackFieldColors(green),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                BoldLabel("تعليق إضافي عن السائق أو المواصلات (اختياري)")
                OutlinedTextField(
                    value = driverComment,
                    onValueChange = { driverComment = it },
                    placeholder = {
                        Text(
                            text = "مثال: السائق محترم والمواصلات نظيفة",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    textStyle = arabicTextStyle,
                    colors = feedbackFieldColors(green),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val validationMessage = validateFeedbackForm(
                            name = name,
                            email = email,
                            selectedRating = selectedRating,
                            selectedReason = selectedReason,
                            comments = comments
                        )

                        if (validationMessage != null) {
                            Toast.makeText(context, validationMessage, Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        pendingRequest = FeedbackRequest(
                            fullName = name.trim(),
                            email = email.trim(),
                            phoneNumber = phone.trim(),
                            reason = selectedReason,
                            timeSlot = if (tripDateTime.trim().length > 0) tripDateTime.trim() else "غير محدد",
                            message = comments.trim(),
                            comment = driverComment.trim(),
                            ratingId = ratingIdFromSelection(selectedRating),
                            userProfileId = 1,
                            routeId = 1,
                            tripStatusId = 1
                        )
                        submitTrigger = submitTrigger + 1
                    },
                    enabled = !isSending,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    if (isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("إرسال التقييم", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun BoldLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.End,
        color = Color.DarkGray,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun feedbackFieldColors(green: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = green,
    unfocusedBorderColor = green,
    cursorColor = green,
    focusedLabelColor = green,
    unfocusedLabelColor = green,
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    label: String,
    options: java.util.ArrayList<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val displayText = if (selectedOption.length == 0) label else selectedOption

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(min = 220.dp)
            ) {
                OutlinedTextField(
                    value = displayText,
                    onValueChange = {},
                    readOnly = true,
                    textStyle = TextStyle(
                        textAlign = TextAlign.Right,
                        color = Color.White
                    ),
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.White
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF28A745),
                        unfocusedContainerColor = Color(0xFF28A745),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF28A745),
                        unfocusedBorderColor = Color(0xFF28A745),
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    var optionIndex = 0
                    while (optionIndex < options.size) {
                        val option = options[optionIndex]
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Right
                                )
                            },
                            onClick = {
                                onOptionSelected(option)
                                expanded = false
                            }
                        )
                        optionIndex = optionIndex + 1
                    }
                }
            }
        }
    }
}

private fun buildRatingOptions(): java.util.ArrayList<String> {
    val options = java.util.ArrayList<String>()
    options.add("ممتازة ⭐⭐⭐⭐⭐")
    options.add("جيدة ⭐⭐⭐⭐")
    options.add("متوسطة ⭐⭐⭐")
    options.add("سيئة ⭐⭐")
    options.add("ضعيفة ⭐")
    return options
}

private fun buildReasonOptions(): java.util.ArrayList<String> {
    val options = java.util.ArrayList<String>()
    options.add("تقييم الرحلة")
    options.add("استفسار")
    options.add("شكوى")
    options.add("اقتراح")
    return options
}

private fun validateFeedbackForm(
    name: String,
    email: String,
    selectedRating: String,
    selectedReason: String,
    comments: String
): String? {
    if (!hasInputText(name)) {
        return "اكتبي الاسم بالكامل"
    }
    if (!hasInputText(email) || email.indexOf("@") < 0) {
        return "اكتبي بريد إلكتروني صحيح"
    }
    if (!hasInputText(selectedRating)) {
        return "اختاري تقييم الرحلة"
    }
    if (!hasInputText(selectedReason)) {
        return "اختاري سبب التقييم"
    }
    if (!hasInputText(comments)) {
        return "اكتبي تفاصيل التقييم أو الرسالة"
    }
    return null
}

private fun hasInputText(value: String): Boolean {
    var index = 0
    while (index < value.length) {
        val char = value[index]
        if (char != ' ' && char != '\n' && char != '\t' && char != '\r') {
            return true
        }
        index = index + 1
    }
    return false
}

private fun ratingIdFromSelection(selectedRating: String): Int {
    if (selectedRating.indexOf("ممتازة") >= 0) {
        return 5
    }
    if (selectedRating.indexOf("جيدة") >= 0) {
        return 4
    }
    if (selectedRating.indexOf("متوسطة") >= 0) {
        return 3
    }
    if (selectedRating.indexOf("سيئة") >= 0) {
        return 2
    }
    return 1
}

private suspend fun submitFeedbackToApi(request: FeedbackRequest): String {
    return withContext(Dispatchers.IO) {
        try {
            val gson = Gson()
            val jsonBody = gson.toJson(request)
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = jsonBody.toRequestBody(mediaType)
            val httpRequest = Request.Builder()
                .url(FEEDBACK_API_URL)
                .post(body)
                .build()

            val client = OkHttpClient()
            val response = client.newCall(httpRequest).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful) {
                "تم إرسال التقييم بنجاح"
            } else {
                if (responseBody.length > 0) {
                    "مش قادرين نرسل التقييم (${response.code}): $responseBody"
                } else {
                    "مش قادرين نرسل التقييم، جرّبي تاني (${response.code})"
                }
            }
        } catch (throwable: Throwable) {
            throwable.localizedMessage ?: "حصل خطأ في الاتصال"
        }
    }
}
