package com.example.zclip_android.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.zclip_android.R
import com.example.zclip_android.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment()
{
    lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
}