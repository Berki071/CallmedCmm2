package com.medhelp.callmed2.data.model

class MkbItem {
    var kodMkb : String = ""
    var count : Int = 0

    constructor(kodMkb: String) {
        this.kodMkb = kodMkb
        count = 1
    }
}