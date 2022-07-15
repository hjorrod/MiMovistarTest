package com.mimovistartest.data.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLHandshakeException

class CustomInterceptor : Interceptor {

    private var responseValue: InternetConnectionResponse =
        InternetConnectionResponse.LossConnection()

    override fun intercept(chain: Interceptor.Chain): Response {

        Log.d("elfoco", " interceptor")
        var networkProblem = false
        val original: Request = chain.request()

        // Customize the request
        val request: Request = original.newBuilder()
            .method(original.method, original.body)
            .build()

        try {
            return chain.proceed(request)
        } catch (socketException: SocketException) {
        } catch (socketTimeoutException: SocketTimeoutException) {
            networkProblem = true
        } catch (sslException: SSLHandshakeException) {
        } catch (connectException: ConnectException) {
            networkProblem = true
        } catch (io: IOException) {
            networkProblem = true
            // Parse I/O Cancelled Exceptions
            if ((io.message?.indexOf("Canceled", ignoreCase = true) != -1)) {
                networkProblem = false
            }
        }

        if (networkProblem) {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    InternetConnectionObserver.get().setResponseValue(responseValue)
                }
            }
        }

        return Response.Builder()
            .code(408)
            .protocol(Protocol.HTTP_2)
            .message("Error 408: Timeout from server")
            .request(chain.request())
            .body("{}".toResponseBody())
            .build()
    }
}

