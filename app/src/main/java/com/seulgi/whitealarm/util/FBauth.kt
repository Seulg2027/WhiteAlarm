package com.seulgi.whitealarm.util

import com.google.firebase.auth.FirebaseAuth

class FBauth {
    companion object{
        private lateinit var auth : FirebaseAuth

        fun getUid() : String {
            auth = FirebaseAuth.getInstance()

            return auth.currentUser?.uid.toString()
        }
    }
}