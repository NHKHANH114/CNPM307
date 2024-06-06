package com.app.shopfee.fragment.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.shopfee.MyApplication
import com.app.shopfee.R
import com.app.shopfee.activity.AdminMainActivity
import com.app.shopfee.activity.AdminReceiptOderActivity
import com.app.shopfee.activity.TrackingOrderActivity
import com.app.shopfee.adapter.AdminOderAdapter2
import com.app.shopfee.databinding.FragmentAdminOderBinding
import com.app.shopfee.fragment.BaseFragment
import com.app.shopfee.fragment.OrderFragment
import com.app.shopfee.model.Order
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.GlobalFunction
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import java.util.*
import kotlin.collections.ArrayList


class AdminOderFragment : BaseFragment() {
    private var mAdminOderFragmentBinding : FragmentAdminOderBinding? = null
    private var orderTabType = 0
    private var listOrder: MutableList<Order>? = null
    private var listOrder2: MutableList<Order>? = null

    private var orderAdapter: AdminOderAdapter2? = null

    override fun initToolbar() {
        if (activity != null) {
            (activity as AdminMainActivity?)!!.setToolBar(true, getString(R.string.nav_order))
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mAdminOderFragmentBinding = FragmentAdminOderBinding.inflate(inflater, container, false)
        //getListOrderFromFirebase()
        getListOrders()
        getDataArguments()
        initUi()
        return mAdminOderFragmentBinding!!.root
    }


    private fun getDataArguments() {
        val bundle = arguments ?: return
        orderTabType = bundle.getInt(Constant.ORDER_TAB_TYPE)

    }

    private fun initUi() {
        listOrder = ArrayList()
        listOrder2 = ArrayList()
        val linearLayoutManager = LinearLayoutManager(activity)
        mAdminOderFragmentBinding!!.rcvOrder.layoutManager = linearLayoutManager
        orderAdapter = AdminOderAdapter2(activity, listOrder, object : AdminOderAdapter2.IClickOrderListener {
            override fun onClickTrackingOrder(orderId: Long) {
                val bundle = Bundle()
                bundle.putLong(Constant.ORDER_ID, orderId)
                GlobalFunction.startActivity(activity, TrackingOrderActivity::class.java, bundle)
            }

            override fun onClickReceiptOrder(order: Order) {
                val bundle = Bundle()
                bundle.putLong(Constant.ORDER_ID, order.id)
                GlobalFunction.startActivity(activity, AdminReceiptOderActivity::class.java, bundle)
            }
        })
        mAdminOderFragmentBinding!!.rcvOrder.adapter = orderAdapter
    }

    private fun getListOrders() {
        if (activity == null) {
            return
        }
        MyApplication[activity!!].getOrderDatabaseReference()
            ?.addChildEventListener(object : ChildEventListener {
                @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val order = dataSnapshot.getValue(Order::class.java)

                    if (order!!.status < Order.STATUS_COMPLETE)
                    {
                        listOrder!!.add(0, order)
                    }
                    mAdminOderFragmentBinding!!.tvTotalOder.text = listOrder!!.size.toString() + " Đơn hàng"
                    orderAdapter!!.notifyDataSetChanged()
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                    val order = dataSnapshot.getValue(Order::class.java)
                    for (i in listOrder!!.indices) {
                        if (order!!.id == listOrder!![i].id) {
                            listOrder!![i] = order
                            break
                        }
                    }
                    orderAdapter!!.notifyDataSetChanged()
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    val order = dataSnapshot.getValue(Order::class.java)

                    for (orderObject in listOrder!!) {
                        if (order!!.id == orderObject.id) {
                            listOrder!!.remove(orderObject)
                            break
                        }
                    }
                    orderAdapter!!.notifyDataSetChanged()
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (orderAdapter != null) orderAdapter!!.release()
    }


//    companion object {
//        fun newInstance(type: Int): OrderFragment {
//            val orderFragment = OrderFragment()
//            val bundle = Bundle()
//            bundle.putLong(Constant.ORDER_TAB_TYPE, type)
//            orderFragment.arguments = bundle
//            return orderFragment
//        }
//    }
}