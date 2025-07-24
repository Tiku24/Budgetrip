package com.example.budgetrip.ui.features.textextraction

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.budgetrip.data.model.ReceiptItem
import com.example.budgetrip.ui.widgets.ConcentricCircleLoader
import com.example.budgetrip.ui.widgets.SlowCircularProgressIndicator
import kotlinx.coroutines.flow.collectLatest


@Composable
fun TextExtractScreen(modifier: Modifier,vm: TextExtractViewModel= hiltViewModel()) {

    LaunchedEffect(true) {
        vm.event.collectLatest {
            when (it) {
                is TextExtractEvent.showErrorMessage -> {

                }
            }
        }
    }
    val state = vm.state.collectAsStateWithLifecycle()
    when (state.value) {
        is TextExtractState.Idle ->{
            CameraCaptureWithButton(viewModel = vm,modifier = modifier)
        }

        is TextExtractState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SlowCircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    strokeWidth = 15.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    speedMultiplier = 0.25f // slow it down
                )
                Spacer(modifier = Modifier.size(50.dp))
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        ConcentricCircleLoader()
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Wait...",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = "While AI processing your receipt",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        is TextExtractState.Success -> {
            val data = ((state.value) as TextExtractState.Success).data
            val person = remember { mutableStateOf(1) }
            LazyColumn(modifier.padding(horizontal = 10.dp)) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Split bill arrangement",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                        Text(text = "Item Name", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("Quantity",color = MaterialTheme.colorScheme.onPrimary,fontWeight = FontWeight.Bold)
                        Text("Price",color = MaterialTheme.colorScheme.onPrimary,fontWeight = FontWeight.Bold)
                    }
                }

                items(data) {
                    ShowText(it)
                }

                item {
                    Column {
                        val total = data.sumOf { it.price.toDouble() * it.quantity }
                        val splitBill = remember { mutableStateOf(0.0) }
                        Text(text = "Total: $total", color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(top = 10.dp).fillMaxWidth(), textAlign = TextAlign.End, fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = person.value.toString(), onValueChange = {
                            person.value = it.toInt()
                        })
                        Button(onClick = {
                            splitBill.value = total / person.value
                        }) {
                            Text(text = "Split")
                        }
                        Text(text = if (splitBill.value.equals(0.0)) "" else "Each person should pay: ${splitBill.value}", color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(top = 10.dp).fillMaxWidth(), textAlign = TextAlign.End, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        is TextExtractState.Error -> {
            val errorMessage = (state.value as TextExtractState.Error).message

        }
    }
}

@Composable
fun CameraCaptureWithButton(viewModel: TextExtractViewModel,modifier: Modifier) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 📷 Camera launcher (returns Bitmap)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            capturedBitmap = it
            viewModel.getTextFormImages(it)
        }
    }

    // 🖼️ Gallery picker (returns Uri -> Bitmap)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
            capturedBitmap = bitmap
            viewModel.getTextFormImages(bitmap)
        }
    }
    ScanUploadCard(
        onUploadClick = {
            galleryLauncher.launch("image/*")
        },
        onScanClick = {
            cameraLauncher.launch(null)
        }
    )
}


@Composable
fun ShowText(item: ReceiptItem) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(30.dp),
        ) {
            Text(text = item.item_name, color = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "${ item.quantity }", color = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier.width(5.dp))
            Text(text = item.price, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ScanUploadCard(onUploadClick: () -> Unit,onScanClick: () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                // The top bar contains the close button and the title.
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets(top = 0.dp),
                    title = {
                        Text(
                            "Read Receipt",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { /* Handle close action */ }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ActionButton(
                    icon = Icons.Outlined.QrCodeScanner,
                    text = "Scan",
                    onClick = onScanClick
                )
                Spacer(modifier = Modifier.height(40.dp))
                ActionButton(
                    icon = Icons.Outlined.Receipt,
                    text = "Upload",
                    onClick = onUploadClick
                )
            }
        }
    }
}


@Composable
fun ActionButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // The circular background for the icon.
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(80.dp).clickable(onClick = onClick)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(35.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        // The text label below the button.
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
