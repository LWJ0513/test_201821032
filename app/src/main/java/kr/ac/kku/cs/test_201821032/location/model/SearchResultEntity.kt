package kr.ac.kku.cs.test_201821032.location.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kr.ac.kku.cs.test_201821032.location.model.LocationLatLngEntity

@Parcelize
data class SearchResultEntity(
    val name: String,
    val fullAddress: String,
    val locationLatLng: LocationLatLngEntity
): Parcelable