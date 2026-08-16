package com.example.hazaro.di

import android.content.Context
import com.example.hazaro.data.auth.AuthRepository
import com.example.hazaro.data.cloudinary.CloudinaryUploader
import com.example.hazaro.data.location.LocationClient
import com.example.hazaro.data.report.ReportRepository

class AppContainer(context: Context) {
    val authRepository = AuthRepository()
    val reportRepository = ReportRepository()
    val cloudinaryUploader = CloudinaryUploader()
    val locationClient = LocationClient(context.applicationContext)
}
