package com.spundev.appplugin.consumer.ui.availableProviders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.spundev.appplugin.consumer.databinding.AvailableProvidersFragmentBinding

class AvailableProvidersFragment : Fragment() {

    private var _binding: AvailableProvidersFragmentBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: AvailableProvidersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AvailableProvidersFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val availableProvidersAdapter =
            AvailableProvidersAdapter(requireContext(), clickListener = { providerData ->
                val navAction =
                    AvailableProvidersFragmentDirections
                        .actionAvailableProvidersToProviderDetails(providerData)
                findNavController().navigate(navAction)
            })
        binding.availableProvidersRV.apply {
            adapter = availableProvidersAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.availableProvidersList.observe(viewLifecycleOwner) { providers ->
            availableProvidersAdapter.submitList(providers)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}