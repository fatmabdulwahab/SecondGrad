package com.example.secondgrad.screens.scoffold

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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

data class BusRoute(
    val sector: String,
    val lineCode: String,
    val from: String,
    val to: String,
    val stations: String,
    val duration: String,
    val price: String,
    val status: String
)

private fun sampleBusRoutes(): java.util.ArrayList<BusRoute> {
    val routes = java.util.ArrayList<BusRoute>()
    routes.add(BusRoute("القطامية", "M5", "التجمع الخامس", "التحرير", "شارع التسعين - محور المشير - العباسية - رمسيس", "90 دقيقة", "EGP 40", "نشط"))
    routes.add(BusRoute("القطامية", "M8", "التجمع الخامس", "جامعة القاهرة", "شارع التسعين - الدائري - مصر القديمة", "90 دقيقة", "EGP 40", "نشط"))
    routes.add(BusRoute("القطامية", "M9", "التجمع الخامس", "ميدان الاتحاد بالمعادي", "شارع التسعين - الدائري - صقر قريش", "90 دقيقة", "EGP 40", "نشط"))
    routes.add(BusRoute("أكتوبر", "M10", "وادي دجلة", "التحرير", "الهرم - محور 26 يوليو - الزمالك", "90 دقيقة", "EGP 40", "نشط"))
    routes.add(BusRoute("أكتوبر", "M21", "ليلة القدر", "التحرير", "فودافون - حدائق الأهرام - الهرم - جامعة القاهرة", "100 دقيقة", "EGP 40", "نشط"))
    routes.add(BusRoute("زايد", "M20", "زايد", "جامعة القاهرة", "حدائق الأهرام - شارع فيصل", "50 دقيقة", "EGP 35", "نشط"))
    routes.add(BusRoute("زايد", "M22", "حدائق أكتوبر", "التجمع الأول", "حدائق الأهرام - فيصل", "90 دقيقة", "EGP 30", "نشط"))
    routes.add(BusRoute("القاهرة الجديدة", "NC1", "القطامية", "التجمع الثالث", "زايدالها - الرحاب - الجامعة الأمريكية", "50 دقيقة", "EGP 22", "نشط"))
    routes.add(BusRoute("القاهرة الجديدة", "NC2", "القطامية", "اللوتس", "زايدالها - الرحاب - الجامعة الأمريكية", "50 دقيقة", "EGP 22", "نشط"))
    routes.add(BusRoute("الشروق", "NS5", "محطة المشير", "طيبة مول", "مدينتي - رمسيس", "75 دقيقة", "EGP 35", "نشط"))
    routes.add(BusRoute("الشروق", "NS7", "محطة المشير", "سرايا القبة", "طريق السويس - ميدان الساعة - رمسيس", "75 دقيقة", "EGP 35", "نشط"))
    routes.add(BusRoute("الشروق", "NS9", "محطة المشير", "محطة الغاز", "طريق السويس - الرحاب", "75 دقيقة", "EGP 35", "نشط"))
    routes.add(BusRoute("العاشر من رمضان", "NA13", "الأردنية", "الحي الثالث", "لا توجد محطات", "90 دقيقة", "EGP 13", "نشط"))
    routes.add(BusRoute("العاشر من رمضان", "NA15", "الأردنية", "الحي الثاني عشر", "لا توجد محطات", "90 دقيقة", "EGP 13", "نشط"))
    routes.add(BusRoute("العاشر من رمضان", "NA18", "الأردنية", "سلطان عويس", "لا توجد محطات", "90 دقيقة", "EGP 13", "نشط"))
    routes.add(BusRoute("العاشر من رمضان", "NA19", "الأردنية", "الجامعة الأهلية", "لا توجد محطات", "90 دقيقة", "EGP 13", "نشط"))
    routes.add(BusRoute("العبور", "AN3", "العبور 2", "سنترال الياسمين", "مدرسة الحرية - ماركت التيسير", "25 دقيقة", "EGP 9", "نشط"))
    routes.add(BusRoute("العبور", "AN4", "كافور", "زهراء مدينة نصر", "إيمون - محور السادات", "30 دقيقة", "EGP 12", "نشط"))
    routes.add(BusRoute("مجتمعات غرب", "Z4", "زايد", "المهندسين", "ميدان جهينة - محور مصر", "60 دقيقة", "EGP 15", "نشط"))
    return routes
}

private fun routeCells(route: BusRoute): java.util.ArrayList<String> {
    val cells = java.util.ArrayList<String>()
    cells.add(route.sector)
    cells.add(route.lineCode)
    cells.add(route.from)
    cells.add(route.to)
    cells.add(route.stations)
    cells.add(route.duration)
    cells.add(route.price)
    cells.add(route.status)
    return cells
}

private fun tableHeaderCells(): java.util.ArrayList<String> {
    val cells = java.util.ArrayList<String>()
    cells.add("القطاع")
    cells.add("الخط")
    cells.add("من")
    cells.add("إلى")
    cells.add("المحطات")
    cells.add("المدة")
    cells.add("السعر")
    cells.add("الحالة")
    return cells
}

@Composable
fun BusRoutesScreen() {
    val scrollState = rememberScrollState()
    val routes = sampleBusRoutes()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E8B57))
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 50.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .horizontalScroll(scrollState)
            ) {
                Text(
                    text = "خطوط السير المتاحة",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E8B57),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 12.dp)
                )

                TableRow(
                    cells = tableHeaderCells(),
                    isHeader = true
                )

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(routes) { route ->
                        TableRow(
                            cells = routeCells(route)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TableRow(cells: java.util.ArrayList<String>, isHeader: Boolean = false) {
    Row(
        modifier = Modifier
            .width(1400.dp)
            .background(if (isHeader) Color.White else Color.Transparent)
    ) {
        var index = 0
        while (index < cells.size) {
            val cell = cells[index]

            val cellWidth = when (index) {
                0 -> 140.dp
                1 -> 80.dp
                2 -> 180.dp
                3 -> 180.dp
                4 -> 400.dp
                5 -> 120.dp
                6 -> 120.dp
                7 -> 100.dp
                else -> 120.dp
            }

            Box(
                modifier = Modifier
                    .width(cellWidth)
                    .then(
                        if (isHeader) {
                            Modifier
                        } else {
                            Modifier.border(1.dp, Color(0xFFA0E0BC))
                        }
                    )
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cell,
                    fontSize = if (isHeader) 16.sp else 14.sp,
                    fontWeight = when {
                        isHeader -> FontWeight.ExtraBold
                        index == 1 || index == 6 || index == 7 -> FontWeight.Bold
                        else -> FontWeight.Normal
                    },
                    color = when {
                        isHeader -> Color.Black
                        index == 1 -> Color(0xFFFF9800)
                        index == 6 || index == 7 -> Color(0xFF2E8B57)
                        else -> Color.Black
                    },
                    maxLines = 1
                )
            }

            index = index + 1
        }
    }
}
