package com.example.secondgrad.screens.scoffold

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class FaqItem(
    val question: String,
    val answer: String
)

private fun sampleFaqItems(): java.util.ArrayList<FaqItem> {
    val items = java.util.ArrayList<FaqItem>()
    items.add(
        FaqItem(
            question = "هل لازم أكون عامل حساب أو تسجيل دخول؟",
            answer = "لا، تقدري تبحثي عن الطرق وتستخدمي البحث بالصوت أو الكتابة بدون حساب. التسجيل بس بيخليكي تحفظي سجل رحلاتك وتستخدمي كل المميزات."
        )
    )
    items.add(
        FaqItem(
            question = "هل النظام بيدعم مناطق معينة بس؟",
            answer = "حالياً بنركز على مناطق القاهرة الكبرى وخطوط السير الرئيسية، وبنوسّع التغطية باستمرار لمناطق أكتر."
        )
    )
    items.add(
        FaqItem(
            question = "هل أقدر أشوف تفاصيل عن الرحلة؟",
            answer = "أيوه، بعد البحث هتلاقي تفاصيل الخط ونوع المواصلة والمدة التقريبية والمحطات اللي بتمشي عليها الرحلة."
        )
    )
    items.add(
        FaqItem(
            question = "هل البيانات دقيقة؟",
            answer = "بنعتمد على بيانات محدثة من مصادر رسمية ومن مجتمع المستخدمين، لكن ممكن يحصل تغيير في المواعيد فننصحك تتأكدي قبل ما تتحركي."
        )
    )
    items.add(
        FaqItem(
            question = "طيب ايه الحاجة اللى بتميزنا؟",
            answer = "البحث بالصوت ولغة الإشارة، والبحث بالعربي بسهولة، وعرض أكتر من مسار في نفس الوقت عشان تختاري الأنسب ليكي."
        )
    )
    items.add(
        FaqItem(
            question = "طيب ايه انواع المواصلات المتاحة؟",
            answer = "أتوبيسات النقل العام، الميني باص، والمترو حسب الخط المتاح في نتيجة البحث."
        )
    )
    return items
}

@Composable
fun CommonQuestionScreen() {
    val faqItems = sampleFaqItems()
    val primaryGreen = Color(0xFF00875A)
    var expandedIndex by remember { mutableIntStateOf(-1) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = primaryGreen)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "اكثر الاسئلة الشائعة؟",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
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
                    var itemIndex = 0
                    while (itemIndex < faqItems.size) {
                        val currentIndex = itemIndex
                        val faqItem = faqItems[currentIndex]

                        FaqItemRow(
                            faqItem = faqItem,
                            primaryGreen = primaryGreen,
                            isExpanded = expandedIndex == currentIndex,
                            onToggle = {
                                expandedIndex = if (expandedIndex == currentIndex) {
                                    -1
                                } else {
                                    currentIndex
                                }
                            }
                        )

                        if (currentIndex < faqItems.size - 1) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color.Black)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        itemIndex = itemIndex + 1
                    }
                }
            }
        }
    }
}

@Composable
private fun FaqItemRow(
    faqItem: FaqItem,
    primaryGreen: Color,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
    ) {
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
                    text = faqItem.question,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Right
                )
            }

            Text(
                text = if (isExpanded) "−" else "+",
                color = primaryGreen,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clickable(onClick = onToggle)
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = faqItem.answer,
                color = Color(0xFF1F2937),
                fontSize = 14.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF3F4F6),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            )
        }
    }
}
