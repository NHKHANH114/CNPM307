package com.app.shopfee.utils

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.app.shopfee.activity.AdminMainActivity
import com.app.shopfee.activity.MainActivity
import com.app.shopfee.constant.AboutUsConfig
import com.app.shopfee.listener.IGetDateListener
import com.app.shopfee.prefs.DataStoreManager
import com.app.shopfee.prefs.DataStoreManager.Companion.user
import com.app.shopfee.utils.StringUtil.getDoubleNumber
import com.app.shopfee.utils.StringUtil.isEmpty
import java.util.Calendar

object GlobalFunction {

    fun startActivity(context: Context?, clz: Class<*>?) {
        val intent = Intent(context, clz)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context!!.startActivity(intent)
    }

    fun startActivity(context: Context?, clz: Class<*>?, bundle: Bundle?) {
        val intent = Intent(context, clz)
        intent.putExtras(bundle!!)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context!!.startActivity(intent)
    }

    @JvmStatic
    fun gotoMainActivity(context: Context) {
        if (user!!.isAdmin) {
            startActivity(context, AdminMainActivity::class.java)
        } else {
            startActivity(context, MainActivity::class.java)
        }
    }
    fun encodeEmailUser(): Int {
        var hashCode: Int = DataStoreManager.user?.email.hashCode()
        if (hashCode < 0) {
            hashCode *= -1
        }
        return hashCode
    }

    fun onClickOpenGmail(context: Context?) {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", AboutUsConfig.GMAIL, null
            )
        )
        context!!.startActivity(Intent.createChooser(emailIntent, "Send Email"))
    }

    fun onClickOpenSkype(context: Context?) {
        try {
            val skypeUri = Uri.parse("skype:" + AboutUsConfig.SKYPE_ID + "?chat")
            context!!.packageManager.getPackageInfo("com.skype.raider", 0)
            val skypeIntent = Intent(Intent.ACTION_VIEW, skypeUri)
            skypeIntent.component = ComponentName("com.skype.raider", "com.skype.raider.Main")
            context.startActivity(skypeIntent)
        } catch (e: Exception) {
            openSkypeWebView(context)
        }
    }

    private fun openSkypeWebView(context: Context?) {
        try {
            context!!.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("skype:" + AboutUsConfig.SKYPE_ID + "?chat")
                )
            )
        } catch (exception: Exception) {
            val skypePackageName = "com.skype.raider"
            try {
                context!!.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$skypePackageName")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                context!!.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$skypePackageName")
                    )
                )
            }
        }
    }

    fun onClickOpenFacebook(context: Context?) {
        var intent: Intent
        try {
            var urlFacebook: String = AboutUsConfig.PAGE_FACEBOOK
            val packageManager = context!!.packageManager
            val versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            if (versionCode >= 3002850) {
                urlFacebook = "fb://facewebmodal/f?href=" + AboutUsConfig.LINK_FACEBOOK
            }
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlFacebook))
        } catch (e: Exception) {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse(AboutUsConfig.LINK_FACEBOOK))
        }
        context!!.startActivity(intent)
    }

    fun onClickOpenYoutubeChannel(context: Context?) {
        context!!.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(AboutUsConfig.LINK_YOUTUBE)
            )
        )
    }

    fun onClickOpenZalo(context: Context?) {
        context!!.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(AboutUsConfig.ZALO_LINK)
            )
        )
    }

    fun callPhoneNumber(activity: Activity) {
        try {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    101
                )
                return
            }
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + AboutUsConfig.PHONE_NUMBER)
            activity.startActivity(callIntent)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun showToastMessage(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun hideSoftKeyboard(activity: Activity) {
        try {
            val inputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }
    }

    @JvmStatic
    fun showDatePicker(context: Context?, currentDate: String, getDateListener: IGetDateListener) {
        val mCalendar = Calendar.getInstance()
        var currentDay = mCalendar[Calendar.DATE]
        var currentMonth = mCalendar[Calendar.MONTH]
        var currentYear = mCalendar[Calendar.YEAR]
        mCalendar[currentYear, currentMonth] = currentDay
        if (!isEmpty(currentDate)) {
            val split = currentDate.split("/".toRegex()).toTypedArray()
            currentDay = split[0].toInt()
            currentMonth = split[1].toInt()
            currentYear = split[2].toInt()
            mCalendar[currentYear, currentMonth - 1] = currentDay
        }
        val callBack =
            DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val date = getDoubleNumber(dayOfMonth) + "/" +
                        getDoubleNumber(monthOfYear + 1) + "/" + year
                getDateListener.getDate(date)
            }
        val datePicker = DatePickerDialog(context!!,
            callBack, mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH],
            mCalendar[Calendar.DATE])
        datePicker.show()
    }
}