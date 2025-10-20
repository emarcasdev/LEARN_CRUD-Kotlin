package com.emarcasdev.crud_android.api

import com.emarcasdev.crud_android.api.model.Product
import com.emarcasdev.crud_android.api.model.ProductBody
import com.emarcasdev.crud_android.api.model.ProductResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Endpoint para recuperar todos los productos
    @GET("api/products")
    suspend fun getProducts(@Query("name") name: String? = null): Response<List<Product>>;

    // Endpoint para crear un nuevo producto
    @POST("api/product")
    suspend fun createProduct(@Body body: ProductBody): Response<ProductResponse>;

    // Endpoint para actualizar un producto
    @PUT("api/product/{id}")
    suspend fun editProduct(
        @Path("id") id: String,
        @Body body: ProductBody
    ): Response<ProductResponse>;

    // Endpoint para eliminar un producto
    @DELETE("api/product/{id}")
    suspend fun deleteProduct(@Path("id") id: String,): Response<ProductResponse>;
}