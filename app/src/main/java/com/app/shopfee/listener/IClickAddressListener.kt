package com.app.shopfee.listener

import com.app.shopfee.model.Address

interface IClickAddressListener {
    fun onClickAddressItem(address: Address)
    fun onClickDeleteAddressItem(address: Address)
}