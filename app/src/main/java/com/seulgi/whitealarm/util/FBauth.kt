package com.seulgi.whitealarm.util

import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*


class FBauth {
    companion object{
        private lateinit var auth : FirebaseAuth

        fun getUid() : String {
            auth = FirebaseAuth.getInstance()

            return auth.currentUser?.uid.toString()
        }

        fun getTime() : String {
            val currentDataTime = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(currentDataTime)

            return  dateFormat
        }

        fun diffTime(time : String) : Int {
            val dateFormat = SimpleDateFormat("yyyy.MM.dd")

            val nowDate = dateFormat.parse(getTime()).time
            val startDate = dateFormat.parse(time).time
            val diff = "${(nowDate - startDate) / (24 * 60 * 60 * 1000)}"

            return Integer.parseInt(diff)
        }
    }
}