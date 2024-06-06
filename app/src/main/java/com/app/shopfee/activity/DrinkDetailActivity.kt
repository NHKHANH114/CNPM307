package com.app.shopfee.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.shopfee.MyApplication
import com.app.shopfee.R
import com.app.shopfee.adapter.ToppingAdapter
import com.app.shopfee.database.DrinkDatabase
import com.app.shopfee.databinding.ActivityDrinkDetailBinding
import com.app.shopfee.event.DisplayCartEvent
import com.app.shopfee.model.Drink
import com.app.shopfee.model.RatingReview
import com.app.shopfee.model.Topping
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.GlideUtils
import com.app.shopfee.utils.GlobalFunction
import com.app.shopfee.utils.GlobalFunction.showToastMessage
import com.app.shopfee.utils.StringUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus

class DrinkDetailActivity : BaseActivity() {


    private var mDrinkId = 0
    private var mDrinkOld: Drink? = null
    private var mDrink: Drink? = null
    private var currentVariant: String? = Topping.VARIANT_ICE
    private var currentSize: String? = Topping.SIZE_REGULAR
    private var currentSugar: String? = Topping.SUGAR_NORMAL
    private var currentIce: String? = Topping.ICE_NORMAL
    private var listTopping: MutableList<Topping>? = null
    private var toppingAdapter: ToppingAdapter? = null
    private var variantText = ""
    private var sizeText = ""
    private var sugarText = ""
    private var iceText = ""
    private var toppingIdsText = ""

    private var mActivityDrinkDetailBinding : ActivityDrinkDetailBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityDrinkDetailBinding = ActivityDrinkDetailBinding.inflate(layoutInflater)
        setContentView(mActivityDrinkDetailBinding!!.root)

        getDataIntent()

