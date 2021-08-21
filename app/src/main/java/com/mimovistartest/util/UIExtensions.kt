package com.mimovistartest.util

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.mimovistartest.R
import com.squareup.picasso.Picasso

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


