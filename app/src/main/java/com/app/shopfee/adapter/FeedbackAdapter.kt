package com.app.shopfee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shopfee.databinding.ItemFeedbackBinding
import com.app.shopfee.model.Feedback


class FeedbackAdapter(private val mListFeedback: List<Feedback>?) : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val itemFeedbackBinding = ItemFeedbackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return FeedbackViewHolder(itemFeedbackBinding)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = mListFeedback!![position]
        holder.mItemFeedbackBinding.tvEmail.text = feedback.email
        holder.mItemFeedbackBinding.tvFeedback.text = feedback.comment
    }

    override fun getItemCount(): Int {
        return mListFeedback?.size ?: 0
    }

    class FeedbackViewHolder(val mItemFeedbackBinding: ItemFeedbackBinding) : RecyclerView.ViewHolder(mItemFeedbackBinding.root)
}