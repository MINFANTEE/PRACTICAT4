package com.example.practicat4.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.practicat4.FormularioPeliculas;
import com.example.practicat4.R;
import com.example.practicat4.entities.Pelicula;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PeliculaAdapter extends RecyclerView.Adapter {

    List<Pelicula> dataPelicula;

    public PeliculaAdapter(List<Pelicula> data){
        this.dataPelicula = data;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_pelicula, parent, false);
        return new PeliculaViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Pelicula pelicula = dataPelicula.get(position);

        ImageView imPelicula = holder.itemView.findViewById(R.id.imPelicula);
        TextView tvTitulo = holder.itemView.findViewById(R.id.tvTitulo);
        TextView tvSinopsis = holder.itemView.findViewById(R.id.tvSinopsis);

        Picasso.get().load(pelicula.imagen).into(imPelicula);
        tvTitulo.setText(pelicula.titulo);
        tvSinopsis.setText(pelicula.sinopsis);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(holder.itemView.getContext(), FormularioPeliculas.class);
                intent.putExtra("PELICULA_DATA", new Gson().toJson(pelicula));

                holder.itemView.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataPelicula.size();
    }

    public class PeliculaViewHolder extends RecyclerView.ViewHolder{

        public PeliculaViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}


