package com.shaalevikas.app.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.shaalevikas.app.model.AlumniUser
import com.shaalevikas.app.model.NeedItem
import com.shaalevikas.app.model.Pledge
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {

    private val auth    = Firebase.auth
    private val db      = Firebase.database.reference
    private val storage = Firebase.storage.reference

    // ─── AUTH ─────────────────────────────────────────────────────────────

    suspend fun loginAlumni(email: String, password: String): Result<AlumniUser> = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val uid = result.user!!.uid
        val snap = db.child("alumni").child(uid).get().await()
        snap.getValue<AlumniUser>() ?: error("Alumni profile not found")
    }

    suspend fun registerAlumni(
        email: String, password: String, user: AlumniUser
    ): Result<AlumniUser> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user!!.uid
        val newUser = user.copy(uid = uid)
        db.child("alumni").child(uid).setValue(newUser).await()
        newUser
    }

    suspend fun loginAdmin(email: String, password: String): Result<String> = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val uid = result.user!!.uid
        val snap = db.child("admins").child(uid).get().await()
        if (snap.getValue<Boolean>() == true) uid
        else error("Not an admin account")
    }

    fun signOut() = auth.signOut()

    fun currentUid() = auth.currentUser?.uid

    // ─── NEEDS ────────────────────────────────────────────────────────────

    fun observeActiveNeeds(): Flow<List<NeedItem>> = callbackFlow {
        val listener = db.child("needs").orderByChild("completed").equalTo(false)
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snap: com.google.firebase.database.DataSnapshot) {
                    val list = snap.children.mapNotNull {
                        it.getValue<NeedItem>()?.copy(id = it.key ?: "")
                    }.sortedBy { it.priorityOrder() }
                    trySend(list)
                }
                override fun onCancelled(e: com.google.firebase.database.DatabaseError) {
                    close(e.toException())
                }
            })
        awaitClose { db.child("needs").removeEventListener(listener) }
    }

    fun observeAllNeeds(): Flow<List<NeedItem>> = callbackFlow {
        val listener = db.child("needs")
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snap: com.google.firebase.database.DataSnapshot) {
                    val list = snap.children.mapNotNull {
                        it.getValue<NeedItem>()?.copy(id = it.key ?: "")
                    }.sortedWith(compareBy({ it.completed }, { it.priorityOrder() }))
                    trySend(list)
                }
                override fun onCancelled(e: com.google.firebase.database.DatabaseError) {
                    close(e.toException())
                }
            })
        awaitClose { db.child("needs").removeEventListener(listener) }
    }

    fun observeCompletedNeeds(): Flow<List<NeedItem>> = callbackFlow {
        val listener = db.child("needs").orderByChild("completed").equalTo(true)
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snap: com.google.firebase.database.DataSnapshot) {
                    val list = snap.children.mapNotNull {
                        it.getValue<NeedItem>()?.copy(id = it.key ?: "")
                    }
                    trySend(list)
                }
                override fun onCancelled(e: com.google.firebase.database.DatabaseError) {
                    close(e.toException())
                }
            })
        awaitClose { db.child("needs").removeEventListener(listener) }
    }

    fun observeNeed(needId: String): Flow<NeedItem?> = callbackFlow {
        val listener = db.child("needs").child(needId)
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snap: com.google.firebase.database.DataSnapshot) {
                    trySend(snap.getValue<NeedItem>()?.copy(id = snap.key ?: ""))
                }
                override fun onCancelled(e: com.google.firebase.database.DatabaseError) {
                    close(e.toException())
                }
            })
        awaitClose { db.child("needs").child(needId).removeEventListener(listener) }
    }

    suspend fun addNeed(need: NeedItem): Result<Unit> = runCatching {
        val key = db.child("needs").push().key ?: error("Key generation failed")
        db.child("needs").child(key).setValue(need.copy(id = key)).await()
    }

    suspend fun updateNeed(needId: String, updates: Map<String, Any?>): Result<Unit> = runCatching {
        db.child("needs").child(needId).updateChildren(updates).await()
    }

    suspend fun deleteNeed(needId: String): Result<Unit> = runCatching {
        db.child("needs").child(needId).removeValue().await()
    }

    suspend fun markNeedComplete(needId: String, completed: Boolean): Result<Unit> = runCatching {
        db.child("needs").child(needId).child("completed").setValue(completed).await()
    }

    suspend fun uploadPhoto(needId: String, tag: String, bytes: ByteArray): Result<String> = runCatching {
        val ref = storage.child("need_photos/${tag}_$needId.jpg")
        ref.putBytes(bytes).await()
        ref.downloadUrl.await().toString()
    }

    // ─── PLEDGES ──────────────────────────────────────────────────────────

    fun observePledgesForNeed(needId: String): Flow<List<Pledge>> = callbackFlow {
        val listener = db.child("pledges").orderByChild("needId").equalTo(needId)
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snap: com.google.firebase.database.DataSnapshot) {
                    val list = snap.children.mapNotNull {
                        it.getValue<Pledge>()?.copy(id = it.key ?: "")
                    }
                    trySend(list)
                }
                override fun onCancelled(e: com.google.firebase.database.DatabaseError) {
                    close(e.toException())
                }
            })
        awaitClose { db.child("pledges").removeEventListener(listener) }
    }

    suspend fun hasPledged(needId: String, alumniUid: String): Boolean {
        val snap = db.child("pledges").orderByChild("alumniUid").equalTo(alumniUid).get().await()
        return snap.children.any { it.child("needId").getValue<String>() == needId }
    }

    suspend fun submitPledge(pledge: Pledge, currentRaised: Long): Result<Unit> = runCatching {
        val key = db.child("pledges").push().key ?: error("Key fail")
        db.child("pledges").child(key).setValue(pledge.copy(id = key)).await()
        db.child("needs").child(pledge.needId).child("raisedAmount")
            .setValue(currentRaised + pledge.amount).await()
        db.child("alumni").child(pledge.alumniUid).child("pledgeCount")
            .setValue(com.google.firebase.database.ServerValue.increment(1))
        db.child("alumni").child(pledge.alumniUid).child("totalPledged")
            .setValue(com.google.firebase.database.ServerValue.increment(pledge.amount))
    }

    // ─── DONORS ───────────────────────────────────────────────────────────

    fun observeDonors(): Flow<List<AlumniUser>> = callbackFlow {
        val listener = db.child("alumni").orderByChild("pledgeCount")
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snap: com.google.firebase.database.DataSnapshot) {
                    val list = snap.children.mapNotNull { it.getValue<AlumniUser>() }
                        .filter { it.pledgeCount > 0 }
                        .sortedByDescending { it.totalPledged }
                    trySend(list)
                }
                override fun onCancelled(e: com.google.firebase.database.DatabaseError) {
                    close(e.toException())
                }
            })
        awaitClose { db.child("alumni").removeEventListener(listener) }
    }

    // ─── STATS ────────────────────────────────────────────────────────────

    suspend fun getTotalPledgeCount(): Long {
        return db.child("pledges").get().await().childrenCount
    }
}
