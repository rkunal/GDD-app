package net.aiscope.gdd_app.ui.sample_preparation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.ActivitySamplePreparationBinding
import net.aiscope.gdd_app.extensions.select
import net.aiscope.gdd_app.ui.CaptureFlow
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbar
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbarAction
import javax.inject.Inject

class SamplePreparationActivity : AppCompatActivity(), SamplePreparationView, CaptureFlow {


    @Inject
    lateinit var presenter: SamplePreparationPresenter

    private val defaultFormData: SamplePreparationViewStateModel by lazy {
        SamplePreparationViewStateModel(
            waterType = getString(R.string.spinner_empty_option),
            usesGiemsa = true,
            giemsaFP = true,
            usesPbs = true,
            reusesSlides = false,
            bloodQuality = getString(R.string.spinner_empty_option),
        )
    }

    private lateinit var binding: ActivitySamplePreparationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivitySamplePreparationBinding.inflate(layoutInflater)
        with(binding) {
            setContentView(root)

            setSupportActionBar(toolbarLayout.toolbar)
            attachCaptureFlowToolbar(toolbarLayout.toolbar)

            samplePreparationContinueButton.setOnClickListener { save() }
        }

        lifecycleScope.launch {
            presenter.showScreen()
        }
    }

    override fun fillForm(model: SamplePreparationViewStateModel?) = with(binding) {
        val formData = model ?: defaultFormData
        with(formData) {
            samplePreparationWaterTypeSpinner.select(waterType)
            samplePreparationGiemsaSwitch.isChecked = usesGiemsa
            samplePreparationGiemsaFpSwitch.isChecked = giemsaFP
            samplePreparationPbsSwitch.isChecked = usesPbs
            samplePreparationSlidesReuseSwitch.isChecked = reusesSlides
            samplePreparationBloodQualitySpinner.select(bloodQuality)
        }
    }

    private fun validateForm(): Boolean = with(binding) {
        val isWaterTypeValid = samplePreparationWaterTypeSpinner.selectedItem.toString() !=
                getString(R.string.spinner_empty_option)      
        samplePreparationWaterTypeError.isVisible = !isWaterTypeValid
        val isBloodQualityValid = samplePreparationBloodQualitySpinner.selectedItem.toString() !=
                getString(R.string.spinner_empty_option)
        samplePreparationBloodQualityError.isVisible = !isBloodQualityValid
        return isWaterTypeValid && isBloodQualityValid
    }

    private fun save() {
        if (!validateForm()) return
        lifecycleScope.launch {
            val viewModel = with(binding) {
                 SamplePreparationViewStateModel(
                    samplePreparationWaterTypeSpinner.selectedItem.toString(),
                    samplePreparationGiemsaSwitch.isChecked,
                    samplePreparationGiemsaFpSwitch.isChecked,
                    samplePreparationPbsSwitch.isChecked,
                    samplePreparationSlidesReuseSwitch.isChecked,
                    samplePreparationBloodQualitySpinner.selectedItem.toString(),
                )
            }

            presenter.save(viewModel)
        }
    }

    override fun showRetryBar() {
        CustomSnackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.sample_preparation_snackbar_error),
            Snackbar.LENGTH_INDEFINITE, null,
            CustomSnackbarAction(
                getString(R.string.sample_preparation_snackbar_retry),
                View.OnClickListener {
                    save()
                })
        ).show()
    }

    override fun goToMicroscopeQuality() {
        //No longer exists
    }
}
