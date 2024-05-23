package com.kaimdev.zclip_android.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kaimdev.zclip_android.databinding.FragmentHomeBinding
import com.kaimdev.zclip_android.models.LocalIpModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.kaimdev.zclip_android.R
import com.kaimdev.zclip_android.helpers.ClipboardModes
import com.kaimdev.zclip_android.stores.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment : Fragment()
{
    private lateinit var binding: FragmentHomeBinding
    private var isSynced = false

    @Inject
    lateinit var localIpModel: LocalIpModel

    @Inject
    lateinit var dataStore: DataStore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setUpNetworkState()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        if (!localIpModel.hasError)
        {
            getClipboardMode()
        }
    }

    private fun setUpNetworkState()
    {
        binding.tvLanIp.text = localIpModel.ip

        if (!localIpModel.hasError)
        {
            binding.ivAppState.setImageResource(R.drawable.ic_sync_disabled)

            if (!isSynced)
            {
                binding.efabSend.visibility = View.GONE
            }
        }
    }

    private fun getClipboardMode()
    {
        val clipboardMode = dataStore.getClipboardMode()
        var mode: ClipboardModes? = null

        CoroutineScope(Dispatchers.IO).launch {

            clipboardMode.collect() {
                mode = it

                withContext(Dispatchers.Main) {
                    if (mode != null)
                        Log.d("ClipboardMode", mode!!.name)

                    if (mode == ClipboardModes.AUTOMATIC)
                    {
                        setUpNetworkState()
                    }
                }
            }
        }
    }
}