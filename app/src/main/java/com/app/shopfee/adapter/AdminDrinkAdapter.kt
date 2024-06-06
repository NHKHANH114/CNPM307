package com.app.shopfee.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.shopfee.R
import com.app.shopfee.databinding.ItemAdminDrinkBinding
import com.app.shopfee.listener.AdminIClickDrinkListener


import com.app.shopfee.model.Drink
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.GlideUtils

class AdminDrinkAdapter(
    private val listDrink: List<Drink>?,
    private val iClickDrinkListener: AdminIClickDrinkListener

) : RecyclerView.Adapter<AdminDrinkAdapter.DrinkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrinkViewHolder {
        val mItemAdminDrinkBinding = ItemAdminDrinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DrinkViewHolder(mItemAdminDrinkBinding)
    }

    override fun onBindViewHolder(holder: DrinkViewHolder, position: Int) {
        val drink = listDrink!![position]
        GlideUtils.loadUrl(drink.image, holder.mItemAdminDrinkBinding.imgDrink)
        holder.mItemAdminDrinkBinding.tvName.text = drink.name
        holder.mItemAdminDrinkBinding.tvDescription.text = drink.description
        holder.mItemAdminDrinkBinding.tvRate.text = drink.rate.toString()
        if (drink.sale <= 0) {
            holder.mItemAdminDrinkBinding.tvPrice.visibility = View.GONE
            holder.mItemAdminDrinkBinding.tvSaleOff.visibility = View.GONE
            val strPrice = drink.price.toString() + Constant.CURRENCY
            holder.mItemAdminDrinkBinding.tvPriceSale.text = strPrice
        } else {
            holder.mItemAdminDrinkBinding.tvPrice.visibility = View.VISIBLE
            val strOldPrice = drink.price.toString() + Constant.CURRENCY
            val strSale = "Giảm " + drink.sale + "%"
            holder.mItemAdminDrinkBinding.tvSaleOff.text = strSale
            holder.mItemAdminDrinkBinding.tvPrice.text = strOldPrice
            holder.mItemAdminDrinkBinding.tvPrice.paintFlags = holder.mItemAdminDrinkBinding.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            val strRealPrice = drink.realPrice.toString() + Constant.CURRENCY
            holder.mItemAdminDrinkBinding.tvPriceSale.text = strRealPrice
        }
        holder.mItemAdminDrinkBinding.layoutItem.setOnClickListener {
            iClickDrinkListener.onClickDrinkItem(
                drink
            )
        }
        holder.mItemAdminDrinkBinding.imgEditAdmin.setOnClickListener { iClickDrinkListener.onClickUpdateFood(drink) }
        holder.mItemAdminDrinkBinding.imgDeleteAdmin.setOnClickListener { iClickDrinkListener.onClickDeleteFood(drink) }
    }

    override fun getItemCount(): Int {
        return listDrink?.size ?: 0
    }

//    class DrinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val imgDrink: ImageView
//        val tvName: TextView
//        val tvPrice: TextView
//        val tvPriceSale: TextView
//        val tvDescription: TextView
//        val tvRate: TextView
//        val layoutItem: LinearLayout
//        val imgDelete : ImageView
//        val imgEdit : ImageView
//
//        init {
//            imgDrink = itemView.findViewById(R.id.img_drink)
//            tvName = itemView.findViewById(R.id.tv_name)
//            tvPrice = itemView.findViewById(R.id.tv_price)
//            tvPriceSale = itemView.findViewById(R.id.tv_price_sale)
//            tvDescription = itemView.findViewById(R.id.tv_description)
//            tvRate = itemView.findViewById(R.id.tv_rate)
//            layoutItem = itemView.findViewById(R.id.layout_item)
//            imgDelete = itemView.findViewById(R.id.img_delete_admin)
//            imgEdit = itemView.findViewById(R.id.img_edit_admin)
//        }
//    }

    class DrinkViewHolder(val mItemAdminDrinkBinding: ItemAdminDrinkBinding) : RecyclerView.ViewHolder(mItemAdminDrinkBinding.root)
}