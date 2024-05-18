package com.example.zclip_android.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.zclip_android.databinding.FragmentHomeBinding
import com.example.zclip_android.models.LocalIpModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment()
{
    lateinit var binding: FragmentHomeBinding

    @Inject
    lateinit var localIpModel: LocalIpModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.tvLanIp.text = localIpModel.ip

        return binding.root
    }
}