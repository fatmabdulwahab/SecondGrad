package com.example.secondgrad.screens.login



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.secondgrad.R

@Composable
fun RegisterScreen(navController: NavController) {
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
                            email.isBlank() -> "email is required"
                            !email.contains("@") -> "please include an '@' in the email address"
                            else -> ""
                        }
                        passwordError = if (password.isBlank()) "Password is required"
                        else if (password.length < 5 || !password[0].isUpperCase()) "password is invalid ex: Ahmed123 " else ""
                        nameError = if (name.isBlank()) "name is required" else ""
                        countryError = if (country.isBlank()) "country is required" else ""
                        addressError = if (address.isBlank()) "address is required" else ""

                        if (emailError.isEmpty() && passwordError.isEmpty() && nameError.isEmpty() && countryError.isEmpty() && addressError.isEmpty()) {
                            navController.navigate("home")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                    modifier = Modifier.fillMaxWidth(0.7f).height(50.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Submit", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
            isError = errorMessage.isNotEmpty(),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = color,
                unfocusedBorderColor = color.copy(alpha = 0.3f),
                errorBorderColor = if (errorMessage.contains("required")) color else Color.Red
            )
        )

        if (errorMessage.isNotEmpty()) {
            // تحديد اللون والشكل بناءً على نوع الخطأ
            val isRequiredError = errorMessage.contains("required", ignoreCase = true)

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

                        nameError = if (name.isBlank()) "Name is required" else ""
                        emailError = if (email.isBlank()) "Email is required" else ""
                        passwordError = if (password.length < 6) "Min 6 characters" else ""

                        val isValid = nameError.isEmpty()
                                && emailError.isEmpty()
                                && passwordError.isEmpty()

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