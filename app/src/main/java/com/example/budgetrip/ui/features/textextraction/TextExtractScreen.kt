package com.example.budgetrip.ui.features.textextraction

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.budgetrip.data.model.ReceiptItem
import com.example.budgetrip.ui.widgets.ConcentricCircleLoader
import com.example.budgetrip.ui.widgets.SlowCircularProgressIndicator
import com.example.budgetrip.ui.widgets.TransparentOutlinedTextField
import kotlinx.coroutines.flow.collectLatest


@Composable
fun TextExtractScreen(navController: NavController,modifier: Modifier,vm: TextExtractViewModel= hiltViewModel()) {

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
            val permission = listOf(Manifest.permission.CAMERA)
            val isGranted = vm.isGranted.collectAsStateWithLifecycle()

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                // Update state in ViewModel based on granted status
                val allGranted = permissions.values.all { it }
                vm.setPermissionGranted(allGranted)
            }

            LaunchedEffect(Unit) {
                if (!isGranted.value) {
                    permissionLauncher.launch(permission.toTypedArray())
                }
            }

            if (isGranted.value){
                CameraCaptureWithButton(vm,navController)
            }
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

            BillSplitterScreen(data = data, modifier = modifier)
        }

        is TextExtractState.Error -> {
            val errorMessage = (state.value as TextExtractState.Error).message

        }
    }
}

@Composable
fun CameraCaptureWithButton(viewModel: TextExtractViewModel,navController: NavController) {
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
        },
        navController = navController
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ScanUploadCard(onUploadClick: () -> Unit,onScanClick: () -> Unit,navController: NavController) {
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
                        IconButton(onClick = { navController.popBackStack() }) {
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
            modifier = Modifier
                .size(80.dp)
                .clickable(onClick = onClick)
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



@Composable
fun BillSplitterScreen(data: List<ReceiptItem>,modifier: Modifier) {
    val enterTax = remember { mutableStateOf<String?>("") }
    var peopleCount by remember { mutableStateOf<String?>("1") }
    val subTotal = data.sumOf { it.price.toDouble() * it.quantity }
//    val subtotal = items.sumOf { it.price * it.quantity }
    var total = subTotal + (enterTax.value?.toDoubleOrNull() ?: 0.0) // example 10% tax
    val splitBill = remember { mutableStateOf(0.0) }


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Bill Splitter",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        data.forEach { item ->
            BillItemRow(item)
        }
        HorizontalDivider()
        Text(
            "Summary",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp,bottom = 8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        SummaryRow("Subtotal", subTotal)
        TransparentOutlinedTextField(value = enterTax.value.toString(), onValueChange = { enterTax.value = it })
        SummaryRow("Total", total)

        Text(
            "Split Money",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )

        OutlinedTextField(
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = peopleCount!!,
            onValueChange = { peopleCount = it },
            placeholder = { Text("Number of People", color = MaterialTheme.colorScheme.scrim) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimary),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.onBackground,
                unfocusedContainerColor = MaterialTheme.colorScheme.onBackground,
                focusedBorderColor = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor = MaterialTheme.colorScheme.surface
            )
        )

        SummaryRow("Amount Per Person", splitBill.value)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Button(
                onClick = {
                    splitBill.value = total / peopleCount!!.toInt()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text("Calculate Split", color = Color(0xFF0D181C), fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun BillItemRow(item: ReceiptItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(item.item_name, color = MaterialTheme.colorScheme.onPrimary)
            Text(item.price, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.scrim)
        }
        Text("${item.quantity}", modifier = Modifier.width(24.dp), textAlign = TextAlign.Center,color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun SummaryRow(label: String, value: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.scrim)
        Text("%.2f".format(value), fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimary)
    }
}