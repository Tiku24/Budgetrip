package com.example.budgetrip.ui.navigation

import kotlinx.serialization.Serializable

interface Routes

@Serializable
object Home: Routes

@Serializable
object TextExtractScreen: Routes

@Serializable
object UploadScanImage: Routes