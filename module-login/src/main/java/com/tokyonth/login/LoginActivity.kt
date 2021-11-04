package com.tokyonth.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tokyonth.trouter.annotation.TRouterAnt

@TRouterAnt(path = "/login/LoginActivity")
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.e("跳转--->", "LoginActivity")
    }

}
