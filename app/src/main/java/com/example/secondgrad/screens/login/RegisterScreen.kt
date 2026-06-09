package com.example.secondgrad.screens.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondgrad.AuthViewModel
import com.example.secondgrad.R

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val authMessage by viewModel.authMessage.collectAsState()
    val registerSuccess by viewModel.registerSuccess.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // States للرسائل
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf("") }
    var countryError by remember { mutableStateOf("") }
    var addressError by remember { mutableStateOf("") }

    val primaryGreen = Color(0xFF2E8B57)

    LaunchedEffect(authMessage) {
        val message = authMessage
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            viewModel.clearRegisterSuccess()
            navController.navigate("home") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryGreen),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bus_logo2),
                    contentDescription = "Logo",
                    modifier = Modifier.height(110.dp).fillMaxWidth()
                )

                Text(text = "انشاء حساب", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = primaryGreen)
                Spacer(modifier = Modifier.height(15.dp))

                // حقل الإيميل (يتعامل مع النوعين)
                CustomInputField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it; emailError = "" },
                    color = primaryGreen,
                    errorMessage = emailError,
                    isEmailField = true // ميزة خاصة للإيميل
                )

                // بقية الحقول
                CustomInputField(label = "Password", value = password, onValueChange = { password = it; passwordError = "" }, color = primaryGreen, isPassword = true, errorMessage = passwordError)
                CustomInputField(label = "Name", value = name, onValueChange = { name = it; nameError = "" }, color = primaryGreen, errorMessage = nameError)
                CustomInputField(label = "Country", value = country, onValueChange = { country = it; countryError = "" }, color = primaryGreen, errorMessage = countryError)
                CustomInputField(label = "Address", value = address, onValueChange = { address = it; addressError = "" }, color = primaryGreen, errorMessage = addressError)

                Spacer(modifier = Modifier.height(25.dp))

                Button(
                    onClick = {
                        // التحقق من الحقول
                        emailError = when {
                            !hasRegisterText(email) -> "email is required"
                            !hasRegisterChar(email, '@') -> "please include an '@' in the email address"
                            else -> ""
                        }
                        passwordError = if (!hasRegisterText(password)) "Password is required"
                        else if (password.length < 6) "password must be at least 6 characters" else ""
                        nameError = if (!hasRegisterText(name)) "name is required" else ""
                        countryError = if (!hasRegisterText(country)) "country is required" else ""
                        addressError = if (!hasRegisterText(address)) "address is required" else ""

                        if (emailError.length == 0 && passwordError.length == 0 && nameError.length == 0 && countryError.length == 0 && addressError.length == 0) {
                            viewModel.signUp(
                                email = email.trim(),
                                password = password,
                                fullName = name.trim(),
                                country = country.trim(),
                                address = address.trim()
                            )
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                    modifier = Modifier.fillMaxWidth(0.7f).height(50.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Submit", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))
                Text(text = "I have an account", color = primaryGreen, modifier = Modifier.clickable { navController.navigate("login") })
            }
        }
    }
}
@Composable
fun CustomInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    color: Color,
    isPassword: Boolean = false,
    errorMessage: String = "",
    isEmailField: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = label, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            isError = errorMessage.length > 0,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color,
                unfocusedBorderColor = color.copy(alpha = 0.3f),
                errorBorderColor = if (isRequiredMessage(errorMessage)) color else Color.Red
            )
        )

        if (errorMessage.length > 0) {
            // تحديد اللون والشكل بناءً على نوع الخطأ
            val isRequiredError = isRequiredMessage(errorMessage)

            // لو الخطأ "required" نستخدم الأخضر الشفاف، لو خطأ إيميل نستخدم الأسود الشفاف
            val backgroundColor = if (isRequiredError) color.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.7f)
            val textColor = if (isRequiredError) color else Color.White
            val iconTint = if (isRequiredError) color else Color.Yellow

            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isRequiredError) Icons.Default.Warning else Icons.Default.Warning,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = errorMessage,
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun hasRegisterText(value: String): Boolean {
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

private fun hasRegisterChar(value: String, target: Char): Boolean {
    var index = 0
    while (index < value.length) {
        if (value[index] == target) {
            return true
        }
        index++
    }
    return false
}

private fun isRequiredMessage(value: String): Boolean {
    return hasWordAt(value, "required") || hasWordAt(value, "Required")
}

private fun hasWordAt(value: String, word: String): Boolean {
    if (word.length == 0 || value.length < word.length) {
        return false
    }

    var start = 0
    while (start <= value.length - word.length) {
        var offset = 0
        var matched = true

        while (offset < word.length) {
            if (value[start + offset] != word[offset]) {
                matched = false
                break
            }
            offset++
        }

        if (matched) {
            return true
        }
        start++
    }

    return false
}
/*
@Composable
fun RegisterScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background_green)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.bus_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "Join us and start your journey",
            fontSize = 16.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    isError = nameError.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (nameError.isNotEmpty()) {
                    Text(nameError, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    isError = emailError.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (emailError.isNotEmpty()) {
                    Text(emailError, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passwordError.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (passwordError.isNotEmpty()) {
                    Text(passwordError, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Create Account Button
                Button(
                    onClick = {

                        nameError = if (!hasRegisterText(name)) "Name is required" else ""
                        emailError = if (!hasRegisterText(email)) "Email is required" else ""
                        passwordError = if (password.length < 6) "Min 6 characters" else ""

                        val isValid = nameError.length == 0
                                && emailError.length == 0
                                && passwordError.length == 0

                        if (isValid) {
                            navController.navigate("home") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults
                        .buttonColors(Color(0xFF00B55D)),

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp)

                ) {
                    Text("Create Account", color = Color.White)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Go to Login
                Row {
                    Text("Already have an account? ")

                    Text(
                        text = "Sign In",
                        color = Color(0xFF00B55D),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

 */