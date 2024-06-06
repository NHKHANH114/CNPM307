package com.app.shopfee.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.shopfee.MyApplication
import com.app.shopfee.R
import com.app.shopfee.adapter.DrinkOrderAdapter
import com.app.shopfee.databinding.ActivityReceiptOrderBinding
import com.app.shopfee.model.Order
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.DateTimeUtils
import com.app.shopfee.utils.GlobalFunction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ReceiptOrderActivity : BaseActivity() {


    private var orderId: Long = 0
    private var mOrder: Order? = null
    private var mActivityReceiptOderBinding : ActivityReceiptOrderBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityReceiptOderBinding = ActivityReceiptOrderBinding.inflate(layoutInflater)
        setContentView(mActivityReceiptOderBinding!!.root)

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
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        imgToolbarBack.setOnClickListener { finish() }
        tvToolbarTitle.text = getString(R.string.label_receipt_order)
        imgToolbarBack.visibility = View.VISIBLE
    }

    private fun initUi() {

        val linearLayoutManager = LinearLayoutManager(this)
        mActivityReceiptOderBinding!!.rcvDrinks.layoutManager = linearLayoutManager

    }

    private fun initListener() {
        mActivityReceiptOderBinding!!.tvTrackingOrder.setOnClickListener {
            if (mOrder == null) return@setOnClickListener
            val bundle = Bundle()
            bundle.putLong(Constant.ORDER_ID, mOrder?.id!!)
            GlobalFunction.startActivity(
                this@ReceiptOrderActivity,
                TrackingOrderActivity::class.java, bundle
            )
            finish()
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

    private fun initData() {
        mActivityReceiptOderBinding!!.tvIdTransaction.text = mOrder?.id.toString()
        mActivityReceiptOderBinding!!.tvDateTime.text = DateTimeUtils.convertTimeStampToDate(mOrder?.dateTime!!.toLong())
        val strPrice = mOrder?.price.toString() + Constant.CURRENCY
        mActivityReceiptOderBinding!!.tvPrice.text = strPrice
        val strVoucher = "-" + mOrder?.voucher + Constant.CURRENCY
        mActivityReceiptOderBinding!!.tvVoucher.text = strVoucher
        val strTotal = mOrder?.total.toString() + Constant.CURRENCY
        mActivityReceiptOderBinding!!.tvTotal.text = strTotal
        mActivityReceiptOderBinding!!.tvPaymentMethod.text = mOrder?.paymentMethod
        mActivityReceiptOderBinding!!.tvName.text = mOrder?.address?.name
        mActivityReceiptOderBinding!!.tvPhone.text = mOrder?.address?.phone
        mActivityReceiptOderBinding!!.tvAddress.text = mOrder?.address?.address
        val adapter = DrinkOrderAdapter(mOrder?.drinks)
        mActivityReceiptOderBinding!!.rcvDrinks.adapter = adapter
        if (Order.STATUS_COMPLETE == mOrder?.status) {
            mActivityReceiptOderBinding!!.tvTrackingOrder.visibility = View.GONE
        } else {
            mActivityReceiptOderBinding!!.tvTrackingOrder.visibility = View.VISIBLE
        }
    }
}