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
import com.seulgi.whitealarm.databinding.ActivitySigninBinding

class SigninActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var binding : ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signin)

        binding.back.setOnClickListener {
            val intent = Intent(this, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.loginAction.setOnClickListener {
            var email = binding.emailArea.text.toString()
            var pwd = binding.pwdArea.text.toString()

            auth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 로그인 성공 //
                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        // 로그인 실패 //
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}