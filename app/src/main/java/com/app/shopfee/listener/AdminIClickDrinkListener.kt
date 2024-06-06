package com.app.shopfee.listener

import com.app.shopfee.model.Drink

interface AdminIClickDrinkListener {
    fun onClickDrinkItem(drink: Drink)
    fun onClickUpdateFood(drink : Drink?)
    fun onClickDeleteFood(drink : Drink?)
}