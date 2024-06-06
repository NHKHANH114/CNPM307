package com.app.shopfee.activity


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.shopfee.MyApplication
import com.app.shopfee.R
import com.app.shopfee.adapter.DrinkOrderAdapter
import com.app.shopfee.databinding.ActivityAdminReceiptOderBinding
import com.app.shopfee.model.Order
import com.app.shopfee.model.RatingReview
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.DateTimeUtils
import com.app.shopfee.utils.GlobalFunction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class AdminReceiptOderActivity : BaseActivity() {
    private var orderId: Long = 0
    private var mOrder: Order? = null
    private var mAdminReceiptOderBinding : ActivityAdminReceiptOderBinding?  = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdminReceiptOderBinding = ActivityAdminReceiptOderBinding.inflate(layoutInflater)
        setContentView(mAdminReceiptOderBinding!!.root)
        getDataIntent()
        initToolbar()
        initUi()
        initListener()
        getOrderDetailFromFirebase()
    }

    private fun getDataIntent() {
        val bundle = intent.extras ?: return
        orderId = bundle.getLong(Constant.ORDER_ID)
    }

    private fun initToolbar() {
        mAdminReceiptOderBinding!!.toolbar.imgToolbarBack.setOnClickListener { finish() }
        mAdminReceiptOderBinding!!.toolbar.tvToolbarTitle.text = getString(R.string.label_receipt_order)
        mAdminReceiptOderBinding!!.toolbar.imgToolbarBack.visibility = View.VISIBLE
    }

    private fun initUi() {
        val linearLayoutManager = LinearLayoutManager(this)
        mAdminReceiptOderBinding!!.rcvDrinks.layoutManager = linearLayoutManager
    }

    private fun initListener() {
        mAdminReceiptOderBinding!!.tvTrackingOrder.setOnClickListener {
            updateStatusOrder(Order.STATUS_ARRIVED)
            showToastMessage("Don Hang Da gui Thanh Cong")
        }
    }

    private fun getOrderDetailFromFirebase() {
        showProgressDialog(true)
        MyApplication[this].getOrderDetailDatabaseReference(orderId)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    showProgressDialog(false)
                    mOrder = snapshot.getValue(Order::class.java)
                    if (mOrder == null) return
                    initData()
                }

                override fun onCancelled(error: DatabaseError) {
                    showProgressDialog(false)
                    showToastMessage(getString(R.string.msg_get_date_error))
                }
            })
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        mAdminReceiptOderBinding!!.tvIdTransaction.text = mOrder?.id.toString()
        mAdminReceiptOderBinding!!.tvDateTime.text = DateTimeUtils.convertTimeStampToDate(mOrder?.dateTime!!.toLong())
        val strPrice = mOrder?.price.toString() + Constant.CURRENCY
        mAdminReceiptOderBinding!!.tvPrice.text = strPrice
        val strVoucher = "-" + mOrder?.voucher + Constant.CURRENCY
        mAdminReceiptOderBinding!!.tvVoucher.text = strVoucher
        val strTotal = mOrder?.total.toString() + Constant.CURRENCY
        mAdminReceiptOderBinding!!.tvTotal.text = strTotal
        mAdminReceiptOderBinding!!.tvPaymentMethod.text = mOrder?.paymentMethod
        mAdminReceiptOderBinding!!.tvName.text = mOrder?.address?.name
        mAdminReceiptOderBinding!!.tvPhone.text = mOrder?.address?.phone
        mAdminReceiptOderBinding!!.tvAddress.text = mOrder?.address?.address
        val adapter = DrinkOrderAdapter(mOrder?.drinks)
        mAdminReceiptOderBinding!!.rcvDrinks.adapter = adapter
        if (Order.STATUS_COMPLETE == mOrder?.status ) {
            mAdminReceiptOderBinding!!.tvTrackingOrder.visibility = View.GONE
        }
        else if(Order.STATUS_ARRIVED == mOrder?.status ){
            mAdminReceiptOderBinding!!.tvTrackingOrder.text = "Don Hang Da gui Thanh Cong"
        }
        else{
            mAdminReceiptOderBinding!!.tvTrackingOrder.visibility = View.VISIBLE
        }
    }

    private fun updateStatusOrder(status: Int) {
        if (mOrder == null) return
        val map: MutableMap<String, Any> = HashMap()
        map["status"] = status
        MyApplication[this].getOrderDatabaseReference()
            ?.child(mOrder?.id.toString())
            ?.updateChildren(
                map
            ) { _: DatabaseError?, _: DatabaseReference? ->
                if (Order.STATUS_COMPLETE == status) {
                    val bundle = Bundle()
                    val ratingReview = RatingReview(
                        RatingReview.TYPE_RATING_REVIEW_ORDER,
                        mOrder?.id.toString()
                    )
                    bundle.putSerializable(Constant.RATING_REVIEW_OBJECT, ratingReview)
                    GlobalFunction.startActivity(
                        this@AdminReceiptOderActivity,
                        RatingReviewActivity::class.java, bundle
                    )
                    finish()
                }
            }
    }
}