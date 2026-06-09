package com.example.secondgrad.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilterChipDefaults.filterChipColors
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.common.util.CollectionUtils.listOf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(

    selected: String,

    onSelectedChange: (String) -> Unit

) {

    val filters = listOf(
        "باص",
        "مترو",
        "الكل"
    )

    Row(

        horizontalArrangement = Arrangement.spacedBy(10.dp),

        modifier = Modifier
            .background(
                Color.White,
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)

    ) {

        filters.forEach { item ->

            FilterChip(

                selected = selected == item,

                onClick = {
                    onSelectedChange(item)
                },

                label = {

                    Text(
                        text = item
                    )
                },

                colors = filterChipColors(

                    selectedContainerColor = Color(0xFF22C55E),

                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

