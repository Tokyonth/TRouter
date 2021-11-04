package com.trouter.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.tokyonth.trouter.core.TRouter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_router).setOnClickListener {
            TRouter.getInstance()
                .setPath("/login/LoginActivity")
                .addFiled("Str")
                .navigation()
        }
    }

}
