package ua.itaysonlab.homefeeder.activites

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.overlay.notification.NotificationListener

class MainActivity : AppCompatActivity() {
    fun isNotificationServiceEnabled(): Boolean {
        val enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (!isNotificationServiceEnabled()) {
            requestNotificationPermission()
        } else {
            findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_settings)
        }
    }

    fun requestNotificationPermission() {
        MaterialAlertDialogBuilder(this, R.style.MDialog).apply {
            setTitle(R.string.allow_notify_pref)
            setMessage(R.string.allow_notify_desc)
            setPositiveButton(R.string.allow_notify_to_settings) { _, _ ->
                startActivityForResult(Intent(HFApplication.ACTION_MANAGE_LISTENERS).putExtra(
                    ":settings:fragment_args_key", ComponentName(context, NotificationListener::class.java).flattenToString()
                ), 1)
            }
            setNeutralButton(R.string.cancel, null)
        }.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_settings, R.id.navigation_about
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (!isNotificationServiceEnabled()) requestNotificationPermission()
        requestStoragePermission()
    }

    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            showStorageAlert()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_settings)
            } else {
                showStorageAlert()
            }
        }
    }

    private fun showStorageAlert() {
        MaterialAlertDialogBuilder(this, R.style.MDialog).apply {
            setTitle(R.string.storage_alert)
            setMessage(R.string.storage_desc)
            setPositiveButton(R.string.storage_action) { _, _ ->
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
            setNeutralButton(R.string.cancel, null)
        }.show()
    }
}
