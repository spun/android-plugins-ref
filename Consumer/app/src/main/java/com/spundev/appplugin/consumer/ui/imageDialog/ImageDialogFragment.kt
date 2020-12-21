package com.spundev.appplugin.consumer.ui.imageDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.spundev.appplugin.consumer.databinding.ImageDialogFramgentBinding

class ImageDialogFragment : DialogFragment() {
    private var _binding: ImageDialogFramgentBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: ImageDialogViewModel by viewModels()

    private val args: ImageDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ImageDialogFramgentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateDialogContent()
    }

    private fun updateDialogContent() {
        // Note you don't need to use viewLifecycleOwner in DialogFragment.
        // (https://github.com/google/iosched/blob/b428d2be4bb96bd423e47cb709c906ce5d02150f/mobile/src/main/java/com/google/samples/apps/iosched/ui/settings/ThemeSettingDialogFragment.kt#L58)
        viewModel.providerImageLiveData.observe(this) {
            binding.petImageView.load(it)
        }

        val imageUri = args.imageUri
        viewModel.requestImage(imageUri)

        // Coil can load from content uri
        // val imageUri = args.imageUri
        // binding.petImageView.load(imageUri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
