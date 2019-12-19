package com.reportsweatherandnetwork.network

import android.content.Context
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlin.system.measureTimeMillis

class NetworkWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wasWifiEnabled = wifiManager.isWifiEnabled
        if (wasWifiEnabled) {
            wifiManager.isWifiEnabled = false
        }

        Thread.sleep(10000)

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val pathRef = storageRef.child("Rockaankuthu.mp3")

        val db = FirebaseFirestore.getInstance()

        return try {
            val elapsed = measureTimeMillis {
                Tasks.await(pathRef.getBytes(15 * 1024 * 1024))
            }

            val downloadSpeed = (7.253034 * 8.0) / (elapsed.toDouble() / 1000.0)
            val roundedSpeed = "%.2f".format(downloadSpeed).toDouble()

            val report = hashMapOf(
                "speed" to roundedSpeed,
                "timestamp" to System.currentTimeMillis()
            )

            val telephonyManager =
                applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simOperatorName = telephonyManager.simOperatorName

            when {
                simOperatorName.contains("vodafone", true) -> {
                    Tasks.await(db.collection("vodafone").add(report))
                }
                simOperatorName.contains("airtel", true) -> {
                    Tasks.await(db.collection("airtel").add(report))
                }
                else -> {
                    Tasks.await(db.collection("others").add(report))
                }
            }

            if (wasWifiEnabled) {
                wifiManager.isWifiEnabled = true
            }

            Result.success()
        } catch (it: Exception) {
            if (wasWifiEnabled) {
                wifiManager.isWifiEnabled = true
            }

            Result.failure()
        }
    }

}