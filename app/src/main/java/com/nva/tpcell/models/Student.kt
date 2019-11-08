package com.nva.tpcell.models

import android.os.Parcel
import android.os.Parcelable


data class Student(
    //only email compulsory for now, may change later
    val email: String,
    val institute_id: String? = null,
    val session_year: String? = null,
    val enroll: String? = null,
    val name: String? = null,
    val course: String? = null,
    val branch: String? = null,
    val phone: String? = null,
    val aggregate_10th: String? = null,
    val aggregate_12th: String? = null,
    val aggregate_college: String? = null,
    val backlog: String? = null,
    val gap_years: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(institute_id)
        parcel.writeString(session_year)
        parcel.writeString(enroll)
        parcel.writeString(name)
        parcel.writeString(course)
        parcel.writeString(branch)
        parcel.writeString(phone)
        parcel.writeString(aggregate_10th)
        parcel.writeString(aggregate_12th)
        parcel.writeString(aggregate_college)
        parcel.writeString(backlog)
        parcel.writeString(gap_years)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Student> {
        override fun createFromParcel(parcel: Parcel): Student {
            return Student(parcel)
        }

        override fun newArray(size: Int): Array<Student?> {
            return arrayOfNulls(size)
        }
    }
}