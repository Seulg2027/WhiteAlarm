package com.seulgi.whitealarm.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.seulgi.whitealarm.MainActivity
import com.seulgi.whitealarm.R
import com.seulgi.whitealarm.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth

    private lateinit var binding : ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)

        binding.signinBtn.setOnClickListener {
            var intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }

        binding.signupBtn.setOnClickListener {
            var intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.nomemberBtn.setOnClickListener {
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "오류",  Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}