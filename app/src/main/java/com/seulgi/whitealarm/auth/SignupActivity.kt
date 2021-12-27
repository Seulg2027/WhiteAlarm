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
import com.seulgi.whitealarm.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth

    private lateinit var binding : ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)

        binding.back.setOnClickListener {
            val intent = Intent(this, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.joinAction.setOnClickListener {
            var isGoToJoin = true

            val email = binding.emailArea.text.toString()
            val pwd = binding.pwdArea.text.toString()
            val pwdcheck = binding.pwdcheck.text.toString()

            if (email.isEmpty()){
                Toast.makeText(this, "이메일을 입력해주세요. ", Toast.LENGTH_SHORT).show()
                isGoToJoin = false
            }
            if (pwdcheck.isEmpty()){
                Toast.makeText(this, "패스워드 확인을 입력해주세요. ", Toast.LENGTH_SHORT).show()
                isGoToJoin = false
            }
            if (!pwd.equals(pwdcheck)){
                Toast.makeText(this, "패스워드를 똑같이 입력해주세요. ", Toast.LENGTH_SHORT).show()
                isGoToJoin = false
            }
            if (pwd.length < 6){
                Toast.makeText(this, "비밀번호를 6자리 이상으로 입력해주세요. ", Toast.LENGTH_SHORT).show()
                isGoToJoin = false
            }
            if ( isGoToJoin ){
                auth.createUserWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // 회원가입 성공 //
                            Toast.makeText(this, "성공", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            // 회원가입 실패 //
                            Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}