package com.app.shopfee.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shopfee.R
import com.app.shopfee.databinding.ItemOrderBinding
import com.app.shopfee.model.Order
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.GlideUtils

class AdminOderAdapter(private var context: Context?,
                       private val listOrder: List<Order>?,
                       private val iClickOrderListener : IClickOrderListener
) : RecyclerView.Adapter<AdminOderAdapter.AdminOrderViewHolder>() {

    interface IClickOrderListener {
        fun onClickTrackingOrder(orderId: Long)
        fun onClickReceiptOrder(order: Order)
    }

    class AdminOrderViewHolder(val mItemOderBinding: ItemOrderBinding) :
        RecyclerView.ViewHolder(mItemOderBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminOrderViewHolder {
        val mItemOderBinding =
            ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        val firstDrinkOrder = order.drinks?.get(0)
        GlideUtils.loadUrl(firstDrinkOrder?.image, holder.mItemOderBinding.imgDrink)
//        holder.mItemOderBinding.tvOrderId.text = order.id.toString()
        val strTotal = order.total.toString() + Constant.CURRENCY
        holder.mItemOderBinding.tvTotal.text = strTotal
        holder.mItemOderBinding.tvDrinksName.text = order.getListDrinksName()
        val strQuantity =
            "(" + order.drinks?.size + " " + context!!.getString(R.string.label_item) + ")"
        holder.mItemOderBinding.tvQuantity.text = strQuantity
        if (Order.STATUS_COMPLETE == order.status) {
            holder.mItemOderBinding.tvSuccess.visibility = View.VISIBLE
            holder.mItemOderBinding.tvAction.text =
                context!!.getString(R.string.label_receipt_order)
//            holder.mItemOderBinding.layoutReview.visibility = View.VISIBLE
//            holder.mItemOderBinding.tvRate.text = order.rate.toString()
//            holder.mItemOderBinding.tvReview.text = order.review
            holder.mItemOderBinding.layoutAction.setOnClickListener {
                iClickOrderListener.onClickReceiptOrder(order)
            }
        }
        else if (Order.STATUS_ARRIVED == order.status) {
                holder.mItemOderBinding.tvSuccess.visibility = View.VISIBLE
                holder.mItemOderBinding.tvSuccess.text = context!!.getString(R.string.label_arriver)
                holder.mItemOderBinding.tvAction.text =
                    context!!.getString(R.string.label_receipt_order)
//                holder.mItemOderBinding.layoutReview.visibility = View.VISIBLE
//                holder.mItemOderBinding.tvRate.text = order.rate.toString()
//                holder.mItemOderBinding.tvReview.text = order.review
                holder.mItemOderBinding.layoutAction.setOnClickListener {
                    iClickOrderListener.onClickReceiptOrder(order)
                }
        } else {
            holder.mItemOderBinding.tvSuccess.visibility = View.GONE
            holder.mItemOderBinding.tvAction.text =
                context!!.getString(R.string.label_receipt_order)
//            holder.mItemOderBinding.layoutReview.visibility = View.GONE
            holder.mItemOderBinding.layoutAction.setOnClickListener {
                iClickOrderListener.onClickReceiptOrder(order)
            }
        }
    }
}