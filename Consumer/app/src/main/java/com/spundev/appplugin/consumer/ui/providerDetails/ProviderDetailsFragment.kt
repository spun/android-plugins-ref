package com.spundev.appplugin.consumer.ui.providerDetails

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.spundev.appplugin.consumer.databinding.ProviderDetailsFragmentBinding
import com.spundev.appplugin.consumer.model.ProviderData
import com.spundev.appplugin.pluginapi.provider.ApiPetProvider
import com.spundev.appplugin.pluginapi.provider.ApiProviderContract

class ProviderDetailsFragment : Fragment() {

    private var _binding: ProviderDetailsFragmentBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: ProviderDetailsViewModel by viewModels()

    private val args: ProviderDetailsFragmentArgs by navArgs()

    private lateinit var providerData: ProviderData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProviderDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        providerData = args.providerData

        // Set authority on ViewModel
        viewModel.setProviderInfo(args.providerData)

        val availableProvidersAdapter = ProviderDetailsAdapter(requireContext()) { petData ->
            val imageUri = ApiProviderContract
                .getContentUri(providerData.authority)
                .buildUpon()
                .appendPath(petData.id.toString())
                .build()
            // Show pet image dialog
            val action =
                ProviderDetailsFragmentDirections.actionProviderDetailsToImageDialog(
                    petData.id,
                    imageUri.toString()
                )
            findNavController().navigate(action)
        }
        binding.providerContentRecyclerView.apply {
            adapter = availableProvidersAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        // Provider details
        viewModel.providerDataLiveData.observe(viewLifecycleOwner) { providerData ->
            binding.providerTitle.text = providerData.title
            binding.providerAuthority.text = providerData.authority
            binding.providerDescription.text = providerData.description
        }

        // Recycler view content
        viewModel.getProviderContentLiveData().observe(viewLifecycleOwner) { contentList ->
            availableProvidersAdapter.submitList(contentList)
        }

        // Button listeners
        setActionButtonsListener()
    }

    private val providerSettings = registerForActivityResult(StartActivityFromSettings()) {
        // on activity result
    }

    private fun setActionButtonsListener() {
        // Request new data to the provider
        binding.loadRequestButton.setOnClickListener {
            viewModel.sendLoadRequest()
        }

        // Open provider settings
        binding.openSettingsButton.setOnClickListener {
            try {
                providerSettings.launch(providerData.settingsActivity)
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "Can't launch provider settings.", e)
            } catch (e: SecurityException) {
                Log.e(TAG, "Can't launch provider settings.", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class StartActivityFromSettings : ActivityResultContract<ComponentName, Boolean>() {
        override fun createIntent(context: Context, input: ComponentName): Intent =
            Intent().setComponent(input)
                .putExtra(ApiPetProvider.EXTRA_FROM_CONSUMER, true)

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
            resultCode == Activity.RESULT_OK
    }
}

private const val TAG = "ProviderDetailsFragment"