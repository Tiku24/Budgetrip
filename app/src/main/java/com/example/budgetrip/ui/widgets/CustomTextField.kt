package com.example.budgetrip.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    colors: TextFieldColors = TextFieldDefaults.colors(),
) {
    OutlinedTextField(
        shape = RoundedCornerShape(12.dp),
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = modifier,
        singleLine = singleLine,
        maxLines = maxLines,
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon,
        isError = isError,
        readOnly = readOnly,
        enabled = enabled,
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun TransparentOutlinedTextField(value: String,onValueChange: (String) -> Unit) {

    Row (
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(text = "Tax", color = MaterialTheme.colorScheme.scrim,style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .width(90.dp)
                .height(56.dp),
            // 1. Align the text to the end (right side) of the TextField.
            textStyle = TextStyle(
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onPrimary
            ),
            placeholder = {
                Text(
                    fontSize = 12.sp,
                    text = "Enter Tax",
                    // The placeholder should also be aligned to the end.
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            },
            // 2. Make the TextField transparent and customize colors for visibility.
            colors = OutlinedTextFieldDefaults.colors(
                // --- Container ---
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,

                // --- Text and Label ---
                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,

                // --- Border ---
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,

                // --- Placeholder ---
                unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
                focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
            ),
            singleLine = true
        )
    }
}