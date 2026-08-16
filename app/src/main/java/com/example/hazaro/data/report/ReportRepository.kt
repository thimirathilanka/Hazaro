package com.example.hazaro.data.report

import com.example.hazaro.data.model.DisasterType
import com.example.hazaro.data.model.Report
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReportRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    private val reports = firestore.collection(COLLECTION)

    fun observeReports(): Flow<Result<List<Report>>> = callbackFlow {
        val registration = reports.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Result.failure(error))
                return@addSnapshotListener
            }
            val items = snapshot?.documents?.mapNotNull { doc ->
                runCatching { doc.toReport() }.getOrNull()
            }?.sortedByDescending { it.createdAt } ?: emptyList()
            trySend(Result.success(items))
        }
        awaitClose { registration.remove() }
    }

    suspend fun addReport(
        type: DisasterType,
        description: String,
        photoUrl: String,
        lat: Double,
        lng: Double,
        reporterId: String,
        reporterEmail: String,
    ) {
        val data = hashMapOf(
            "type" to type.id,
            "description" to description.trim(),
            "photoUrl" to photoUrl,
            "lat" to lat,
            "lng" to lng,
            "createdAt" to FieldValue.serverTimestamp(),
            "reporterId" to reporterId,
            "reporterEmail" to reporterEmail,
        )
        reports.add(data).await()
    }

    private companion object {
        const val COLLECTION = "reports"
    }
}

private fun com.google.firebase.firestore.DocumentSnapshot.toReport(): Report? {
    val type = DisasterType.fromId(getString("type").orEmpty())
    val description = getString("description").orEmpty()
    val photoUrl = getString("photoUrl").orEmpty()
    val lat = getDouble("lat") ?: return null
    val lng = getDouble("lng") ?: return null
    val createdAt = when (val raw = get("createdAt")) {
        is Timestamp -> raw.toDate().time
        is Number -> raw.toLong()
        else -> 0L
    }
    return Report(
        id = id,
        type = type,
        description = description,
        photoUrl = photoUrl,
        lat = lat,
        lng = lng,
        createdAt = createdAt,
        reporterId = getString("reporterId").orEmpty(),
        reporterEmail = getString("reporterEmail").orEmpty(),
    )
}
