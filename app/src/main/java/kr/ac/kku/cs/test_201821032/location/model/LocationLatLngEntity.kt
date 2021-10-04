package kr.ac.kku.cs.test_201821032.location.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationLatLngEntity(            // 4-3
    val latitude: Float,
    val longitude: Float
): Parcelable