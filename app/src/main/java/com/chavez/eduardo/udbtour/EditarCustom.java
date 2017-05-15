package com.chavez.eduardo.udbtour;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EditarCustom extends AppCompatActivity implements Validator.ValidationListener {
    private Button updateRoutine;
    private Button deleteRoutine;
    private Button cancelUpdateRoutine;
    private Button confirmUpdateRoutine;

    @NotEmpty(message = "Ingresa un nombre")
    EditText routineNameUpdate;

    @NotEmpty(message = "Ingresa una descripcion")
    EditText routineDescriptionUpdate;

    private TextView titleMarker;
    private TextView markerNameUpdateTitle;
    private TextView markerDescriptionUpdateTitle;

    private String nombreSQ,descripcionSQ, imagenSQ, thumbnailSQ, categoriaSQ, nombre, descripcion;
    private double latitudSQ, longitudSQ;
    private int ID, idSQ;
    private ImageView imageView;

    private Place place;
    MapasModel mapasModel;

    Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editar_custom);
        mapasModel = new MapasModel(this);
        ID = getIntent().getIntExtra("ID_",0);

        routineNameUpdate = (EditText) findViewById(R.id.routineNameUpdate);
        routineDescriptionUpdate = (EditText) findViewById(R.id.routineDescriptionUpdate);

        titleMarker = (TextView) findViewById(R.id.viewMarkerTitle);
        markerNameUpdateTitle = (TextView) findViewById(R.id.routineNameUpdateTitle);
        markerDescriptionUpdateTitle = (TextView) findViewById(R.id.routineDescriptionUpdateTitle);
        imageView = (ImageView) findViewById(R.id.itemPicture);


        updateRoutine = (Button) findViewById(R.id.buttonUpdateRoutine);

        deleteRoutine = (Button)findViewById(R.id.buttonDeleteRoutine);
        cancelUpdateRoutine = (Button)findViewById(R.id.buttonCancelUpdate);
        confirmUpdateRoutine = (Button) findViewById(R.id.buttonConfirmUpdate);

        validator = new Validator(this);
        validator.setValidationListener(this);

        loadInitialView();

        Picasso.with(this).load(place.getImagen()).fit().placeholder(R.drawable.loading).error(R.drawable.alert).into(imageView);
        updateRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUpdateView();
            }
        });



        deleteRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDeleteDialog();
            }
        });

        cancelUpdateRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInitialView();
            }
        });

        confirmUpdateRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });



    }

    private void loadUpdateData() {
        nombre = routineNameUpdate.getText().toString();
        descripcion = routineDescriptionUpdate.getText().toString();
        mapasModel.actualizar(ID,nombre,descripcion,latitudSQ,longitudSQ,imagenSQ,thumbnailSQ,categoriaSQ);
        Toast.makeText(getApplicationContext(),"Elemento actualizado",Toast.LENGTH_SHORT).show();
        loadInitialView();
    }

    private void loadDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar elemento")
                .setMessage("¿Desea eliminar: " + place.getNombre() + "?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mapasModel.eliminar(ID);
                        Toast.makeText(getApplicationContext(),"Elemento eliminado",Toast.LENGTH_SHORT).show();
                        EditarCustom.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private void loadUpdateView() {
        routineNameUpdate.setVisibility(View.VISIBLE);
        routineDescriptionUpdate.setVisibility(View.VISIBLE);


        routineNameUpdate.setText(place.getNombre());
        routineDescriptionUpdate.setText(place.getDescripcion());


        titleMarker.setText(place.getNombre());
        markerNameUpdateTitle.setText("Ingrese un nuevo nombre");
        markerDescriptionUpdateTitle.setText("Ingrese una descripcion nueva");

        updateRoutine.setVisibility(View.GONE);
        deleteRoutine.setVisibility(View.GONE);
        cancelUpdateRoutine.setVisibility(View.VISIBLE);
        confirmUpdateRoutine.setVisibility(View.VISIBLE);
    }

    private void loadInitialView() {
        generateInitialData();

        routineNameUpdate.setVisibility(View.GONE);
        routineDescriptionUpdate.setVisibility(View.GONE);

        titleMarker.setText(place.getNombre());
        markerNameUpdateTitle.setText("Nombre: "+place.getNombre());
        markerDescriptionUpdateTitle.setText("Descripcion: "+place.getDescripcion());


        updateRoutine.setVisibility(View.VISIBLE);
        deleteRoutine.setVisibility(View.VISIBLE);
        cancelUpdateRoutine.setVisibility(View.GONE);
        confirmUpdateRoutine.setVisibility(View.GONE);
    }

    private void generateInitialData() {

        Cursor c = mapasModel.mostrarPorID(ID);
        c.moveToFirst();

        while (!c.isAfterLast()){
            idSQ = c.getInt(c.getColumnIndex("id"));
            nombreSQ = c.getString(c.getColumnIndex("nombre"));
            descripcionSQ = c.getString(c.getColumnIndex("descripcion"));
            latitudSQ = c.getDouble(c.getColumnIndex("latitud"));
            longitudSQ = c.getDouble(c.getColumnIndex("longitud"));
            imagenSQ = c.getString(c.getColumnIndex("imagen"));
            thumbnailSQ = c.getString(c.getColumnIndex("thumbnail"));
            categoriaSQ = c.getString(c.getColumnIndex("categoria"));

            place = (new Place(idSQ,nombreSQ,descripcionSQ,latitudSQ,longitudSQ,imagenSQ,thumbnailSQ,categoriaSQ));
            c.moveToNext();
        }

    }

    @Override
    public void onValidationSucceeded() {
        loadUpdateData();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error:errors){
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText){
                ((EditText)view).setError(message);
            } else {
                Toast.makeText(this,message,Toast.LENGTH_LONG).show();
            }
        }
    }
}
