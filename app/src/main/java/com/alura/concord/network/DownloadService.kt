package com.alura.concord.network

import android.accounts.NetworkErrorException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.UnknownHostException

object DownloadService {

    suspend fun makeDownloadByURL(
        url: String,
        onFinishedDownload: (InputStream) -> Unit,
        onFailedDownload: () -> Unit
    ) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        try {
            withContext(IO) {
                delay(2000)
                client.newCall(request).execute().let { response ->
                    response.body?.byteStream()?.let { fileData: InputStream ->
                        withContext(Main){
                            onFinishedDownload(fileData)

                        }
                    }
                }
            }
        } catch (e: Exception) {
            when(e){
                is NetworkErrorException,
                is UnknownHostException,
                is FileNotFoundException -> {
                    onFailedDownload()
                } else -> throw e
            }
        }
    }
}
