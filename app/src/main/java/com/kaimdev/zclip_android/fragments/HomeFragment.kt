package com.kaimdev.zclip_android.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.kaimdev.zclip_android.databinding.FragmentHomeBinding
import com.kaimdev.zclip_android.models.LocalIpModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.kaimdev.zclip_android.R
import com.kaimdev.zclip_android.helpers.ClipboardModes
import com.kaimdev.zclip_android.models.FragmentEventModel
import com.kaimdev.zclip_android.modules.NetworkModule
import com.kaimdev.zclip_android.stores.DataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment(private val fragmentEventModel: FragmentEventModel) : Fragment()
{
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private var clipboardMode: ClipboardModes? = null
    private var isSynced = false

    @Inject
    lateinit var localIpModel: LocalIpModel

    @Inject
    lateinit var dataStore: DataStore

    private var alertDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setUpListeners()
        setUpNetworkState()

        binding.efabSend.setOnClickListener { viewModel.sendClipboard() }

        if (fragmentEventModel.sendClipboardContent)
        {
            viewModel.startSyncService()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        if (!localIpModel.hasError)
        {
            observeClipboardMode()
        }

        observeSyncState()
        observeShowDialog()
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)

        outState.putBoolean("isSynced", isSynced)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?)
    {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null)
        {
            isSynced = savedInstanceState.getBoolean("isSynced")
        }
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        viewModel.stopSyncService()
    }

    private fun setUpListeners()
    {
        if (localIpModel.hasError)
        {
            binding.btnSynchronize.setOnClickListener {
                if (!verifyNetwork())
                {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.network_not_detected),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            return
        }

        if (!isSynced)
        {
            binding.btnSynchronize.setOnClickListener {

                if (verifyNetwork())
                {
                    val ip = binding.tietTargetIp.text.toString()

                    // Regex for 192.168.X.X
                    val regex = Regex("^(192\\.168\\.[0-9]{1,3}\\.[0-9]{1,3})\$")

                    if (!regex.matches(ip))
                    {
                        binding.tilInput.isErrorEnabled = true
                        binding.tilInput.error = getString(R.string.invalid_ip)
                        binding.tilInput.requestFocus()
                        return@setOnClickListener
                    } else
                    {
                        binding.tilInput.isErrorEnabled = false
                    }

                    dataStore.setTargetIp(ip)

                    viewModel.startSyncService()
                }

            }
        } else
        {
            binding.btnSynchronize.setOnClickListener { viewModel.stopSyncService() }
        }
    }

    private fun setUpNetworkState()
    {
        binding.tvLanIp.text = localIpModel.ip

        if (!localIpModel.hasError)
        {
            binding.ivAppState.setImageResource(R.drawable.ic_sync_disabled)
        } else
        {
            binding.ivAppState.setImageResource(R.drawable.ic_wifi_off)
        }

        setUpListeners()
    }

    private fun verifyNetwork(): Boolean
    {
        val networkModule = NetworkModule()
        localIpModel = networkModule.provideLocalIpAddress(requireContext())

        setUpNetworkState()

        return !localIpModel.hasError
    }

    private fun setUpClipboardModes()
    {
        if (clipboardMode == ClipboardModes.AUTOMATIC)
        {
            binding.tvMode.text = getString(R.string.auto)
        } else if (clipboardMode == ClipboardModes.MANUAL)
        {
            binding.tvMode.text = getString(R.string.manual)
        }

        handleSendButton()
    }

    private fun handleSendButton()
    {
        if (clipboardMode == ClipboardModes.MANUAL && isSynced)
        {
            binding.efabSend.visibility = View.VISIBLE
            return
        }

        binding.efabSend.visibility = View.GONE
    }

    private fun observeSyncState()
    {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.isSync().collect {
                isSynced = it

                withContext(Dispatchers.Main)
                {
                    if (isSynced)
                    {
                        changeUItoSynced()

                        if (fragmentEventModel.sendClipboardContent)
                        {
                            viewModel.sendClipboard()
                        }

                    } else
                    {
                        changeUItoNotSynced()
                    }
                }
            }
        }
    }

    private fun observeClipboardMode()
    {
        val clipboardModeFlow = dataStore.getClipboardMode()

        CoroutineScope(Dispatchers.IO).launch {

            clipboardModeFlow.collect {
                clipboardMode = it

                withContext(Dispatchers.Main) {
                    if (clipboardMode != null)
                        Log.d("ClipboardMode", clipboardMode!!.name)

                    setUpClipboardModes()
                }
            }
        }
    }

    private fun changeUItoSynced()
    {
        binding.btnSynchronize.text = getString(R.string.stop_sync)
        binding.ivAppState.setImageResource(R.drawable.ic_sync_enable)
        binding.tvAppState.text = getString(R.string.connected)
        binding.tietTargetIp.isEnabled = false
        handleSendButton()
        setUpListeners()
    }

    private fun changeUItoNotSynced()
    {
        binding.btnSynchronize.text = getString(R.string.synchronize)
        binding.ivAppState.setImageResource(R.drawable.ic_sync_disabled)
        binding.tvAppState.text = getString(R.string.disconnected)
        binding.tietTargetIp.isEnabled = true
        handleSendButton()
        setUpListeners()
    }

    private fun observeShowDialog()
    {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.showDialogFlow.collect { showDialog ->

                if (showDialog)
                {
                    showRequestConnectionDialog()
                } else
                {
                    hideRequestConnectionDialog()
                }

            }
        }
    }

    private fun showRequestConnectionDialog()
    {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.there_is_a_new_request))
        builder.setMessage(getString(R.string.allow_new_connection))
        builder.setPositiveButton(getString(R.string.accept)) { dialog, which ->
            viewModel.acceptConnection()
            viewModel.showDialogFlow.value = false
        }
        builder.setNegativeButton(getString(R.string.deny)) { _, _ ->
            viewModel.denyConnection()
            viewModel.showDialogFlow.value = false
        }

        alertDialog = builder.create()
        alertDialog!!.show()
    }

    private fun hideRequestConnectionDialog()
    {
        alertDialog?.dismiss()
    }
}