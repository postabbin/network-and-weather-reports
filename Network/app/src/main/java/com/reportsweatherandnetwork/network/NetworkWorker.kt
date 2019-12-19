package com.reportsweatherandnetwork.network

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class NetworkWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.v("MyLogs", "Hello from worker")
        return Result.success()
    }

}