class DsuViewModel : ViewModel() {
    private val _dsuProgress = MutableLiveData<Int>()
    val dsuProgress: LiveData<Int> = _dsuProgress
    
    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorEvent = MutableLiveData<Event<ErrorInfo>>()
    val errorEvent: LiveData<Event<ErrorInfo>> = _errorEvent
    
    private val _autoReboot = MutableLiveData(false)
    val autoReboot: LiveData<Boolean> = _autoReboot
    
    private val _batterySaverEnabled = MutableLiveData(true)
    val batterySaverEnabled: LiveData<Boolean> = _batterySaverEnabled
    
    private val dsuManager = DsuManager()
    private val logger = DsuLogger()
    
    fun loadDsu(uri: Uri) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _status.value = "Checking compatibility..."
                
                if (batterySaverEnabled.value == true && !checkBatteryLevel()) {
                    _errorEvent.value = Event(ErrorInfo(
                        "Low Battery",
                        "Battery level too low. Please charge device or disable battery saver mode.",
                        true
                    ))
                    return@launch
                }
                
                if (!dsuManager.checkCompatibility()) {
                    _errorEvent.value = Event(ErrorInfo(
                        "Compatibility Error",
                        "This DSU is not compatible with your device",
                        false
                    ))
                    return@launch
                }
                
                _status.value = "Loading DSU..."
                dsuManager.loadDsu(uri) { progress ->
                    _dsuProgress.value = progress
                }
                
                _status.value = "DSU loaded successfully"
            } catch (e: Exception) {
                logger.logError(e)
                _errorEvent.value = Event(ErrorInfo(
                    "Loading Error",
                    e.message ?: "Unknown error occurred",
                    true
                ))
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun applyDsu() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _status.value = "Creating backup..."
                createSystemBackup()
                
                _status.value = "Applying DSU..."
                dsuManager.applyDsu { progress ->
                    _dsuProgress.value = progress
                }
                
                _status.value = "DSU applied successfully"
                
                if (autoReboot.value == true) {
                    _status.value = "Rebooting..."
                    dsuManager.rebootDevice()
                }
            } catch (e: Exception) {
                logger.logError(e)
                _errorEvent.value = Event(ErrorInfo(
                    "Application Error",
                    e.message ?: "Unknown error occurred",
                    true
                ))
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private suspend fun checkBatteryLevel(): Boolean {
        return dsuManager.getBatteryLevel() >= 20
    }
    
    private suspend fun createSystemBackup() {
        // Implementation for system backup
    }
    
    fun toggleAutoReboot(enabled: Boolean) {
        _autoReboot.value = enabled
    }
    
    fun toggleBatterySaver(enabled: Boolean) {
        _batterySaverEnabled.value = enabled
    }
    
    data class ErrorInfo(
        val title: String,
        val message: String,
        val canRetry: Boolean
    )
}

class Event<T>(private val content: T) {
    private var hasBeenHandled = false
    
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
    
    fun peekContent(): T = content
} 