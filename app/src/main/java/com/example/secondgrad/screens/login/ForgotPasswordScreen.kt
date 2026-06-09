package com.example.secondgrad.screens.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondgrad.AuthViewModel
import com.example.secondgrad.R
import com.example.secondgrad.hasInputText
import com.example.secondgrad.indexOfChar
import com.example.secondgrad.trimInputText

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val primaryGreen = Color(0xFF2E8B57)

    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val authMessage by viewModel.authMessage.collectAsState()
    val resetCodeSent by viewModel.resetCodeSent.collectAsState()
    val resetCodeConfirmed by viewModel.resetCodeConfirmed.collectAsState()
    val resetCompleted by viewModel.resetCompleted.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.clearResetState()
    }

    LaunchedEffect(authMessage) {
        val message = authMessage
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(resetCompleted) {
        if (resetCompleted) {
            viewModel.clearResetCompleted()
            navController.navigate("login") {
                popUpTo("forgot_password") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryGreen)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState()),
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
                Image(
                    painter = painterResource(id = R.drawable.bus_logo2),
                    contentDescription = "Logo",
                    modifier = Modifier.size(90.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "استعادة كلمة المرور",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryGreen,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = when {
                        !resetCodeSent -> "اكتبي إيميلك عشان نبعتلك كود التأكيد"
                        !resetCodeConfirmed -> "اكتبي كود التأكيد اللي وصلك على الإيميل"
                        else -> "اكتبي كلمة المرور الجديدة"
                    },
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    enabled = !resetCodeSent && !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                )

                if (resetCodeSent) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("كود التأكيد") },
                        enabled = !resetCodeConfirmed && !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    )
                }

                if (resetCodeConfirmed) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("كلمة المرور الجديدة") },
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("تأكيد كلمة المرور") },
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val trimmedEmail = trimInputText(email)
                        when {
                            !resetCodeSent -> {
                                if (!hasInputText(trimmedEmail) || indexOfChar(trimmedEmail, '@') < 0) {
                                    Toast.makeText(context, "اكتبي إيميل صحيح", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.sendResetCode(trimmedEmail)
                                }
                            }
                            !resetCodeConfirmed -> {
                                if (!hasInputText(code)) {
                                    Toast.makeText(context, "اكتبي كود التأكيد", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.confirmResetCode(trimmedEmail, trimInputText(code))
                                }
                            }
                            else -> {
                                if (!hasInputText(password) || password.length < 6) {
                                    Toast.makeText(context, "كلمة المرور لازم تكون 6 أحرف على الأقل", Toast.LENGTH_SHORT).show()
                                } else if (password != confirmPassword) {
                                    Toast.makeText(context, "كلمة المرور غير متطابقة", Toast.LENGTH_SHORT).show()
                                } else {
                                    viewModel.resetPassword(trimmedEmail, password, confirmPassword)
                                }
                            }
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                    modifier = Modifier.fillMaxWidth(0.8f),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = when {
                                !resetCodeSent -> "إرسال الكود"
                                !resetCodeConfirmed -> "تأكيد الكود"
                                else -> "تغيير كلمة المرور"
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = { navController.popBackStack() }) {
                    Text("رجوع لتسجيل الدخول", color = primaryGreen, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

