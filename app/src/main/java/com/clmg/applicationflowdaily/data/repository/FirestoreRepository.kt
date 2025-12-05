package com.clmg.applicationflowdaily.data.repository

import com.clmg.applicationflowdaily.data.firestore.FirebaseModule
import com.clmg.applicationflowdaily.data.models.DailySession
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseModule.db
    private val collection = db.collection("sessions")

    suspend fun saveSession(session: DailySession) {
        collection.document(session.id).set(session).await()
    }

    suspend fun getSessions(): List<DailySession> {
        val snapshot = collection.get().await()
        return snapshot.documents.mapNotNull { it.toObject(DailySession::class.java) }
    }
}