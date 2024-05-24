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
    private var clipboardMode: ClipboardModes? = null

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
        }

        if (clipboardMode == ClipboardModes.AUTOMATIC)
        {
            binding.tvMode.text = getString(R.string.auto)
            binding.efabSend.visibility = View.GONE
        } else
        {
            binding.tvMode.text = getString(R.string.manual)
            binding.efabSend.visibility = View.VISIBLE
        }

        if (!isSynced)
        {
            binding.efabSend.visibility = View.GONE
        }
    }

    private fun getClipboardMode()
    {
        val clipboardModeFlow = dataStore.getClipboardMode()

        CoroutineScope(Dispatchers.IO).launch {

            clipboardModeFlow.collect() {
                clipboardMode = it

                withContext(Dispatchers.Main) {
                    if (clipboardMode != null)
                        Log.d("ClipboardMode", clipboardMode!!.name)

                    setUpNetworkState()
                }
            }
        }
    }
}