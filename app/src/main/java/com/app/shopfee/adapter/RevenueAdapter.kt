package com.app.shopfee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shopfee.databinding.ItemRevenueBinding
import com.app.shopfee.model.Order
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.DateTimeUtils.convertTimeStampToDate_2

class RevenueAdapter (private val mListOrder: List<Order>?) : RecyclerView.Adapter<RevenueAdapter.RevenueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RevenueViewHolder {
        val itemRevenueBinding = ItemRevenueBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return RevenueViewHolder(itemRevenueBinding)
    }

    override fun onBindViewHolder(holder: RevenueViewHolder, position: Int) {
        val order = mListOrder!![position]
        holder.mItemRevenueBinding.tvId.text = order.id.toString()
        holder.mItemRevenueBinding.tvDate.text = convertTimeStampToDate_2(order.id)
        val strAmount: String = "" + order.total + Constant.CURRENCY
        holder.mItemRevenueBinding.tvTotalAmount.text = strAmount
    }

    override fun getItemCount(): Int {
        return mListOrder?.size ?: 0
    }

    class RevenueViewHolder(val mItemRevenueBinding: ItemRevenueBinding) : RecyclerView.ViewHolder(mItemRevenueBinding.root)
}