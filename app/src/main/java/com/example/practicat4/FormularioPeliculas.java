package com.example.practicat4;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.practicat4.entities.Imagen;
import com.example.practicat4.entities.ImagenResponse;
import com.example.practicat4.entities.Pelicula;
import com.example.practicat4.factories.RetrofitFactory;
import com.example.practicat4.services.ImagenService;
import com.example.practicat4.services.PeliculaService;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FormularioPeliculas extends AppCompatActivity {

    private final static int CAMERA_REQUEST = 1000;

    //public Pelicula pelicula = new Pelicula();

    public String link;
    private Pelicula pelicula = new Pelicula();
    private EditText etTitulo;
    private EditText etSinopsis;
    private EditText etImagenURL;
    private Button btnSavePelicula;
    private Button btnTakePhoto;

    private ImageView ivPhoto;

    private Retrofit retrofit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_peliculas);

        etTitulo = findViewById(R.id.etTitulo);
        etSinopsis = findViewById(R.id.etSinopsis);
        etImagenURL = findViewById(R.id.etImagenURL);

        btnSavePelicula = findViewById(R.id.btnSavePelicula);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);


        ivPhoto = findViewById(R.id.ivPhoto);



        Intent intent = getIntent();
        String peliculaJson = intent.getStringExtra("PELICULA_DATA");

       // Log.i("MAIN_APP", "animeJson:" + peliculaJson);

        if(peliculaJson != null){
            pelicula = new Gson().fromJson(peliculaJson, Pelicula.class);
            etTitulo.setText(pelicula.titulo);
            etSinopsis.setText(pelicula.sinopsis);
            etImagenURL.setText(pelicula.imagen);
        }



        //Boton para abrir la camara
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                    abrirCamara();
                }
                else{
                    requestPermissions(new String[] {Manifest.permission.CAMERA}, 100);
                }

            }
        });



        btnSavePelicula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePelicula();
            }
        });
    }


    private void savePelicula() {

        if(pelicula.titulo == "" || pelicula.sinopsis == "" || pelicula.imagen == ""){

            Toast.makeText(this, "LLenar datos obligatorios", Toast.LENGTH_SHORT).show();
            return;

        }
        pelicula.titulo = etTitulo.getText().toString();
        pelicula.sinopsis = etSinopsis.getText().toString();
        pelicula.imagen = etImagenURL.getText().toString();


        Log.i("MAIN_ACTIVITY", new Gson().toJson(pelicula));

        if(pelicula.id == 0){
            callCreateAPI(pelicula);
        }
        else{
            CallUpdateAPI(pelicula);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1001);

    }

    private void abrirCamara() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivPhoto.setImageBitmap(imageBitmap);


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();

            String imgBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

            Retrofit retrofit = new RetrofitFactory(this)
                    .build("https://api.imgur.com/", "Client-ID 8bcc638875f89d9");




            ImagenService imageService = retrofit.create(ImagenService.class);
            Imagen img = new Imagen();
            img.img= imgBase64;


            imageService.enviarFoto(img).enqueue(new Callback<ImagenResponse>() {
                @Override
                public void onResponse(Call<ImagenResponse> call, Response<ImagenResponse> response) {
                    ImagenResponse res = response.body();

                    etImagenURL.setText(res.data.link);
                   // link=res.data.link;

                    GuardarPelicula();


                }

                @Override
                public void onFailure(Call<ImagenResponse> call, Throwable t) {

                }
            });


        }

        if(requestCode == 1001) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(data.getData(), filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);
            ivPhoto.setImageBitmap(imageBitmap);


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();

            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.i("MAIN_APP", encoded);
        }
    }

    private void GuardarPelicula() {

        Retrofit retro2 = new RetrofitFactory(FormularioPeliculas.this)
                .build("https://api.imgur.com/", "Client-ID 8bcc638875f89d9");

        PeliculaService s = retro2.create(PeliculaService.class);

        s.create(new Pelicula()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {


            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });


    }

    private void CallUpdateAPI(Pelicula pelicula) {

        Retrofit retro = new Retrofit.Builder()
                .baseUrl("https://6359bef538725a1746b71cf0.mockapi.io/")// -> Aquí va la URL sin el Path
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PeliculaService service = retro.create(PeliculaService.class);
        service.update(pelicula, pelicula.id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(FormularioPeliculas.this, "Se actualizo correctamente", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(FormularioPeliculas.this, "No se pudo actualizar los datos...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                Log.e("MAIN_APP", t.toString());

            }
        });
    }

    private void callCreateAPI(Pelicula pelicula) {
        Retrofit retro = new Retrofit.Builder()
                .baseUrl("https://6359bef538725a1746b71cf0.mockapi.io/")// -> Aquí va la URL sin el Path
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PeliculaService service = retro.create(PeliculaService.class);
        service.create(pelicula).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(FormularioPeliculas.this, "Se guardo correctamente", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(FormularioPeliculas.this, "Error en el servidor...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                Log.e("MAIN_APP", t.toString());

            }
        });
    }
}



