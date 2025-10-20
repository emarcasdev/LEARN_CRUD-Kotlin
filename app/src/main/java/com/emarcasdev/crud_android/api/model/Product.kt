package com.emarcasdev.crud_android.api.model

import com.google.gson.annotations.SerializedName

data class Product (
    @SerializedName("_id") val id: String? = null,
    val name: String,
    val category: String,
    val stock: Int
)

data class ProductBody(
    val name: String,
    val category: String,
    val stock: Int
)

data class ProductResponse (
    val message: String,
    val product: Product
)