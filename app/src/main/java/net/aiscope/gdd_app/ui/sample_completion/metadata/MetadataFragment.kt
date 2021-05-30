package net.aiscope.gdd_app.ui.sample_completion.metadata

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.FragmentMetadataBinding
import net.aiscope.gdd_app.extensions.select
import net.aiscope.gdd_app.model.CompletedCapture
import net.aiscope.gdd_app.ui.metadata.SampleImagesAdapter
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionViewModel
import timber.log.Timber

class MetadataFragment : Fragment(R.layout.fragment_metadata) {
    private var _binding: FragmentMetadataBinding? = null
    private val binding get() = _binding!!
    private val sharedVM: SampleCompletionViewModel by activityViewModels()

    private val imagesAdapter =
        SampleImagesAdapter(lifecycleScope, this::onAddImageClicked, this::onImageClicked)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMetadataBinding.bind(view)
        with(binding) {
            metadataBloodSampleImages.apply {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(
                        view.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                adapter = imagesAdapter
            }

            with(sharedVM) {
                imagesAdapter.setCaptures(captures)

                smearTypeId?.let { metadataSectionSmearTypeRadioGroup.check(it) }
                speciesValue?.let { metadataSpeciesSpinner.select(it) }
            }
        }
    }

    fun validateAndUpdateVM(): Boolean {
        with(binding) {
            sharedVM.smearTypeId = metadataSectionSmearTypeRadioGroup.checkedRadioButtonId
            sharedVM.speciesValue = metadataSpeciesSpinner.selectedItem.toString()
            sharedVM.comments = metadataCommentsInput.text.toString()
        }
        //No validation on this tab
        return true
    }

    private fun onAddImageClicked() {
        lifecycleScope.launch {
            val intent = sharedVM.addImage()
            startActivity(intent)
        }
    }

    private fun onImageClicked(capture: CompletedCapture) {
        lifecycleScope.launch {
            val intent = sharedVM.editImage(capture)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
