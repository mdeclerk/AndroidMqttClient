package com.example.mqttclient.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("android:fadeVisible")
fun setFadeVisible(view: View, visible: Boolean? = true) {
    if (view.tag == null) {
        view.tag = true
        view.visibility = if (visible == true) View.VISIBLE else View.GONE
    } else {
        view.animate().cancel()
        if (visible == true) {
            view.fadeIn()
        } else {
            view.fadeOut()
        }
    }
}

fun View.fadeIn() {
    if (this.visibility == View.VISIBLE) {
        return
    }

    this.visibility = View.VISIBLE
    this.alpha = 0f
    this.animate().alpha(1f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeIn.alpha = 1f
        }
    })
}

fun View.fadeOut() {
    if (this.visibility == View.GONE) {
        return
    }

    this.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeOut.alpha = 1f
            this@fadeOut.visibility = View.GONE
        }
    })
}