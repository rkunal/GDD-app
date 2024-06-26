package net.aiscope.gdd_app.ui.mask

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BrushDiseaseStage(val id: Int, val name: String, val maskColor: Int) : Parcelable {

    override fun toString() = name
}
