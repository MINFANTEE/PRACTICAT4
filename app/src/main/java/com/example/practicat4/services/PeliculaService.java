package com.example.practicat4.services;

import com.example.practicat4.entities.Pelicula;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PeliculaService {

    @GET("peliculas")
    Call<List<Pelicula>> listPeliculas();

    //Guardar
    @POST("peliculas")
    Call<Void> create(@Body Pelicula pelicula);

    //Actualizar
    @PUT("peliculas/{idPeli}")
    Call<Void> update(@Body Pelicula pelicula, @Path("idPeli") int id);

    @DELETE("peliculas/{idPeli}")
    Call<Void> delete(@Path("idPeli") int id);
}
