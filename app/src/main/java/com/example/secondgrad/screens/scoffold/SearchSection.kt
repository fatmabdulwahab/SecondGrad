package com.example.secondgrad.screens.scoffold


import android.annotation.SuppressLint
import com.example.secondgrad.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.text.input.KeyboardType

@SuppressLint("ContextCastToActivity")
@Composable
fun SearchSection(context: Context ) {

//
//    val fromText by viewModel.fromText.collectAsState()
//    val toText by viewModel.toText.collectAsState()

    // فقط بنراقب الديالوج يفتح ولا يقفل
    var isCameraActive by remember { mutableStateOf(false) }
    val activity = LocalContext.current as ComponentActivity
    val context = LocalContext.current

    //val viewModel: RouteViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    Column(
        modifier = Modifier
            .fillMaxWidth(0.96f)
            .padding(6.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(15.dp)
            ),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            modifier = Modifier.padding(top = 7.dp)
        ) {
            Text(
                text = "SEARCH BY VOICE & SIGN",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF077C32),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 12.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // استدعاء شريط الصوت والكاميرا وربطه بالـ State
        VoiceCameraSearch(
            onMicClick = { /* سنضيفها لاحقاً */ },
            onCameraGranted = { isCameraActive = true }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // استدعاء الـ Dialog المنفصل هنا ونمرر له الأكشنز
        if (isCameraActive) {
            CameraTranslationDialog(
                activity = activity,
                onDismiss = { isCameraActive = false },
                onSaveSuccess = {
                    // الأكشن لما يضغط حفظ
                    isCameraActive = false
                }
            )
        }

        // حقل نص (From)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.bluecircle),
                contentDescription = "bluecircle",
                modifier = Modifier.size(12.dp)
            )

            OutlinedTextField(
                value =  "",
                onValueChange = {   },

                label = { Text("From") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier.weight(1f)
            )
        }

        // السهم بين الحقلين
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_4),
                contentDescription = "Arrow",
                modifier = Modifier.size(40.dp)
            )
        }

        // حقل نص (To)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.orangecircle),
                contentDescription = "orangecircle",
                modifier = Modifier.size(12.dp)
            )

            OutlinedTextField(
                value =  "",
                onValueChange = {   },
                label = { Text("To") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // الأزرار السفليّة (Search & Google Maps)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {


          //  val viewModel: RouteViewModel = viewModel()
                  Button(
                onClick = {


                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EAD60)),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.height(40.dp).weight(1f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_5),
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Search", color = Color.White, fontSize = 12.sp)
            }

            Button(
                onClick = {
//
//                   val location = toText.ifBlank { fromText }
//
//                    if (location.isNotBlank()) {
//
//                        val intent = Intent(
//                            Intent.ACTION_VIEW,
//                            "geo:0,0?q=${Uri.encode(location)}".toUri()
//                        )
//
//                        context.startActivity(intent)
//                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EAD60)),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.height(40.dp).weight(1.5f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_7),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Open in google maps", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, end = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "تنبيه: يجب كتابة اسم المحطة مع المدينة فى البحث*",
                color = Color.Red,
                fontSize = 12.sp,
                textAlign = TextAlign.Right
            )
        }
    }
}