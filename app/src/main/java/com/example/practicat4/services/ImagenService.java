package com.example.practicat4.services;

import com.example.practicat4.entities.Imagen;
import com.example.practicat4.entities.ImagenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ImagenService {

    //Guardar
    @Headers("Authorization: Client-ID 8bcc638875f89d9")
    @POST("3/image")
    Call<ImagenResponse> enviarFoto(@Body Imagen img);

}
