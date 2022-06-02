package com.mimovistartest.util

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.mimovistartest.R
import com.squareup.picasso.Picasso
import dev.skomlach.biometric.compat.BiometricApi
import dev.skomlach.biometric.compat.BiometricAuthRequest
import dev.skomlach.biometric.compat.BiometricConfirmation
import dev.skomlach.biometric.compat.*

fun FragmentActivity.replaceFragment(
    fragment: Fragment,
    frameId: Int,
    tag: String,
    addToBackStack: Boolean = false
) {
    supportFragmentManager.inTransaction {
        setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
        if (addToBackStack) addToBackStack(tag)
        replace(frameId, fragment, tag)
    }
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) =
    beginTransaction().func().commit()

inline val <reified T> T.TAG: String
    get() = T::class.java.canonicalName ?: T::class.simpleName ?: T::class.java.simpleName

fun ImageView.loadUrl(url: String) = loadUri(Uri.parse(url))

fun ImageView.loadUri(uri: Uri) {
    Picasso.get().load(uri).into(this)
}

fun String.toast(context: Context, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(context, this, duration).apply { show() }
}

fun FragmentActivity.startMyBioAuth() {
    val promptInfo = BiometricPrompt.PromptInfo.Builder().apply {
        setTitle("titulo")
        setSubtitle("subtitulo")
        setConfirmationRequired(true)
        //setDeviceCredentialAllowed(true)
        setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        setNegativeButtonText("NegativeText")
    }.build()

    val biometricPrompt = BiometricPrompt(
        this,
        ContextCompat.getMainExecutor(this.applicationContext),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                "Authentication error! $errorCode - $errString".toast(this@startMyBioAuth.applicationContext)
            }

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                super.onAuthenticationSucceeded(result)
                "Authentication succeeded!".toast(this@startMyBioAuth.applicationContext)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                "Authentication failed!".toast(this@startMyBioAuth.applicationContext)
            }
        })

    val biometricManager = BiometricManager.from(this.applicationContext)
    when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
        BiometricManager.BIOMETRIC_SUCCESS -> {
            Log.d("FirebaseAndBioTest", "App can authenticate using biometrics.")
            //biometricPrompt.authenticate(promptInfo)
        }
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
            Log.d("FirebaseAndBioTest", "BIOMETRIC_ERROR_NO_HARDWARE Hardware not available")
        }
        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
            Log.d(
                "FirebaseAndBioTest",
                "BIOMETRIC_ERROR_HW_UNAVAILABLE Biometric features are currently unavailable."
            )
        }
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            Log.d(
                "FirebaseAndBioTest",
                "BIOMETRIC_ERROR_NONE_ENROLLED The user hasn't associated any biometric credentials with their account."
            )
        }
        else -> {
            Log.d("FirebaseAndBioTest", "Nothing supported")
        }
    }
    biometricPrompt.authenticate(promptInfo)
}

fun FragmentActivity.startBioAuth() {
    val iris = BiometricAuthRequest(
        BiometricApi.AUTO,
        BiometricType.BIOMETRIC_IRIS,
        BiometricConfirmation.ANY
    )
    val faceId = BiometricAuthRequest(
        BiometricApi.AUTO,
        BiometricType.BIOMETRIC_FACE,
        BiometricConfirmation.ANY
    )
    val fingerprint = BiometricAuthRequest(
        BiometricApi.AUTO,
        BiometricType.BIOMETRIC_FINGERPRINT,
        BiometricConfirmation.ANY
    )
    var title = "Title"
    val currentBiometric =
        if (BiometricManagerCompat.isHardwareDetected(iris)
            && BiometricManagerCompat.hasEnrolled(iris)
        ) {
            title =
                "Your eyes are not only beautiful, but you can use them to unlock our app"
            iris
        } else
            if (BiometricManagerCompat.isHardwareDetected(faceId)
                && BiometricManagerCompat.hasEnrolled(faceId)
            ) {
                title = "Use your smiling face to enter the app"
                faceId
            } else if (BiometricManagerCompat.isHardwareDetected(fingerprint)
                && BiometricManagerCompat.hasEnrolled(fingerprint)
            ) {
                title = "Your unique fingerprints can unlock this app"
                fingerprint
            } else {
                null
            }

    currentBiometric?.let { biometricAuthRequest ->
        if (BiometricManagerCompat.isBiometricSensorPermanentlyLocked(biometricAuthRequest)
            || BiometricManagerCompat.isLockOut(biometricAuthRequest)
        ) {
            "Biometric not available right now. Try again later".toast(this.applicationContext)
            return
        }

        val prompt = BiometricPromptCompat.Builder(this).apply {
            this.setTitle(title)
            this.setNegativeButton("Cancel", null)
            this.setDescription("Description")
            this.setEnabledNotification(false)//hide notification
            this.setEnabledBackgroundBiometricIcons(false)//hide duplicate biometric icons above dialog
        }.build()

        prompt.authenticate(object : BiometricPromptCompat.AuthenticationCallback() {
            override fun onSucceeded(confirmed: Set<BiometricType>) {
                "User authorized :)".toast(this@startBioAuth.applicationContext)
            }

            override fun onCanceled() {
                "Auth canceled :|".toast(this@startBioAuth.applicationContext)
            }

            override fun onFailed(reason: AuthenticationFailureReason?) {
                "Fatal error happens :(\nReason $reason".toast(this@startBioAuth.applicationContext)
            }

            override fun onUIOpened() {}

            override fun onUIClosed() {}
        })
    } ?: run {
        "No available biometric on this device".toast(this.applicationContext)
    }
}