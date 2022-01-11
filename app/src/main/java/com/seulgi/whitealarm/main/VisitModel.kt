package com.seulgi.whitealarm.main

data class VisitModel ( var place_name : String = "", var date : String = "") {
    fun getdateData() : String {
        return date
    }

    fun getPlaceName() : String {
        return place_name
    }
}