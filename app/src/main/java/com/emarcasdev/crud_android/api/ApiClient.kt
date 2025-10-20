package com.emarcasdev.crud_android.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Ruta para conectar el emulador con la API en local
    private val URL = "http://10.0.2.2:3000/";

    // Usamos HTTP por defecto
    private val httpClient = OkHttpClient();

    // Creamos el retrofit para poder llamar a la API
    private val retrofit = Retrofit.Builder().baseUrl(URL).client(httpClient)
        .addConverterFactory(GsonConverterFactory.create()).build();

    // Creamos el servicio para llamar a los endpoints de la API
    val service: ApiService  = retrofit.create(ApiService::class.java);
}