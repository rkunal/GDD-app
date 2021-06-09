package net.aiscope.gdd_app.presentation

import android.content.Context
import com.nhaarman.mockito_kotlin.whenever
import junit.framework.Assert.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.aiscope.gdd_app.CoroutineTestRule
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.BloodQuality
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.MicroscopeQuality
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SamplePreparation
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.model.WaterType
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SampleCompletionViewModelTest {
    companion object {
        private val sample = Sample("an id", "a facility", "a microscopist", "a disease")
        private val lastSample = sample.copy(
            id = "last ID",
            microscopeQuality = MicroscopeQuality(true, 1500),
            preparation = SamplePreparation(
                WaterType.TAP,
                usesGiemsa = true,
                giemsaFP = true,
                usesPbs = false,
                reusesSlides = false,
                bloodQuality = BloodQuality.OLD
            ),
            metadata = SampleMetadata(
                smearType = SmearType.THICK,
                species = MalariaSpecies.P_FALCIPARUM,
                comments = "Should not show"
            )
        )
    }

    @get:Rule
    val coroutinesTestRule = CoroutineTestRule()

    @Mock
    private lateinit var repository: SampleRepository

    @Mock
    private lateinit var remoteStorage: RemoteStorage

    @Mock
    private lateinit var context: Context

    private lateinit var viewModel: SampleCompletionViewModel

    @Before
    fun before() {
        coroutinesTestRule.runBlockingTest {
            whenever(context.getString(R.string.spinner_empty_option))
                .thenReturn("- select -")
            whenever(context.getString(R.string.malaria_species_p_falciparum))
                .thenReturn("P. falciparum")
            whenever(context.getString(R.string.water_type_tap))
                .thenReturn("Tap")
            whenever(context.getString(R.string.blood_quality_old))
                .thenReturn("Old")
            whenever(repository.current()).thenReturn(sample)

            viewModel = SampleCompletionViewModel(repository, remoteStorage, context)
        }
    }

    @Test
    fun `Should set defaults if no previously saved sample`() {
        coroutinesTestRule.runBlockingTest {
            whenever(repository.lastSaved()).thenReturn(null)
            viewModel.initVM()

            // Should be a nicer way to do this, really
            Thread.sleep(1000)

            assertEquals(null, viewModel.smearTypeId)
            assertEquals("a disease", viewModel.disease)
        }
    }

    @Test
    fun `Should set values from previously saved sample`() {
        coroutinesTestRule.runBlockingTest {
            whenever(repository.lastSaved()).thenReturn(lastSample)
            viewModel.initVM()

            // Should be a nicer way to do this, really
            Thread.sleep(1000)

            //Taken from current
            assertEquals("a disease", viewModel.disease)

            //Default
            assertNull(viewModel.comments)

            //Taken from previous
            assertEquals(true, viewModel.microscopeDamaged)
            assertEquals(1500, viewModel.microscopeMagnification)

            assertEquals("Tap", viewModel.waterType)
            assertEquals(true, viewModel.usesGiemsa)
            assertEquals(true, viewModel.giemsaFP)
            assertEquals(false, viewModel.usesPbs)
            assertEquals(false, viewModel.reusesSlides)
            assertEquals("Old", viewModel.bloodQuality)

            assertEquals(R.id.metadata_blood_smear_thick, viewModel.smearTypeId)
            assertEquals("P. falciparum", viewModel.speciesValue)

            //Make sure we have the right current
            assertEquals("an id", viewModel.getCurrentSample().id)
        }
    }

}