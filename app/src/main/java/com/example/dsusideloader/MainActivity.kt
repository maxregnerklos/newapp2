class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: DsuViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupTheme()
        setupObservers()
        setupClickListeners()
        setupNotificationChannel()
    }
    
    private fun setupTheme() {
        // Apply theme based on system settings
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Configuration.UI_MODE_NIGHT_NO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
    
    private fun setupObservers() {
        viewModel.dsuProgress.observe(this) { progress ->
            binding.progressBar.progress = progress
            updateProgressUI(progress)
        }
        
        viewModel.status.observe(this) { status ->
            binding.tvStatus.text = status
            updateStatusUI(status)
        }

        viewModel.compatibility.observe(this) { compatibility ->
            handleCompatibilityStatus(compatibility)
        }

        viewModel.backgroundOperation.observe(this) { isRunning ->
            binding.btnLoadDsu.isEnabled = !isRunning
            binding.btnApplyDsu.isEnabled = !isRunning
        }
    }

    private fun updateProgressUI(progress: Int) {
        binding.progressBar.apply {
            isIndeterminate = progress == -1
            if (progress >= 0) {
                setProgressCompat(progress, true)
            }
        }
    }

    private fun updateStatusUI(status: String) {
        binding.tvStatus.apply {
            text = status
            setTextColor(getStatusColor(status))
        }
    }

    private fun getStatusColor(status: String): Int {
        return when {
            status.contains("error", ignoreCase = true) -> 
                ContextCompat.getColor(this, R.color.error_color)
            status.contains("success", ignoreCase = true) -> 
                ContextCompat.getColor(this, R.color.success_color)
            else -> ContextCompat.getColor(this, R.color.default_text_color)
        }
    }

    private fun handleCompatibilityStatus(compatibility: DsuCompatibility) {
        when (compatibility) {
            is DsuCompatibility.Incompatible -> showIncompatibilityDialog(compatibility.reason)
            is DsuCompatibility.Compatible -> proceedWithDsuOperation()
        }
    }

    private fun showIncompatibilityDialog(reason: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.compatibility_warning)
            .setMessage(getString(R.string.compatibility_warning_message, reason))
            .setPositiveButton(R.string.continue_anyway) { _, _ -> 
                proceedWithDsuOperation()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun proceedWithDsuOperation() {
        // Implementation for proceeding with DSU operation
    }

    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "dsu_channel",
                "DSU Operations",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for DSU operations"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkBatteryLevel(): Boolean {
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val isCharging = batteryManager.isCharging()
        
        return when {
            batteryLevel >= 50 -> true
            batteryLevel >= 20 && isCharging -> {
                showBatteryWarningDialog(true)
                true
            }
            else -> {
                showBatteryWarningDialog(false)
                false
            }
        }
    }
    
    private fun showBatteryWarningDialog(canProceed: Boolean) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.battery_warning)
            .setMessage(if (canProceed) 
                R.string.battery_charging_warning_message 
                else R.string.battery_critical_warning_message)
            .apply {
                if (canProceed) {
                    setPositiveButton(R.string.continue_anyway) { _, _ -> 
                        proceedWithDsuOperation()
                    }
                }
                setNegativeButton(R.string.cancel, null)
            }
            .show()
    }

    private fun BatteryManager.isCharging(): Boolean {
        val status = getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        return status == BatteryManager.BATTERY_STATUS_CHARGING || 
               status == BatteryManager.BATTERY_STATUS_FULL
    }

    private fun setupClickListeners() {
        binding.btnLoadDsu.setOnClickListener {
            if (checkBatteryLevel()) {
                viewModel.loadDsu()
            }
        }
        
        binding.btnApplyDsu.setOnClickListener {
            if (checkBatteryLevel()) {
                viewModel.applyDsu()
            }
        }
        
        binding.fabSettings.setOnClickListener {
            SettingsBottomSheet().show(supportFragmentManager, "settings")
        }
    }
} 