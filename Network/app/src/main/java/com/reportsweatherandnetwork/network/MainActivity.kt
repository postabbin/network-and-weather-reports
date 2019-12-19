package com.reportsweatherandnetwork.network

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enqueueButton.setOnClickListener {
            val perioReq = PeriodicWorkRequest.Builder(NetworkWorker::class.java, 16, TimeUnit.MINUTES).build()
            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("com.reportsweatherandnetwork.network.networkworker", ExistingPeriodicWorkPolicy.REPLACE, perioReq)
        }
    }
}
