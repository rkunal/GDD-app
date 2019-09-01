package net.aiscope.gdd_app.ui.metadata

import android.content.Context
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.model.Status
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.repository.SampleRepository
import javax.inject.Inject

data class FieldOption(val id: Long, val title: Int)
data class ViewStateModel(val imagePath: String, val options: List<FieldOption>, val required: Boolean = true)

class MetadataPresenter @Inject constructor(
    private val view: MetadataView,
    private val repository: SampleRepository,
    private val remoteStorage: RemoteStorage,
    private val context: Context
) {

    fun showScreen() {
        // TODO set species stages
        view.fillForm(ViewStateModel(repository.current().imagePath!!, emptyList()))
    }

    fun notValid() {
        view.showInvalidFormError()
    }

    fun save(smearType: SmearType) {
        val sample = repository.current().copy(metadata = SampleMetadata(smearType.id), status = Status.ReadyToUpload)
        repository.store(sample)

        remoteStorage.enqueue(sample, context)

        view.goToHome()
    }
}
