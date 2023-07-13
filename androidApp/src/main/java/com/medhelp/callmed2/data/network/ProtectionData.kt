package com.medhelp.callmed2.data.network

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import com.medhelp.callmed2.utils.timber_log.LoggingTree.Companion.getMessageForError
import timber.log.Timber
import java.security.MessageDigest

class ProtectionData {
    //получение hash для защиты app
    fun getSignature(context: Context): String? {
        var apkSignature: String? = null
        try {
            @SuppressLint("PackageManagerGetSignatures") val packageInfo =
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
            for (signature in packageInfo.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                apkSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                apkSignature = escapeCharacter(apkSignature)
            }
        } catch (e: Exception) {
            Timber.e(getMessageForError(e, "ProtectionData/getSignature "))
        }
        return apkSignature
    }

    private fun escapeCharacter(str: String?): String {
        return str!!.replace("[^a-zA-Z0-9]+".toRegex(), "")
    }
}