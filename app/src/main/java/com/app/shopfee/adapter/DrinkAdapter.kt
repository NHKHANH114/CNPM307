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
import com.app.shopfee.adapter.DrinkAdapter.DrinkViewHolder
import com.app.shopfee.databinding.ItemDrink1Binding
import com.app.shopfee.databinding.ItemDrinkBinding
import com.app.shopfee.listener.IClickDrinkListener
import com.app.shopfee.model.Drink
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.GlideUtils

class DrinkAdapter(
    private val listDrink: List<Drink>?,
    private val iClickDrinkListener: IClickDrinkListener
) : RecyclerView.Adapter<DrinkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrinkViewHolder {
       val mItemDrinkBinding = ItemDrink1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DrinkViewHolder(mItemDrinkBinding)
    }

    override fun onBindViewHolder(holder: DrinkViewHolder, position: Int) {
        val drink = listDrink!![position]
        GlideUtils.loadUrl(drink.image, holder.mItemDrinkBinding.imgDrink)
        holder.mItemDrinkBinding.tvName.text = drink.name
//        holder.mItemDrinkBinding.tvDescription.text = drink.description
        holder.mItemDrinkBinding.tvRate.text = drink.rate.toString()
        if (drink.sale <= 0) {
            //holder.mItemDrinkBinding.tvPrice.visibility = View.GONE
            holder.mItemDrinkBinding.tvSaleOff.visibility = View.GONE
            val strPrice = drink.price.toString() + Constant.CURRENCY
            holder.mItemDrinkBinding.tvPriceSale.text = strPrice
        } else {
            holder.mItemDrinkBinding.tvPrice.visibility = View.VISIBLE
            val strOldPrice = drink.price.toString() + Constant.CURRENCY
            holder.mItemDrinkBinding.tvPrice.text = strOldPrice
            val strSale = "Giảm " + drink.sale + "%"
            holder.mItemDrinkBinding.tvSaleOff.text = strSale
            holder.mItemDrinkBinding.tvPrice.paintFlags = holder.mItemDrinkBinding.tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            val strRealPrice = drink.realPrice.toString() + Constant.CURRENCY
            holder.mItemDrinkBinding.tvPriceSale.text = strRealPrice
        }
        holder.mItemDrinkBinding.layoutItem.setOnClickListener {
            iClickDrinkListener.onClickDrinkItem(
                drink
            )
        }
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
//
//        init {
//            imgDrink = itemView.findViewById(R.id.img_drink)
//            tvName = itemView.findViewById(R.id.tv_name)
//            tvPrice = itemView.findViewById(R.id.tv_price)
//            tvPriceSale = itemView.findViewById(R.id.tv_price_sale)
//            tvDescription = itemView.findViewById(R.id.tv_description)
//            tvRate = itemView.findViewById(R.id.tv_rate)
//            layoutItem = itemView.findViewById(R.id.layout_item)
//        }
//    }

    class DrinkViewHolder(val mItemDrinkBinding: ItemDrink1Binding) : RecyclerView.ViewHolder(mItemDrinkBinding.root)
}