        getDrinkDetailFromFirebase()
    }

    private fun getDataIntent() {
        val bundle = intent.extras ?: return
        mDrinkId = bundle.getInt(Constant.DRINK_ID)
        if (bundle[Constant.DRINK_OBJECT] != null) {
            mDrinkOld = bundle[Constant.DRINK_OBJECT] as Drink?
        }
    }


    private fun getDrinkDetailFromFirebase() {
        showProgressDialog(true)
        MyApplication[this].getDrinkDetailDatabaseReference(mDrinkId)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    showProgressDialog(false)
                    mDrink = snapshot.getValue(Drink::class.java)
                    if (mDrink == null) return
                    initToolbar()
                    initData()
                    initListener()
                    getListToppingFromFirebase()
                }

                override fun onCancelled(error: DatabaseError) {
                    showProgressDialog(false)
                    showToastMessage(getString(R.string.msg_get_date_error))
                }
            })
    }

    private fun initToolbar() {
        mActivityDrinkDetailBinding!!.toolbar.imgToolbarBack.setOnClickListener { finish() }
        mActivityDrinkDetailBinding!!.toolbar.tvToolbarTitle.text = mDrink?.name
        mActivityDrinkDetailBinding!!.toolbar.imgToolbarBack.visibility = View.VISIBLE
    }

    private fun initData() {
        if (mDrink == null) return
        GlideUtils.loadUrlBanner(mDrink?.banner, mActivityDrinkDetailBinding!!.imgDrink)
        mActivityDrinkDetailBinding!!.tvName.text = mDrink?.name
        val strPrice = mDrink?.realPrice.toString() + Constant.CURRENCY
        mActivityDrinkDetailBinding!!.tvPriceSale.text = strPrice
        mActivityDrinkDetailBinding!!.tvDescription.text = mDrink?.description
        if (mDrinkOld != null) {
            mDrink?.count = mDrinkOld!!.count
        } else {
            mDrink?.count = 1
        }
        mActivityDrinkDetailBinding!!.tvCount.text = mDrink?.count.toString()
        mActivityDrinkDetailBinding!!.tvRate.text = mDrink?.rate.toString()
        val strCountReview = "(" + mDrink?.countReviews + ")"
        mActivityDrinkDetailBinding!!.tvCountReview.text = strCountReview
        if (mDrinkOld != null) {
            if (StringUtil.isEmpty(mDrinkOld?.toppingIds)) calculatorTotalPrice()
        } else {
            calculatorTotalPrice()
        }
        if (mDrinkOld != null) {
            setValueToppingVariant(mDrinkOld?.variant)
            setValueToppingSize(mDrinkOld?.size)
            setValueToppingSugar(mDrinkOld?.sugar)
            setValueToppingIce(mDrinkOld?.ice)
            mActivityDrinkDetailBinding!!.edtNotes.setText(mDrinkOld?.note)
        } else {
            setValueToppingVariant(Topping.VARIANT_ICE)
            setValueToppingSize(Topping.SIZE_REGULAR)
            setValueToppingSugar(Topping.SUGAR_NORMAL)
            setValueToppingIce(Topping.ICE_NORMAL)
        }
    }

    private fun initListener() {
        mActivityDrinkDetailBinding!!.tvSub.setOnClickListener {
            val count = mActivityDrinkDetailBinding!!.tvCount.text.toString().toInt()
            if (count <= 1) {
                return@setOnClickListener
            }
            val newCount = mActivityDrinkDetailBinding!!.tvCount.text.toString().toInt() - 1
            mActivityDrinkDetailBinding!!.tvCount.text = newCount.toString()
            calculatorTotalPrice()
        }
        mActivityDrinkDetailBinding!!.tvAdd.setOnClickListener {
            val newCount = mActivityDrinkDetailBinding!!.tvCount.text.toString().toInt() + 1
            mActivityDrinkDetailBinding!!.tvCount.text = newCount.toString()
            calculatorTotalPrice()
        }
        mActivityDrinkDetailBinding!!.tvVariantIce.setOnClickListener {
            if (Topping.VARIANT_ICE != currentVariant) {
                setValueToppingVariant(Topping.VARIANT_ICE)
            }
        }
        mActivityDrinkDetailBinding!!.tvVariantHot.setOnClickListener {
            if (Topping.VARIANT_HOT != currentVariant) {
                setValueToppingVariant(Topping.VARIANT_HOT)
            }
        }
        mActivityDrinkDetailBinding!!.tvSizeRegular.setOnClickListener {
            if (Topping.SIZE_REGULAR != currentSize) {
                setValueToppingSize(Topping.SIZE_REGULAR)
            }
        }
        mActivityDrinkDetailBinding!!.tvSizeMedium.setOnClickListener {
            if (Topping.SIZE_MEDIUM != currentSize) {
                setValueToppingSize(Topping.SIZE_MEDIUM)
            }
        }
        mActivityDrinkDetailBinding!!.tvSizeLarge.setOnClickListener {
            if (Topping.SIZE_LARGE != currentSize) {
                setValueToppingSize(Topping.SIZE_LARGE)
            }
        }
        mActivityDrinkDetailBinding!!.tvSugarNormal.setOnClickListener {
            if (Topping.SUGAR_NORMAL != currentSugar) {
                setValueToppingSugar(Topping.SUGAR_NORMAL)
            }
        }
        mActivityDrinkDetailBinding!!.tvSugarLess.setOnClickListener {
            if (Topping.SUGAR_LESS != currentSugar) {
                setValueToppingSugar(Topping.SUGAR_LESS)
            }
        }
        mActivityDrinkDetailBinding!!.tvIceNormal.setOnClickListener {
            if (Topping.ICE_NORMAL != currentIce) {
                setValueToppingIce(Topping.ICE_NORMAL)
            }
        }
        mActivityDrinkDetailBinding!!.tvIceLess.setOnClickListener {
            if (Topping.ICE_LESS != currentIce) {
                setValueToppingIce(Topping.ICE_LESS)
            }
        }
        mActivityDrinkDetailBinding!!.layoutRatingAndReview.setOnClickListener {
            val bundle = Bundle()
            val ratingReview = RatingReview(
                RatingReview.TYPE_RATING_REVIEW_DRINK,
                mDrink?.id.toString()
            )
            bundle.putSerializable(Constant.RATING_REVIEW_OBJECT, ratingReview)
            GlobalFunction.startActivity(
                this@DrinkDetailActivity,
                RatingReviewActivity::class.java, bundle
            )
        }
        mActivityDrinkDetailBinding!!.tvAddOrder.setOnClickListener {
            mDrink?.option = getAllOption()
            mDrink?.variant = currentVariant
            mDrink?.size = currentSize
            mDrink?.sugar = currentSugar
            mDrink?.ice = currentIce
            mDrink?.toppingIds = toppingIdsText
            val notes = mActivityDrinkDetailBinding!!.edtNotes.text.toString().trim { it <= ' ' }
            if (!StringUtil.isEmpty(notes)) {
                mDrink?.note = notes
            }
            if (!isDrinkInCart()) {
                DrinkDatabase.getInstance(this@DrinkDetailActivity)!!.drinkDAO()
                    .insertDrink(mDrink)
            } else {
                DrinkDatabase.getInstance(this@DrinkDetailActivity)!!.drinkDAO()
                    .updateDrink(mDrink)
            }
            GlobalFunction.startActivity(this@DrinkDetailActivity, CartActivity::class.java)
            EventBus.getDefault().post(DisplayCartEvent())
            finish()
        }
    }

    private fun setValueToppingVariant(type: String?) {
        currentVariant = type
        when (type) {
            Topping.VARIANT_ICE -> {
                mActivityDrinkDetailBinding!!.tvVariantIce.setBackgroundResource(R.drawable.bg_main_corner_6)
                mActivityDrinkDetailBinding!!.tvVariantIce.setTextColor(ContextCompat.getColor(this, R.color.white))
                mActivityDrinkDetailBinding!!.tvVariantHot.setBackgroundResource(R.drawable.bg_white_corner_6_border_main)
                mActivityDrinkDetailBinding!!.tvVariantHot.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                variantText =
                    getString(R.string.label_variant) + " " + mActivityDrinkDetailBinding!!.tvVariantIce.text.toString()
            }
            Topping.VARIANT_HOT -> {
                mActivityDrinkDetailBinding!!.tvVariantIce.setBackgroundResource(R.drawable.bg_white_corner_6_border_main)
                mActivityDrinkDetailBinding!!.tvVariantIce.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                mActivityDrinkDetailBinding!!.tvVariantHot.setBackgroundResource(R.drawable.bg_main_corner_6)
                mActivityDrinkDetailBinding!!.tvVariantHot.setTextColor(ContextCompat.getColor(this, R.color.white))
                variantText =
                    getString(R.string.label_variant) + " " + mActivityDrinkDetailBinding!!.tvVariantHot.text.toString()
            }
        }
    }

    private fun setValueToppingSize(type: String?) {
        currentSize = type
        when (type) {
            Topping.SIZE_REGULAR -> {
                mActivityDrinkDetailBinding!!.tvSizeRegular.setBackgroundResource(R.drawable.bg_main_corner_6)
                mActivityDrinkDetailBinding!!.tvSizeRegular.setTextColor(ContextCompat.getColor(this, R.color.white))
                mActivityDrinkDetailBinding!!.tvSizeMedium.setBackgroundResource(R.drawable.bg_white_corner_6_border_main)
                mActivityDrinkDetailBinding!!.tvSizeMedium.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                mActivityDrinkDetailBinding!!.tvSizeLarge.setBackgroundResource(R.drawable.bg_white_corner_6_border_main)
                mActivityDrinkDetailBinding!!.tvSizeLarge.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                sizeText = getString(R.string.label_size) + " " + mActivityDrinkDetailBinding!!.tvSizeRegular.text.toString()
            }
            Topping.SIZE_MEDIUM -> {
                mActivityDrinkDetailBinding!!.tvSizeRegular.setBackgroundResource(R.drawable.bg_white_corner_6_border_main)
                mActivityDrinkDetailBinding!!.tvSizeRegular.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                mActivityDrinkDetailBinding!!.tvSizeMedium.setBackgroundResource(R.drawable.bg_main_corner_6)
                mActivityDrinkDetailBinding!!.tvSizeMedium.setTextColor(ContextCompat.getColor(this, R.color.white))
                mActivityDrinkDetailBinding!!.tvSizeLarge.setBackgroundResource(R.drawable.bg_white_corner_6_border_main)
                mActivityDrinkDetailBinding!!.tvSizeLarge.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                sizeText = getString(R.string.label_size) + " " + mActivityDrinkDetailBinding!!.tvSizeMedium.text.toString()
            }
            Topping.SIZE_LARGE -> {
                mActivityDrinkDetailBinding!!.tvSizeRegular.setBackgroundResource(R.drawable.bg_white_corner_6_border_main)
                mActivityDrinkDetailBinding!!.tvSizeRegular.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                mActivityDrinkDetailBinding!!.tvSizeMedium.setBackgroundResource(R.drawable.bg_white_corner_6_border_main)
                mActivityDrinkDetailBinding!!.tvSizeMedium.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                mActivityDrinkDetailBinding!!.tvSizeLarge.setBackgroundResource(R.drawable.bg_main_corner_6)
                mActivityDrinkDetailBinding!!.tvSizeLarge.setTextColor(ContextCompat.getColor(this, R.color.white))
                sizeText = (mActivityDrinkDetailBinding!!.tvSizeLarge.text.toString() + " "
                        + getString(R.string.label_size))
            }
        }
    }

    private fun setValueToppingSugar(type: String?) {
        currentSugar = type
        when (type) {
            Topping.SUGAR_NORMAL -> {
                mActivityDrinkDetailBinding!!.tvSugarNormal.setBackgroundResource(R.drawable.bg_main_corner_6)
                mActivityDrinkDetailBinding!!.tvSugarNormal.setTextColor(ContextCompat.getColor(this, R.color.white))
                mActivityDrinkDetailBinding!!.tvSugarLess.setBackgroundResource(R.drawable.bg_white_corner_6_border_main)
                mActivityDrinkDetailBinding!!.tvSugarLess.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                sugarText = (mActivityDrinkDetailBinding!!.tvSugarNormal.text.toString() + " "
                        + getString(R.string.label_sugar))
            }
            Topping.SUGAR_LESS -> {
                mActivityDrinkDetailBinding!!.tvSugarNormal.setBackgroundResource(R.drawable.bg_white_corner_6_border_main)
                mActivityDrinkDetailBinding!!.tvSugarNormal.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                mActivityDrinkDetailBinding!!.tvSugarLess.setBackgroundResource(R.drawable.bg_main_corner_6)
                mActivityDrinkDetailBinding!!.tvSugarLess.setTextColor(ContextCompat.getColor(this, R.color.white))
                sugarText = (mActivityDrinkDetailBinding!!.tvSugarLess?.text.toString() + " "
                        + getString(R.string.label_sugar))
            }
        }
    }

    private fun setValueToppingIce(type: String?) {
        currentIce = type
        when (type) {
            Topping.ICE_NORMAL -> {
                mActivityDrinkDetailBinding!!.tvIceNormal.setBackgroundResource(R.drawable.bg_main_corner_6)
                mActivityDrinkDetailBinding!!.tvIceNormal.setTextColor(ContextCompat.getColor(this, R.color.white))
                mActivityDrinkDetailBinding!!.tvIceLess.setBackgroundResource(R.drawable.bg_white_corner_6_border_main)
                mActivityDrinkDetailBinding!!.tvIceLess.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                iceText = mActivityDrinkDetailBinding!!.tvIceNormal.text.toString() + " " + getString(R.string.label_ice)
            }
            Topping.ICE_LESS -> {
                mActivityDrinkDetailBinding!!.tvIceNormal.setBackgroundResource(R.drawable.bg_white_corner_6_border_main)
                mActivityDrinkDetailBinding!!.tvIceNormal.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                mActivityDrinkDetailBinding!!.tvIceLess.setBackgroundResource(R.drawable.bg_main_corner_6)
                mActivityDrinkDetailBinding!!.tvIceLess.setTextColor(ContextCompat.getColor(this, R.color.white))
                iceText = mActivityDrinkDetailBinding!!.tvIceLess.text.toString() + " " + getString(R.string.label_ice)
            }
        }
    }

    private fun getListToppingFromFirebase() {
        MyApplication[this].getToppingDatabaseReference()
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (listTopping != null) {
                        listTopping!!.clear()
                    } else {
                        listTopping = ArrayList()
                    }
                    for (dataSnapshot in snapshot.children) {
                        val topping = dataSnapshot.getValue(Topping::class.java)
                        if (topping != null) {
                            listTopping!!.add(topping)
                        }
                    }
                    displayListTopping()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun displayListTopping() {
        val linearLayoutManager = LinearLayoutManager(this)
        mActivityDrinkDetailBinding!!.rcvTopping.layoutManager = linearLayoutManager
        toppingAdapter = ToppingAdapter(listTopping, object : ToppingAdapter.IClickToppingListener {
            override fun onClickToppingItem(topping: Topping) {
                handleClickItemTopping(topping)
            }
        })
        mActivityDrinkDetailBinding!!.rcvTopping.adapter = toppingAdapter
        handleSetToppingDrinkOld()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleSetToppingDrinkOld() {
        if (mDrinkOld == null || StringUtil.isEmpty(mDrinkOld?.toppingIds)) return
        if (listTopping == null || listTopping!!.isEmpty()) return
        val tempId = mDrinkOld?.toppingIds!!.split(",").toTypedArray()
        for (s in tempId) {
            for (topping in listTopping!!) {
                if (topping.id == s.toInt()) {
                    topping.isSelected = true
                    break
                }
            }
        }
        if (toppingAdapter != null) toppingAdapter!!.notifyDataSetChanged()
        calculatorTotalPrice()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleClickItemTopping(topping: Topping) {
        for (toppingEntity in listTopping!!) {
            if (toppingEntity.id == topping.id) {
                toppingEntity.isSelected = !toppingEntity.isSelected
            }
        }
        if (toppingAdapter != null) toppingAdapter!!.notifyDataSetChanged()
        calculatorTotalPrice()
    }

    private fun calculatorTotalPrice() {
        val count = mActivityDrinkDetailBinding!!.tvCount.text.toString().trim { it <= ' ' }.toInt()
        val priceOneDrink = mDrink?.realPrice?.plus(getTotalPriceTopping())
        val totalPrice = priceOneDrink?.times(count)
        val strTotalPrice = totalPrice.toString() + Constant.CURRENCY
        mActivityDrinkDetailBinding!!.tvTotal.text = strTotalPrice
        mDrink?.count = count
        mDrink?.priceOneDrink = priceOneDrink!!
        mDrink?.totalPrice = totalPrice!!
    }

    private fun getTotalPriceTopping(): Int {
        if (listTopping == null || listTopping!!.isEmpty()) return 0
        var total = 0
        for (topping in listTopping!!) {
            if (topping.isSelected) {
                total += topping.price
            }
        }
        return total
    }

    private fun getAllToppingSelected(): String {
        if (listTopping == null || listTopping!!.isEmpty()) return ""
        var strTopping = ""
        for (topping in listTopping!!) {
            if (topping.isSelected) {
                if (StringUtil.isEmpty(strTopping)) {
                    strTopping += topping.name
                    toppingIdsText += topping.id.toString()
                } else {
                    strTopping += ", " + topping.name
                }
                toppingIdsText += if (StringUtil.isEmpty(toppingIdsText)) {
                    topping.id.toString()
                } else {
                    "," + topping.id
                }
            }
        }
        return strTopping
    }

    private fun isDrinkInCart(): Boolean {
        val list: MutableList<Drink>? = DrinkDatabase.getInstance(this)!!
            .drinkDAO().checkDrinkInCart(mDrink!!.id.toInt())
        return list != null && list.isNotEmpty()
    }

    private fun getAllOption(): String {
        var option = "$variantText, $sizeText, $sugarText, $iceText"
        val allToppingSelected = getAllToppingSelected()
        if (!StringUtil.isEmpty(allToppingSelected)) {
            option += ", $allToppingSelected"
        }
        val notes = mActivityDrinkDetailBinding!!.edtNotes.text.toString().trim { it <= ' ' }
        if (!StringUtil.isEmpty(notes)) {
            option += ", $notes"
        }
        return option
    }
}