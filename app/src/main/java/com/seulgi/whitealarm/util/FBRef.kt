package com.seulgi.whitealarm.util

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FBRef {
    companion object{
        private val database = Firebase.database

        val news = database.getReference("news")
        val visit = database.getReference("visit")
    }
}