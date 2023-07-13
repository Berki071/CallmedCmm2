package com.medhelp.callmedcmm2

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform