package com.tokyonth.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.tokyonth.trouter.annotation.TRouterAnt

@TRouterAnt(path = "/login/LoginActivity")
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<TextView>(R.id.tv_info).text = intent.getStringExtra("info")
    }

}
