package com.example.secondgrad.screens.scoffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
@Preview(showSystemUi = true)
fun ContactUs() {

    val green = Color(0xFF2E7D32)
    val lightGreen = Color(0xFF28A745)
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }
    var tripDateTime by remember { mutableStateOf("") }
    var driverComment by remember { mutableStateOf("") }

    var selectedRating by remember { mutableStateOf("") }
    var selectedReason by remember { mutableStateOf("") }

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {

        val rtlTextStyle = TextStyle(textAlign = TextAlign.End)

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

                    // Helper function to create bold labels
                    @Composable
                    fun BoldLabel(text: String) {
                        Text(
                            text = text,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    BoldLabel("الاسم بالكامل")
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },

                        placeholder = { Text("الاسم الثلاثي") },

                        textStyle = rtlTextStyle.copy(color = green),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = green,
                            unfocusedBorderColor = green,

                            cursorColor = green,

                            focusedLabelColor = green,
                            unfocusedLabelColor = green,


                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),

                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BoldLabel("البريد الإلكتروني")
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("example@mail.com") },
                        textStyle = rtlTextStyle,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = green,
                            unfocusedBorderColor = green,

                            cursorColor = green,

                            focusedLabelColor = green,
                            unfocusedLabelColor = green,


                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BoldLabel("رقم الموبايل")
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        placeholder = { Text("01234567890") },
                        textStyle = rtlTextStyle,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = green,
                            unfocusedBorderColor = green,

                            cursorColor = green,

                            focusedLabelColor = green,
                            unfocusedLabelColor = green,


                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BoldLabel("تقييم الرحلة (من 1 إلى 5)")
                    CustomDropdown(
                        label = "اختر تقييمك",
                        options = listOf("ممتازة ⭐⭐⭐⭐⭐", "جيدة ⭐⭐⭐⭐", "متوسطة ⭐⭐⭐", "سيئة ⭐⭐", "ضعيفة ⭐"),
                        selectedOption = selectedRating,
                        onOptionSelected = { selectedRating = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BoldLabel("سبب التواصل / التقييم")
                    CustomDropdown(
                        label = "اختر سبب التقييم",
                        options = listOf( "تقييم الرحلة", "استفسار", "شكوى","اقتراح"),
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
                                "mm/dd/yyyy ",
                                textAlign = TextAlign.Start
                            )
                        },

                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Calendar"
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = green,
                            unfocusedBorderColor = green,

                            cursorColor = green,

                            focusedLabelColor = green,
                            unfocusedLabelColor = green,


                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),

                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BoldLabel("تفاصيل التقييم / الرسالة")
                    OutlinedTextField(
                        value = comments,
                        onValueChange = { comments = it },
                        placeholder = { Text("اكتب ملاحظاتك...") },
                        textStyle = rtlTextStyle,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = green,
                            unfocusedBorderColor = green,

                            cursorColor = green,

                            focusedLabelColor = green,
                            unfocusedLabelColor = green,


                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BoldLabel("تعليق إضافي عن السائق أو المواصلات (اختياري)")
                    OutlinedTextField(
                        value = driverComment,
                        onValueChange = { driverComment = it },
                        placeholder = { Text("مثال: السائق محترم والمواصلات نظيفة") },
                        textStyle = rtlTextStyle,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = green,
                            unfocusedBorderColor = green,

                            cursorColor = green,

                            focusedLabelColor = green,
                            unfocusedLabelColor = green,


                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { /* API Call */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("إرسال التقييم", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.wrapContentWidth()
        ) {
            OutlinedTextField(
                value = if (selectedOption.isEmpty()) label else selectedOption,
                onValueChange = {},
                readOnly = true,
                leadingIcon = {
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
                    unfocusedBorderColor = Color(0xFF28A745)
                ),
                modifier = Modifier
                    .widthIn(min = 200.dp)
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.wrapContentWidth()
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}