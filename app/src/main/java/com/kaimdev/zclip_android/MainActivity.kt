package com.kaimdev.zclip_android

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kaimdev.zclip_android.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kaimdev.zclip_android.stores.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.os.Build

@AndroidEntryPoint
class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var dataStore: DataStore

    private var fromNotification = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val intent = intent

        if (intent != null && intent.getBooleanExtra("notification", false))
        {
            intent.removeExtra("notification")
            fromNotification = true
        }

        checkAndRequestPermissions()

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()

        CoroutineScope(Dispatchers.IO).launch {
            dataStore.initialConfiguration()
        }

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initTabLayout()
    }

    private fun initTabLayout()
    {
        val viewPage2 = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle, fromNotification)
        viewPage2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPage2) { tab, position ->
            tab.icon = when (position)
            {
                0    -> ContextCompat.getDrawable(this, R.drawable.ic_home)
                1    -> ContextCompat.getDrawable(this, R.drawable.ic_security)
                2    -> ContextCompat.getDrawable(this, R.drawable.ic_settings)
                else -> ContextCompat.getDrawable(this, R.drawable.ic_home)
            }
        }.attach()
    }

    private fun checkAndRequestPermissions()
    {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_WIFI_STATE
            ) != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_WIFI_STATE),
                1
            )
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
                2
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            )
            {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    3
                )
            }

            return
        }
    }
}