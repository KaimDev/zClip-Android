package com.kaimdev.zclip_android

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kaimdev.zclip_android.fragments.HomeFragment
import com.kaimdev.zclip_android.fragments.SecurityFragment
import com.kaimdev.zclip_android.fragments.SettingsFragment
import com.kaimdev.zclip_android.models.FragmentEventModel

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val fragmentEventModel: FragmentEventModel
) : FragmentStateAdapter(fragmentManager, lifecycle)
{
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment
    {
        return when (position)
        {
            0 -> HomeFragment(fragmentEventModel)
            1 -> SecurityFragment()
            2 -> SettingsFragment()
            else -> HomeFragment(fragmentEventModel)
        }
    }

}