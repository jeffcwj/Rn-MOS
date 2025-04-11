package com.gtastart.common.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


object CommonUtils {
    fun req(url: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        try {
            val urls = URL(url)

            val connection = urls.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()
                onSuccess.invoke(response)
//                println("Response: $response")
            } else {
                onError.invoke(BufferedReader(InputStreamReader(connection.inputStream)).readText())
                println("Error: $responseCode")
            }

            connection.disconnect()
        } catch (e: Exception) {
            onError.invoke(e.toString())
            e.printStackTrace()
        }
    }
}

object MToast {
    @JvmStatic
    fun show(context: Context, text: String, isLong: Boolean = false) {
        Toast.makeText(context.applicationContext, text, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }
    @JvmStatic
    fun show(context: Context, text: String) {
        Toast.makeText(context.applicationContext, text, Toast.LENGTH_SHORT).show()
    }
}

object MHelpers {
    fun openBrowser(context: Context, url: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 如果是 application context，需要这个
        context.startActivity(intent)
    }

    fun joinQQGroup(key: String, context: Context): Boolean {
        val intent = Intent()
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
            return true
        } catch (e: Exception) {
            // 未安装手Q或安装的版本不支持
            return false
        }
    }

}