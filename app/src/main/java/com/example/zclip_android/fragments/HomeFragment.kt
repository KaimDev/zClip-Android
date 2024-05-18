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
import com.example.zclip_android.R

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

        setUpNetworkState()

        return binding.root
    }

    private fun setUpNetworkState()
    {
        binding.tvLanIp.text = localIpModel.ip

        if (!localIpModel.hasError)
        {
            binding.ivAppState.setImageResource(R.drawable.ic_sync_disabled)
        }
    }
}