package com.mimovistartest.data.util

import android.util.Log
import com.google.gson.Gson
import com.mimovistartest.data.api.RandomCoApiException
import com.mimovistartest.data.common.ServiceError
import com.mimovistartest.data.common.ServiceErrorInfo
import okhttp3.ResponseBody
import retrofit2.Response


sealed class Result<T> {
    class Success<T>(val data: T) : Result<T>()
    class Failure<T>(val error: ServiceError? = null, val exception: Exception? = null) :
        Result<T>()
}

class ApiResponse {
    companion object {
        fun <T> create(classType: Class<T>, requestResponse: Response<ResponseBody>): Result<T> {
            return try {
                handleResponse(requestResponse, classType)
            } catch (exception: Exception) {
                val netError = ServiceError(
                    ServiceErrorInfo(
                        message = requestResponse.message() ?: exception.localizedMessage
                    )
                )
                Result.Failure(netError)
            }
        }

        private fun <T> handleResponse(
            response: Response<ResponseBody>,
            classType: Class<T>
        ): Result<T> {
            return try {
                val body = response.body()?.string()
                try {
                    // Try to parse error or json object
                    Log.d("randomCo", " body $body")
                    val error = Gson().fromJson(body, ServiceError::class.java)
                    if (error.errorInfo.message != null)
                        Result.Failure(error, RandomCoApiException(RandomCoApiException.EMPTY_RESULT))
                     else
                        Result.Success(Gson().fromJson(body, classType))
                } catch (e: Exception) {
                    // no error but result is an array
                    if (!response.isSuccessful)
                        Result.Failure(
                            ServiceError(ServiceErrorInfo(response.message())),
                            RandomCoApiException(RandomCoApiException.UNKNOWN)
                        )
                     else
                        Result.Success(Gson().fromJson(body, classType))
                }
            } catch (exception: Exception) {
                // Error when parsing
                exception.printStackTrace()
                Result.Failure(ServiceError(), exception)
            }
        }
    }

}
