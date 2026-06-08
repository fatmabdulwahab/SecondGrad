package com.example.secondgrad.screens.scoffold

import android.R.color.black
import android.hardware.camera2.params.BlackLevelPattern
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.secondgrad.R

@Composable
fun CommonQuestionScreen() {

    val questions = listOf(
        "هل لازم أكون عامل حساب أو تسجيل دخول؟",
        "هل النظام بيدعم مناطق معينة بس؟",
        "هل أقدر أشوف تفاصيل عن الرحلة؟",
        "هل البيانات دقيقة؟",
        "طيب ايه الحاجة اللى بتميزنا؟",
        "طيب ايه انواع المواصلات المتاحة؟"
    )

    // اللون المطلوب
    val primaryGreen = Color(0xFF00875A)
    val lightGray = Color(0xFFE2E8F0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF00875A))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // إضافة تمرير في حال كانت الشاشة طويلة
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // العنوان
        Text(
            text = "اكثر الاسئلة الشائعة؟",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 50.dp, bottom = 8.dp)
        )

        // فاصل أصفر
        Box(
            modifier = Modifier
                .width(155.dp)
                .height(2.dp)
                .background(Color.Yellow)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                questions.forEachIndexed { index, question ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // مربع السؤال الأخضر
                        Box(
                            modifier = Modifier
                                .background(color = primaryGreen, shape = RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = question,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        // أيقونة الزائد
                        Text(
                            text = "+",
                            color = primaryGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    // خط فاصل بين الأسئلة
                    if (index < questions.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Black)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}