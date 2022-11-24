package com.example.practicat4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.practicat4.adapter.PeliculaAdapter;
import com.example.practicat4.entities.Pelicula;
import com.example.practicat4.services.PeliculaService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListarPeliculas extends AppCompatActivity {

    private RecyclerView rvPelicula;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_peliculas);


        rvPelicula = findViewById(R.id.rvPelicula);
        rvPelicula.setLayoutManager(new LinearLayoutManager(this));

        //Retrofit retrofit = new RetrofitFactory(this).build();
        Retrofit retro= new Retrofit.Builder()
                .baseUrl("https://6359bef538725a1746b71cf0.mockapi.io/")// -> Aquí va la URL sin el Path
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PeliculaService servise = retro.create(PeliculaService.class);

        servise.listPeliculas().enqueue(new Callback<List<Pelicula>>() {
            @Override
            public void onResponse(Call<List<Pelicula>> call, Response<List<Pelicula>> response) {

                rvPelicula.setAdapter(new PeliculaAdapter(response.body()));

                Log.i("MAIN_APP", "Response: " + response.body().size());

            }

            @Override
            public void onFailure(Call<List<Pelicula>> call, Throwable t) {
                Log.e("MAIN_APP", t.toString());
            }
        });
    }
}