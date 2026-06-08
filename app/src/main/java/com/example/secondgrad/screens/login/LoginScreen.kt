package com.example.secondgrad.screens.login


import com.example.secondgrad.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController



/**
 * تصميم السهم الصغير (Triangle) ليظهر أعلى الرسالة السوداء
 */
val TooltipShape = GenericShape { size, _ ->
    val arrowWidth = 30f
    val arrowHeight = 20f
    val startX = 40f // موقع السهم من اليسار

    moveTo(startX, 0f)
    lineTo(startX + arrowWidth / 2, -arrowHeight)
    lineTo(startX + arrowWidth, 0f)
    addRoundRect(
        androidx.compose.ui.geometry.RoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = 20f,
            radiusY = 20f
        )
    )
}

@Composable
fun LoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E8B57))
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(25.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Image(
                    painter = painterResource(id = R.drawable.bus_logo2),
                    contentDescription = "Logo",
                    modifier = Modifier.size(110.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "تسجيل الدخول",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E8B57)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // --- حقل الإيميل ---
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = ""
                    },
                    label = { Text("Email") },
                    isError = emailError.length > 0,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp)
                )

                // --- رسالة الخطأ السوداء (Tooltip) ---
                if (emailError.length > 0) {
                    Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFF333333), // اللون الأسود المطلوب
                                    shape = TooltipShape
                                )
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // المربع البرتقالي الصغير
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .background(Color(0xFFFF8C00), RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("!", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = emailError,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // الرسالة الخضراء الشفافة بالأسفل
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .background(Color(0xFF90EE90), RoundedCornerShape(12.dp))
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Email is invalid",
                                color = Color(0xFF2E8B57),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- حقل كلمة المرور ---
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = ""
                    },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordError.length > 0,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp)
                )

                // --- رسالة خطأ الباسورد (Invalid) باللون الأخضر الفاتح ---
                if (passwordError.length > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(Color(0xFF90EE90), RoundedCornerShape(25.dp)) // خلفية خضراء فاتحة
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = passwordError, // ستظهر Invalid
                            color = Color(0xFF2E8B57), // نص أخضر غامق
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = "Forget Password ?",
                        color = Color(0xFF2E8B57),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                // --- زر الإرسال ---
                Button(
                    onClick = {
                        emailError = when {
                            !hasLoginText(email) -> "email is required"
                            !email.contains("@") -> "Please include '@' in the email."
                            email.endsWith("@") || email.indexOf("@") == email.length - 1 ->
                                "Please enter a part following '@'. '$email' is incomplete."
                            else -> ""
                        }

                        passwordError = when {
                            !hasLoginText(password) -> "password is required"
                            password.length < 5 -> "password is invalid"
                            else -> ""
                        }

                        if (emailError.length == 0 && passwordError.length == 0) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    },

                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E8B57)),
                    modifier = Modifier.fillMaxWidth(.5f),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(text = "Submit", color = Color.White, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(modifier = Modifier.fillMaxWidth(0.8f), thickness = 1.dp)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(0.8f).height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Image(painter = painterResource(id = R.drawable.google), contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تسجيل الدخول باستخدام Google")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "I don't have an account",
                    color = Color(0xFF2E8B57),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.clickable { navController.navigate("register") }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

private fun hasLoginText(value: String): Boolean {
    var index = 0
    while (index < value.length) {
        val char = value[index]
        if (char != ' ' && char != '\n' && char != '\t' && char != '\r') {
            return true
        }
        index++
    }
    return false
}