package com.app.shopfee.activity


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.app.shopfee.MyApplication
import com.app.shopfee.R
import com.app.shopfee.databinding.ActivityAddDrinkBinding
import com.app.shopfee.model.Drink

import com.app.shopfee.model.DrinkObject
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.GlobalFunction.hideSoftKeyboard
import com.app.shopfee.utils.StringUtil.isEmpty
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.util.HashMap
import kotlin.random.Random

class AddDrinkActivity : AppCompatActivity() {

    private var mActivityAddDrinkBinding : ActivityAddDrinkBinding? = null
    private var isUpdate = false
    private var mDrink: Drink? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAddDrinkBinding = ActivityAddDrinkBinding.inflate(layoutInflater)
        getDataIntent()
        initView()
        initToolbar()
        mActivityAddDrinkBinding!!.toolbar.imgToolbarBack.visibility = View.VISIBLE
        mActivityAddDrinkBinding!!.toolbar.imgToolbarBack.setOnClickListener { finish() }
        mActivityAddDrinkBinding!!.btnAddOrEdit.setOnClickListener { addOrEditFood() }
        setContentView(mActivityAddDrinkBinding!!.root)
    }
    private fun getDataIntent() {
        val bundleReceived = intent.extras
        if (bundleReceived != null) {
            isUpdate = true
            mDrink = bundleReceived[Constant.KEY_INTENT_FOOD_OBJECT] as Drink?
        }
    }
    private fun initToolbar() {
        mActivityAddDrinkBinding!!.toolbar.imgToolbarBack.setOnClickListener { finish() }
        mActivityAddDrinkBinding!!.toolbar.tvToolbarTitle.text = "Thêm đồ uống"
        mActivityAddDrinkBinding!!.toolbar.imgToolbarBack.setOnClickListener { finish() }
        mActivityAddDrinkBinding!!.toolbar.imgToolbarBack.visibility = View.VISIBLE
    }

    private fun initView() {
        if (isUpdate) {
            mActivityAddDrinkBinding!!.btnAddOrEdit.text = "edit"
            mActivityAddDrinkBinding!!.edtName.setText(mDrink!!.name)
            mActivityAddDrinkBinding!!.edtDescription.setText(mDrink!!.description)
            mActivityAddDrinkBinding!!.edtPrice.setText(java.lang.String.valueOf(mDrink!!.price))
            mActivityAddDrinkBinding!!.edtDiscount.setText(java.lang.String.valueOf(mDrink!!.sale))
            mActivityAddDrinkBinding!!.edtImage.setText(mDrink!!.image)
            mActivityAddDrinkBinding!!.edtImageBanner.setText(mDrink!!.banner)
            mActivityAddDrinkBinding!!.chbPopular.isChecked = mDrink!!.isFeatured

        }
    }
    private fun addOrEditFood() {
        val strName = mActivityAddDrinkBinding!!.edtName.text.toString().trim { it <= ' ' }
        val strDescription = mActivityAddDrinkBinding!!.edtDescription.text.toString().trim { it <= ' ' }
        val strPrice = mActivityAddDrinkBinding!!.edtPrice.text.toString().trim { it <= ' ' }
        val strDiscount = mActivityAddDrinkBinding!!.edtDiscount.text.toString().trim { it <= ' ' }
        val strImage = mActivityAddDrinkBinding!!.edtImage.text.toString().trim { it <= ' ' }
        val strImageBanner = mActivityAddDrinkBinding!!.edtImageBanner.text.toString().trim { it <= ' ' }
        val isPopular = mActivityAddDrinkBinding!!.chbPopular.isChecked
        var strLoai = ""
        if (mActivityAddDrinkBinding!!.rdCaphe.isChecked){
            strLoai = "1"
        }
        else if (mActivityAddDrinkBinding!!.rdTrasua.isChecked){
            strLoai = "2"
        }
        else if (mActivityAddDrinkBinding!!.rdBanh.isChecked){
            strLoai = "3"
        }


        if (isEmpty(strName)) {
            Toast.makeText(this, getString(R.string.msg_name_food_require), Toast.LENGTH_SHORT).show()
            return
        }
        if (isEmpty(strDescription)) {
            Toast.makeText(this, getString(R.string.msg_description_food_require), Toast.LENGTH_SHORT).show()
            return
        }
        if (isEmpty(strPrice)) {
            Toast.makeText(this, getString(R.string.msg_price_food_require), Toast.LENGTH_SHORT).show()
            return
        }
        if (isEmpty(strDiscount)) {
            Toast.makeText(this, getString(R.string.msg_discount_food_require), Toast.LENGTH_SHORT).show()
            return
        }
        if (isEmpty(strImage)) {
            Toast.makeText(this, getString(R.string.msg_image_food_require), Toast.LENGTH_SHORT).show()
            return
        }
        if (isEmpty(strImageBanner)) {
            Toast.makeText(this, getString(R.string.msg_image_banner_food_require), Toast.LENGTH_SHORT).show()
            return
        }

        // Update food
        if (isUpdate) {
            //showProgressDialog(true)
            val map: MutableMap<String, Any> = HashMap()
            map["name"] = strName
            map["description"] = strDescription
            map["price"] = strPrice.toInt()
            map["sale"] = strDiscount.toInt()
            map["image"] = strImage
            map["banner"] = strImageBanner
            map["popular"] = isPopular

            MyApplication[this].getDrinkDatabaseReference()
                ?.child(mDrink!!.id.toString())?.updateChildren(map) { _: DatabaseError?, _: DatabaseReference? ->
                    //showProgressDialog(false)
                    Toast.makeText(this@AddDrinkActivity,
                        getString(R.string.msg_edit_food_success), Toast.LENGTH_SHORT).show()
                    hideSoftKeyboard(this)
                }
            return
        }


        val foodId: Long = randomBetween1And10000() // Đây là ID của món ăn
        val food = DrinkObject(foodId, strName, strDescription, strPrice.toInt(), strDiscount.toInt(), strLoai.toInt() , strImage, strImageBanner, isPopular)
        checkAndAddFood(foodId, food)

    }

    fun randomBetween1And10000(): Long {
        return Random.nextLong(1, 10001)
    }


    fun checkAndAddFood(foodId: Long, food: DrinkObject) {
        val databaseRef = MyApplication[this].getDrinkDatabaseReference()

        // Kiểm tra xem ID đã tồn tại hay chưa
        databaseRef?.child(foodId.toString())?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // ID đã tồn tại, hiển thị thông báo lỗi hoặc xử lý theo cách khác
                    Toast.makeText(this@AddDrinkActivity, "ID $foodId đã tồn tại.", Toast.LENGTH_SHORT).show()
                } else {
                    // ID không tồn tại, thêm dữ liệu vào database
                    databaseRef.child(foodId.toString()).setValue(food) { databaseError, _ ->
                        if (databaseError == null) {
                            // Thêm dữ liệu thành công
                            clearFieldsAndDisplayToast()
                        } else {
                            // Xảy ra lỗi khi thêm dữ liệu
                            Toast.makeText(this@AddDrinkActivity, "Lỗi khi thêm dữ liệu vào database: $databaseError", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Xảy ra lỗi khi truy cập database
                Toast.makeText(this@AddDrinkActivity, "Lỗi khi truy cập database: $databaseError", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun clearFieldsAndDisplayToast() {
        mActivityAddDrinkBinding!!.edtName.setText("")
        mActivityAddDrinkBinding!!.edtDescription.setText("")
        mActivityAddDrinkBinding!!.edtPrice.setText("")
        mActivityAddDrinkBinding!!.edtDiscount.setText("")
        mActivityAddDrinkBinding!!.edtImage.setText("")
        mActivityAddDrinkBinding!!.edtImageBanner.setText("")
        mActivityAddDrinkBinding!!.chbPopular.isChecked = false
        hideSoftKeyboard(this)
        Toast.makeText(this, getString(R.string.msg_add_food_success), Toast.LENGTH_SHORT).show()
    }
}