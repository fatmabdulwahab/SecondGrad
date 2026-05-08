package com.example.secondgrad.screens.scoffold

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import com.example.secondgrad.R

@Composable
fun SearchSection(context: Context, lifecycleOwner: LifecycleOwner) {

    var fromText by remember { mutableStateOf("") }
    var toText by remember { mutableStateOf("") }

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
                text = "SEARCH BY VOICE",
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

        VoiceCameraSearch(
            onVoiceResult = { text ->
                println("Voice: $text")
            },
            onOpenCamera = {
                startCamera(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    onFrame = { frame ->
                        sendFrameToServer(frame)
                    }
                )
            }
        )
        Spacer(modifier = Modifier.height(4.dp))

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
                value = fromText,
                onValueChange = { fromText = it },
                label = { Text("From") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.padding(start = 170.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_4),
                contentDescription = "Arrow",
                modifier = Modifier.size(50.dp)
            )
        }

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
                value = toText,
                onValueChange = { toText = it },
                label = { Text("To") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Row الأزرار
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            Button(
                onClick = {
                    // TODO: search action
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EAD60)),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.height(40.dp).width(130.dp).padding(start = 20.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_5),
                    contentDescription = null,
                    modifier = Modifier.size(17.dp).padding(start = 1.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Search", color = Color.White, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = {
                    if (toText.isNotBlank()) {

                        val gmmIntentUri = Uri.parse("geo:0,0?q=$toText")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                        mapIntent.setPackage("com.google.android.apps.maps")

                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EAD60)),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.height(40.dp).width(220.dp).padding(start = 12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_7),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Open in google maps", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // التنبيه على اليمين
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