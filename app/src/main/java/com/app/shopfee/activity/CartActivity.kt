package com.app.shopfee.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.shopfee.R
import com.app.shopfee.adapter.CartAdapter
import com.app.shopfee.adapter.CartAdapter.IClickCartListener
import com.app.shopfee.database.DrinkDatabase
import com.app.shopfee.databinding.ActivityCartBinding
import com.app.shopfee.event.AddressSelectedEvent
import com.app.shopfee.event.DisplayCartEvent
import com.app.shopfee.event.OrderSuccessEvent
import com.app.shopfee.event.PaymentMethodSelectedEvent
import com.app.shopfee.event.VoucherSelectedEvent
import com.app.shopfee.model.Address
import com.app.shopfee.model.Drink
import com.app.shopfee.model.DrinkOrder
import com.app.shopfee.model.Order
import com.app.shopfee.model.PaymentMethod
import com.app.shopfee.model.Voucher
import com.app.shopfee.prefs.DataStoreManager
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.GlobalFunction.startActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CartActivity : BaseActivity() {

    private var listDrinkCart: MutableList<Drink>? = null
    private var cartAdapter: CartAdapter? = null
    private var priceDrink = 0
    private var mAmount = 0
    private var paymentMethodSelected: PaymentMethod? = null
    private var addressSelected: Address? = null
    private var voucherSelected: Voucher? = null

    private var mActivityCartBinding : ActivityCartBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityCartBinding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(mActivityCartBinding!!.root)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initToolbar()
        initUi()
        initListener()
        initData()
    }

    private fun initToolbar() {
        mActivityCartBinding!!.toolbar.imgToolbarBack.setOnClickListener { finish() }
        mActivityCartBinding!!.toolbar.tvToolbarTitle.text = getString(R.string.label_cart)
        mActivityCartBinding!!.toolbar.imgToolbarBack.visibility = View.VISIBLE
    }

    private fun initUi() {
        val linearLayoutManager = LinearLayoutManager(this)
        mActivityCartBinding!!.rcvCart.layoutManager = linearLayoutManager

    }

    private fun initListener() {
        mActivityCartBinding!!.layoutAddOrder.setOnClickListener { finish() }
        mActivityCartBinding!!.layoutPaymentMethod.setOnClickListener {
            val bundle = Bundle()
            if (paymentMethodSelected != null) {
                bundle.putInt(Constant.PAYMENT_METHOD_ID, paymentMethodSelected!!.id)
            }
            startActivity(
                this@CartActivity,
                PaymentMethodActivity::class.java,
                bundle
            )
        }

        mActivityCartBinding!!.layoutAddress.setOnClickListener {
            val bundle = Bundle()
            if (addressSelected != null) {
                bundle.putLong(Constant.ADDRESS_ID, addressSelected!!.id)
            }
            startActivity(this@CartActivity, AddressActivity::class.java, bundle)
        }

        mActivityCartBinding!!.layoutVoucher.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constant.AMOUNT_VALUE, priceDrink)
            if (voucherSelected != null) {
                bundle.putInt(Constant.VOUCHER_ID, voucherSelected!!.id)
            }
            startActivity(this@CartActivity, VoucherActivity::class.java, bundle)
        }
        mActivityCartBinding!!.tvCheckout.setOnClickListener {
            if (listDrinkCart == null || listDrinkCart!!.isEmpty()) return@setOnClickListener

            if (paymentMethodSelected == null) {
                showToastMessage(getString(R.string.label_choose_payment_method))
                return@setOnClickListener
            }

            if (addressSelected == null) {
                showToastMessage(getString(R.string.label_choose_address))
                return@setOnClickListener
            }

            val orderBooking = Order()
            orderBooking.id = System.currentTimeMillis()
            orderBooking.userEmail = DataStoreManager.user?.email
            orderBooking.dateTime = System.currentTimeMillis().toString()
            val drinks: MutableList<DrinkOrder?> = ArrayList()
            for (drink in listDrinkCart!!) {
                drinks.add(
                    DrinkOrder(
                        drink.name, drink.option, drink.count,
                        drink.priceOneDrink, drink.image
                    )
                )
            }
            orderBooking.drinks = drinks
            orderBooking.price = priceDrink
            if (voucherSelected != null) {
                orderBooking.voucher = voucherSelected!!.getPriceDiscount(priceDrink)
            }
            orderBooking.total = mAmount
            orderBooking.paymentMethod = paymentMethodSelected!!.name
            orderBooking.address = addressSelected
            orderBooking.status = Order.STATUS_NEW
            val bundle = Bundle()
            bundle.putSerializable(Constant.ORDER_OBJECT, orderBooking)
            startActivity(this@CartActivity, PaymentActivity::class.java, bundle)
        }
    }

    private fun initData() {
        listDrinkCart = ArrayList()
        listDrinkCart = DrinkDatabase.getInstance(this)!!.drinkDAO().listDrinkCart
        if (listDrinkCart!!.isEmpty()) {
            return
        }
        cartAdapter = CartAdapter(listDrinkCart, object : IClickCartListener {
            override fun onClickDeleteItem(drink: Drink?, position: Int) {
                DrinkDatabase.getInstance(this@CartActivity)!!.drinkDAO()
                    .deleteDrink(drink)
                listDrinkCart?.removeAt(position)
                cartAdapter?.notifyItemRemoved(position)
                displayCountItemCart()
                calculateTotalPrice()
                EventBus.getDefault().post(DisplayCartEvent())
            }

            override fun onClickUpdateItem(drink: Drink?, position: Int) {
                DrinkDatabase.getInstance(this@CartActivity)!!.drinkDAO()
                    .updateDrink(drink)
                cartAdapter?.notifyItemChanged(position)
                calculateTotalPrice()
                EventBus.getDefault().post(DisplayCartEvent())
            }

            override fun onClickEditItem(drink: Drink) {
                val bundle = Bundle()
                bundle.putInt(Constant.DRINK_ID, drink.id.toInt())
                bundle.putSerializable(Constant.DRINK_OBJECT, drink)
                startActivity(
                    this@CartActivity,
                    DrinkDetailActivity::class.java,
                    bundle
                )
            }
        })
        mActivityCartBinding!!.rcvCart.adapter = cartAdapter
        calculateTotalPrice()
        displayCountItemCart()
    }

    private fun displayCountItemCart() {
        val strCountItem = "(" + listDrinkCart!!.size + " " + getString(R.string.label_item) + ")"
        mActivityCartBinding!!.tvCountItem.text = strCountItem
    }

    private fun calculateTotalPrice() {
        if (listDrinkCart == null || listDrinkCart!!.isEmpty()) {
            val strZero = 0.toString() + Constant.CURRENCY
            priceDrink = 0
            mActivityCartBinding!!.tvPriceDrink.text = strZero
            mAmount = 0
            mActivityCartBinding!!.tvAmount.text = strZero
            return
        }
        var totalPrice = 0
        for (drink in listDrinkCart!!) {
            totalPrice += drink.totalPrice
        }
        priceDrink = totalPrice
        val strPriceDrink = priceDrink.toString() + Constant.CURRENCY
        mActivityCartBinding!!.tvPriceDrink.text = strPriceDrink
        mAmount = totalPrice
        if (voucherSelected != null) {
            mAmount -= voucherSelected!!.getPriceDiscount(priceDrink)
        }
        val strAmount = mAmount.toString() + Constant.CURRENCY
        mActivityCartBinding!!.tvAmount.text = strAmount
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPaymentMethodSelectedEvent(event: PaymentMethodSelectedEvent) {
        if (event.paymentMethod != null) {
            paymentMethodSelected = event.paymentMethod
            mActivityCartBinding!!.tvPaymentMethod.text = paymentMethodSelected?.name
        } else {
            mActivityCartBinding!!.tvPaymentMethod.text = getString(R.string.label_no_payment_method)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddressSelectedEvent(event: AddressSelectedEvent) {
        if (event.address != null) {
            addressSelected = event.address
            mActivityCartBinding!!.tvAddress.text = addressSelected?.address
        } else {
            mActivityCartBinding!!.tvAddress.text = getString(R.string.label_no_address)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onVoucherSelectedEvent(event: VoucherSelectedEvent) {
        if (event.voucher != null) {
            voucherSelected = event.voucher
            mActivityCartBinding!!.tvVoucher.text = voucherSelected?.title
            mActivityCartBinding!!.tvNameVoucher.text = voucherSelected?.title
            val strPriceVoucher = ("-" + voucherSelected!!.getPriceDiscount(priceDrink)
                    + Constant.CURRENCY)
            mActivityCartBinding!!.tvPriceVoucher.text = strPriceVoucher
        } else {
            mActivityCartBinding!!.tvVoucher.text = getString(R.string.label_no_voucher)
            mActivityCartBinding!!.tvNameVoucher.text = getString(R.string.label_no_voucher)
            val strPriceVoucher = "-0" + Constant.CURRENCY
            mActivityCartBinding!!.tvPriceVoucher.text = strPriceVoucher
        }
        calculateTotalPrice()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderSuccessEvent(event: OrderSuccessEvent?) {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}