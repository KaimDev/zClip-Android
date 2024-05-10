package com.example.zclip_android

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zclip_android.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity()
{
    val viewModel: MainViewModel by lazy { MainViewModel(application) }
    private lateinit var binding: ActivityMainBinding

    init
    {
        instance = this
    }

    companion object
    {
        private var instance: MainActivity? = null

        fun applicationContext() : Context
        {
            return instance!!.applicationContext
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()

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

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPage2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPage2) { tab, position ->
            tab.icon = when (position)
            {
                0 -> ContextCompat.getDrawable(this, R.drawable.ic_home)
                1 -> ContextCompat.getDrawable(this, R.drawable.ic_security)
                2 -> ContextCompat.getDrawable(this, R.drawable.ic_settings)
                else -> ContextCompat.getDrawable(this, R.drawable.ic_home)
            }
        }.attach()
    }
}