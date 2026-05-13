package com.shaalevikas.app.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shaalevikas.app.model.AlumniUser
import com.shaalevikas.app.model.NeedItem
import com.shaalevikas.app.model.Pledge
import com.shaalevikas.app.repository.FirebaseRepository
import com.shaalevikas.app.utils.SessionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ─── UI States ────────────────────────────────────────────────────────────────

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class AlumniSuccess(val user: AlumniUser) : AuthState()
    object AdminSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}

data class AdminStats(
    val totalNeeds: Int = 0,
    val totalRaised: Long = 0L,
    val pledgeCount: Long = 0L
)

// ─── ViewModel ────────────────────────────────────────────────────────────────

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repo    = FirebaseRepository()
    private val session = SessionManager(application)

    // Session
    val sessionRole = session.role.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val sessionUid  = session.uid.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val sessionName = session.name.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val sessionCity = session.city.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    // Auth
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // Needs
    val activeNeeds: StateFlow<List<NeedItem>> = repo.observeActiveNeeds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNeeds: StateFlow<List<NeedItem>> = repo.observeAllNeeds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedNeeds: StateFlow<List<NeedItem>> = repo.observeCompletedNeeds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Donors
    val donors: StateFlow<List<AlumniUser>> = repo.observeDonors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin stats
    val adminStats: StateFlow<AdminStats> = allNeeds.map { needs ->
        AdminStats(
            totalNeeds  = needs.size,
            totalRaised = needs.sumOf { it.raisedAmount },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminStats())

    // Toast / snackbar messages
    private val _message = MutableSharedFlow<String>()
    val message: SharedFlow<String> = _message

    // ─── AUTH ─────────────────────────────────────────────────────────────

    fun loginAlumni(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repo.loginAlumni(email, password)
                .onSuccess { user ->
                    session.saveAlumni(user.uid, user.name, user.city)
                    _authState.value = AuthState.AlumniSuccess(user)
                }
                .onFailure { _authState.value = AuthState.Error(it.message ?: "Login failed") }
        }
    }

    fun registerAlumni(email: String, password: String, user: AlumniUser) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repo.registerAlumni(email, password, user)
                .onSuccess { saved ->
                    session.saveAlumni(saved.uid, saved.name, saved.city)
                    _authState.value = AuthState.AlumniSuccess(saved)
                }
                .onFailure { _authState.value = AuthState.Error(it.message ?: "Registration failed") }
        }
    }

    fun loginAdmin(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repo.loginAdmin(email, password)
                .onSuccess { uid ->
                    session.saveAdmin(uid)
                    _authState.value = AuthState.AdminSuccess
                }
                .onFailure { _authState.value = AuthState.Error(it.message ?: "Admin login failed") }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repo.signOut()
            session.clear()
            _authState.value = AuthState.Idle
        }
    }

    fun resetAuthState() { _authState.value = AuthState.Idle }

    // ─── NEEDS ────────────────────────────────────────────────────────────

    fun addNeed(need: NeedItem) {
        viewModelScope.launch {
            repo.addNeed(need)
                .onSuccess { _message.emit("Need added successfully!") }
                .onFailure { _message.emit("Error: ${it.message}") }
        }
    }

    fun updateNeed(needId: String, updates: Map<String, Any?>) {
        viewModelScope.launch {
            repo.updateNeed(needId, updates)
                .onSuccess { _message.emit("Need updated!") }
                .onFailure { _message.emit("Update failed: ${it.message}") }
        }
    }

    fun deleteNeed(needId: String) {
        viewModelScope.launch {
            repo.deleteNeed(needId)
                .onSuccess { _message.emit("Need deleted") }
                .onFailure { _message.emit("Delete failed: ${it.message}") }
        }
    }

    fun markComplete(needId: String, completed: Boolean) {
        viewModelScope.launch {
            repo.markNeedComplete(needId, completed)
                .onSuccess {
                    _message.emit(if (completed) "Marked as complete!" else "Reopened")
                }
        }
    }

    fun uploadPhoto(context: Context, needId: String, tag: String, uri: Uri, onDone: (String) -> Unit) {
        viewModelScope.launch {
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes() ?: return@launch
            repo.uploadPhoto(needId, tag, bytes)
                .onSuccess { url ->
                    val field = if (tag == "after") "afterPhotoUrl" else "photoUrl"
                    repo.updateNeed(needId, mapOf(field to url))
                    onDone(url)
                    _message.emit("Photo uploaded!")
                }
                .onFailure { _message.emit("Upload failed: ${it.message}") }
        }
    }

    // ─── PLEDGES ──────────────────────────────────────────────────────────

    fun observePledgesForNeed(needId: String): Flow<List<Pledge>> =
        repo.observePledgesForNeed(needId)

    fun observeNeed(needId: String): Flow<NeedItem?> =
        repo.observeNeed(needId)

    fun submitPledge(need: NeedItem, amount: Long) {
        viewModelScope.launch {
            val pledge = Pledge(
                needId     = need.id,
                needTitle  = need.title,
                alumniUid  = sessionUid.value,
                alumniName = sessionName.value,
                alumniCity = sessionCity.value,
                amount     = amount
            )
            repo.submitPledge(pledge, need.raisedAmount)
                .onSuccess { _message.emit("Thank you for pledging ₹$amount! 🙏") }
                .onFailure { _message.emit("Pledge failed: ${it.message}") }
        }
    }

    suspend fun hasPledged(needId: String): Boolean =
        repo.hasPledged(needId, sessionUid.value)
}
