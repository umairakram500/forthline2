package com.fourthline.sdksample.extensions

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.fourthline.sdksample.R
import com.fourthline.sdksample.showDialog
import com.fourthline.sdksample.vibrate
import kotlinx.android.synthetic.main.activity_main.*

fun Fragment.runOnUiThread(action: Runnable) {
    activity?.runOnUiThread(action)
}

fun Fragment.vibrate() = activity?.vibrate()

fun Fragment.showDialog(
    title: CharSequence,
    message: CharSequence,
    positiveButtonText: CharSequence,
    positiveButtonHandler: () -> Unit? = { },
    negativeButtonText: CharSequence? = null,
    negativeButtonHandler: () -> Unit? = { }
) = activity?.showDialog(
    title = title,
    message = message,
    positiveButtonText = positiveButtonText,
    positiveButtonHandler = positiveButtonHandler,
    negativeButtonText = negativeButtonText,
    negativeButtonHandler = negativeButtonHandler
)

/**
 * Convenience method for this example application, popping the fragment from backStack.
 *
 * This is in the assumption the Fragment transaction was actually added to the backStack.
 * Please review carefully for your application.
 */
fun Fragment.finish() {
    parentFragmentManager.popBackStack()
}

/**
 * Convenience method for this example application, adding a new instance of one of the Scanner
 * Fragments.
 */
fun Fragment.replaceWith(newFragment: Fragment) {
    finish()
    parentFragmentManager
        .beginTransaction()
        .add(R.id.fragmentContainer, newFragment)
        .addToBackStack(newFragment.tag)
        .commit()
}
