package com.app.shopfee.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shopfee.R
import com.app.shopfee.R.color
import com.app.shopfee.databinding.ItemAdminOrderBinding
import com.app.shopfee.databinding.ItemOrderBinding
import com.app.shopfee.model.Order
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.DateTimeUtils
import com.app.shopfee.utils.GlideUtils

class AdminOderAdapter2(private var context: Context?,
                        private val listOrder: List<Order>?,
                        private val iClickOrderListener : IClickOrderListener
) : RecyclerView.Adapter<AdminOderAdapter2.AdminOrderViewHolder>() {

    interface IClickOrderListener {
        fun onClickTrackingOrder(orderId: Long)
        fun onClickReceiptOrder(order: Order)
    }

    class AdminOrderViewHolder(val mItemOderBinding: ItemAdminOrderBinding) :
        RecyclerView.ViewHolder(mItemOderBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminOrderViewHolder {
        val mItemOderBinding =
            ItemAdminOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminOrderViewHolder(mItemOderBinding)
    }

    override fun getItemCount(): Int {
        return listOrder!!.size
    }

    fun release() {
        if (context != null) context = null
    }

    override fun onBindViewHolder(holder: AdminOrderViewHolder, position: Int) {
        val order = listOrder!![position]
        holder.mItemOderBinding.tvId.text = order.id.toString()
        val strTotal = order.total.toString() + Constant.CURRENCY
        holder.mItemOderBinding.labelPayment.text = strTotal
        holder.mItemOderBinding.tvMenu.text = order.getListDrinksName()
        holder.mItemOderBinding.tvAddress.text = order.address?.address
        holder.mItemOderBinding.tvName.text = order.address?.name
        holder.mItemOderBinding.tvDate.text = DateTimeUtils.convertTimeStampToDate(order.dateTime!!.toLong())

        if (Order.STATUS_ARRIVED == order.status) {
                holder.mItemOderBinding.tvSuccess.visibility = View.VISIBLE
                holder.mItemOderBinding.tvSuccess.text = context!!.getString(R.string.label_arriver)
            holder.mItemOderBinding.layoutItem.setBackgroundColor(context!!.resources.getColor(color.black_overlay))
                holder.mItemOderBinding.tvAction.setOnClickListener {
                    iClickOrderListener.onClickReceiptOrder(order)
                }
        } else {
            holder.mItemOderBinding.tvSuccess.visibility = View.GONE
            holder.mItemOderBinding.tvAction.text =
                context!!.getString(R.string.label_receipt_order2)
            holder.mItemOderBinding.layoutItem.setBackgroundColor(context!!.resources.getColor(color.white))
            holder.mItemOderBinding.tvAction.setOnClickListener {
                iClickOrderListener.onClickReceiptOrder(order)
            }
        }
    }
}