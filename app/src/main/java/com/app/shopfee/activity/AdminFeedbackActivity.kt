package com.app.shopfee.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.shopfee.MyApplication
import com.app.shopfee.R
import com.app.shopfee.adapter.FeedbackAdapter
import com.app.shopfee.databinding.ActivityAdminFeedbackBinding
import com.app.shopfee.model.Feedback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class AdminFeedbackActivity : AppCompatActivity() {
    private var mActivityAdminFeedbackBinding : ActivityAdminFeedbackBinding? = null
    private var mListFeedback: MutableList<Feedback>? = null
    private var mFeedbackAdapter: FeedbackAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAdminFeedbackBinding = ActivityAdminFeedbackBinding.inflate(layoutInflater)
        initView()
        mActivityAdminFeedbackBinding!!.toolbar.imgToolbarBack.visibility = View.VISIBLE
        mActivityAdminFeedbackBinding!!.toolbar.imgToolbarBack.setOnClickListener { finish() }
        mActivityAdminFeedbackBinding!!.toolbar.tvToolbarTitle.text = getString(R.string.feedback)
        getListFeedback()
        setContentView(mActivityAdminFeedbackBinding!!.root)
    }




    private fun initView() {
        val linearLayoutManager = LinearLayoutManager(this)
        mActivityAdminFeedbackBinding!!.rcvFeedback.layoutManager = linearLayoutManager
    }



    fun getListFeedback() {

        MyApplication[this].getFeedbackDatabaseReference()
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (mListFeedback != null) {
                        mListFeedback!!.clear()
                    } else {
                        mListFeedback = ArrayList()
                    }
                    for (dataSnapshot in snapshot.children) {
                        val feedback = dataSnapshot.getValue(Feedback::class.java)
                        if (feedback != null) {
                            mListFeedback!!.add(0, feedback)
                        }
                    }
                    mFeedbackAdapter = FeedbackAdapter(mListFeedback)
                    mActivityAdminFeedbackBinding!!.rcvFeedback.adapter = mFeedbackAdapter
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}