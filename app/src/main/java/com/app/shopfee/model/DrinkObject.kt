package com.app.shopfee.model

import android.media.Image
import java.io.Serializable

class DrinkObject : Serializable {

    var id: Long = 0
    var name: String? = null
    var description: String? = null
    var price  = 0
    var sale  = 0
    var category_id  = 1
    var image: String? = null
    var banner: String? = null
    var featured = false

    constructor() {}

    constructor(id: Long, name: String?, description: String?, price: Int, sale: Int, category: Int,
                image: String?, banner: String?, popular: Boolean) {
        this.id = id
        this.name = name
        this.description = description
        this.price = price
        this.category_id = category
        this.sale = sale
        this.image = image
        this.banner = banner
        this.featured = popular
    }
}