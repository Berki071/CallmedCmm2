package com.medhelp.callmed2.data.model

import android.os.Parcel
import android.os.Parcelable
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineItem

class AllRecordsTelemedicineItemAndroid: AllRecordsTelemedicineItem, Parcelable {
   // var isShowNewMsgIco= false

    constructor(): super() {}
    constructor(item : AllRecordsTelemedicineItem): super(item) {}

    constructor(parcel: Parcel) : this() {
        this.serverKey = parcel.readString()
        this.dataServer = parcel.readString()
        this.idRoom = parcel.readInt()
        this.status = parcel.readString()
        this.idKl = parcel.readInt()
        this.idFilial = parcel.readInt()
        this.specialty = parcel.readString()
        this.fullNameKl = parcel.readString()
        this.drKl = parcel.readString()
        this.kommentKl = parcel.readString()
        this.fcmKl = parcel.readString()
        this.tmId = parcel.readInt()
        this.tmName = parcel.readString()
        this.tmType = parcel.readString()
        this.tmPrice = parcel.readInt()
        this.tmTimeForTm = parcel.readInt()
        this.timeStartAfterPay = parcel.readInt()
        this.dataStart = parcel.readString()
        this.dataEnd = parcel.readString()
        this.dataPay = parcel.readString()
        this.statusPay = parcel.readString()
        this.aboutShort = parcel.readString()
        this.aboutfull = parcel.readString()

        this.notif_24 = parcel.readString()
        this.notif_12 = parcel.readString()
        this.notif_4 = parcel.readString()
        this.notif_1 = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(serverKey!!)
        parcel.writeString(dataServer!!)
        parcel.writeInt(idRoom!!)
        parcel.writeString(status!!)
        parcel.writeInt(idKl!!)
        parcel.writeInt(idFilial!!)
        parcel.writeString(specialty!!)
        parcel.writeString(fullNameKl!!)
        parcel.writeString(drKl!!)
        parcel.writeString(kommentKl!!)
        parcel.writeString(fcmKl!!)
        parcel.writeInt(tmId!!)
        parcel.writeString(tmName!!)
        parcel.writeString(tmType!!)
        parcel.writeInt(tmPrice!!)
        parcel.writeInt(tmTimeForTm!!)
        parcel.writeInt(timeStartAfterPay!!)
        parcel.writeString(dataStart!!)
        parcel.writeString(dataEnd!!)
        parcel.writeString(dataPay!!)
        parcel.writeString(statusPay!!)
        parcel.writeString(aboutShort!!)
        parcel.writeString(aboutfull!!)

        parcel.writeString(notif_24!!)
        parcel.writeString(notif_12!!)
        parcel.writeString(notif_4!!)
        parcel.writeString(notif_1!!)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AllRecordsTelemedicineItemAndroid> {
        override fun createFromParcel(parcel: Parcel): AllRecordsTelemedicineItemAndroid {
            return AllRecordsTelemedicineItemAndroid(parcel)
        }

        override fun newArray(size: Int): Array<AllRecordsTelemedicineItemAndroid?> {
            return arrayOfNulls(size)
        }
    }
}