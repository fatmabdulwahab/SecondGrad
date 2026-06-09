package com.example.secondgrad.screens.scoffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private fun sampleQuestions(): java.util.ArrayList<String> {
    val questions = java.util.ArrayList<String>()
    questions.add("هل لازم أكون عامل حساب أو تسجيل دخول؟")
    questions.add("هل النظام بيدعم مناطق معينة بس؟")
    questions.add("هل أقدر أشوف تفاصيل عن الرحلة؟")
    questions.add("هل البيانات دقيقة؟")
    questions.add("طيب ايه الحاجة اللى بتميزنا؟")
    questions.add("طيب ايه انواع المواصلات المتاحة؟")
    return questions
}

@Composable
fun CommonQuestionScreen() {
    val questions = sampleQuestions()
    val primaryGreen = Color(0xFF00875A)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF00875A))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "اكثر الاسئلة الشائعة؟",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 50.dp, bottom = 8.dp)
        )

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
                var questionIndex = 0
                while (questionIndex < questions.size) {
                    val question = questions[questionIndex]

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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

                        Text(
                            text = "+",
                            color = primaryGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    if (questionIndex < questions.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.Black)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    questionIndex = questionIndex + 1
                }
            }
        }
    }
}
