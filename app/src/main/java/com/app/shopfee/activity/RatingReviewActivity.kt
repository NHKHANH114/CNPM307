package com.app.shopfee.activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.app.shopfee.MyApplication
import com.app.shopfee.R
import com.app.shopfee.databinding.ActivityRatingReviewBinding
import com.app.shopfee.model.Rating
import com.app.shopfee.model.RatingReview
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.GlobalFunction
import com.app.shopfee.utils.Utils
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

class RatingReviewActivity : BaseActivity() {


    private var ratingReview: RatingReview? = null
    private var mActivityRatingReviewBinding : ActivityRatingReviewBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityRatingReviewBinding = ActivityRatingReviewBinding.inflate(layoutInflater)
        setContentView(mActivityRatingReviewBinding!!.root)
        getDataIntent()
        initToolbar()
        initUi()
        initListener()
    }

    private fun getDataIntent() {
        val bundle = intent.extras ?: return
        ratingReview = bundle[Constant.RATING_REVIEW_OBJECT] as RatingReview?
    }

    private fun initUi() {
        mActivityRatingReviewBinding!!.ratingbar.rating = 5f
        if (RatingReview.TYPE_RATING_REVIEW_DRINK == ratingReview?.type) {
            mActivityRatingReviewBinding!!.tvMessageReview.text = getString(R.string.label_rating_review_drink)
        } else if (RatingReview.TYPE_RATING_REVIEW_ORDER == ratingReview?.type) {
            mActivityRatingReviewBinding!!.tvMessageReview.text = getString(R.string.label_rating_review_order)
        }
    }

    private fun initToolbar() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        imgToolbarBack.setOnClickListener { finish() }
        tvToolbarTitle.text = getString(R.string.ratings_and_reviews)
        imgToolbarBack.visibility = View.VISIBLE
    }

    private fun initListener() {
        mActivityRatingReviewBinding!!.tvSendReview.setOnClickListener {
            val rate = mActivityRatingReviewBinding!!.ratingbar.rating
            val review = mActivityRatingReviewBinding!!.edtReview.text.toString().trim { it <= ' ' }
            val rating = Rating(review, rate.toString().toDouble())
            if (RatingReview.TYPE_RATING_REVIEW_DRINK == ratingReview?.type) {
                sendRatingDrink(rating)
            } else if (RatingReview.TYPE_RATING_REVIEW_ORDER == ratingReview?.type) {
                sendRatingOrder(rating)
            }
        }
    }

    private fun sendRatingDrink(rating: Rating) {
        MyApplication[this].getRatingDrinkDatabaseReference(ratingReview?.id)
            ?.child(GlobalFunction.encodeEmailUser().toString())
            ?.setValue(
                rating
            ) { _: DatabaseError?, _: DatabaseReference? ->
                showToastMessage(getString(R.string.msg_send_review_success))
                mActivityRatingReviewBinding!!.ratingbar.rating = 5f
                mActivityRatingReviewBinding!!.edtReview.setText("")
                Utils.hideSoftKeyboard(this@RatingReviewActivity)
            }
    }

    private fun sendRatingOrder(rating: Rating) {
        val map: MutableMap<String, Any?> = HashMap()
        map["rate"] = rating.rate
        map["review"] = rating.review
        MyApplication[this].getOrderDatabaseReference()
            ?.child(ratingReview?.id.toString())
            ?.updateChildren(
                map
            ) { _: DatabaseError?, _: DatabaseReference? ->
                showToastMessage(getString(R.string.msg_send_review_success))
                mActivityRatingReviewBinding!!.ratingbar.rating = 5f
                mActivityRatingReviewBinding!!.edtReview.setText("")
                Utils.hideSoftKeyboard(this@RatingReviewActivity)
            }
    }
}