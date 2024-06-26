package com.kaimdev.zclip_android.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kaimdev.zclip_android.databinding.FragmentSecurityBinding

class SecurityFragment : Fragment()
{
    lateinit var binding: FragmentSecurityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = FragmentSecurityBinding.inflate(inflater, container, false)
        return binding.root
    }
}