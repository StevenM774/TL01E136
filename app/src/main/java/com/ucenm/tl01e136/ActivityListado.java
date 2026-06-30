package com.ucenm.tl01e136;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ActivityListado extends AppCompatActivity {

    SQLiteConexion conexion;
    ListView lstContactos;
    EditText txtBuscar;
    Button btnAtras, btnCompartir, btnVerImagen, btnEliminar, btnActualizar;
    
    ArrayList<Contactos> lista;
    ArrayList<String> listaString;
    ArrayAdapter<String> adp;
    
    Contactos contactoSeleccionado;
    int indexSeleccionado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        conexion = new SQLiteConexion(this, SQLiteConexion.NameDatabase, null, 1);
        
        lstContactos = findViewById(R.id.lstContactos);
        txtBuscar = findViewById(R.id.txtBuscar);
        btnAtras = findViewById(R.id.btnAtras);
        btnCompartir = findViewById(R.id.btnCompartir);
        btnVerImagen = findViewById(R.id.btnVerImagen);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnActualizar = findViewById(R.id.btnActualizar);

        obtenerListaContactos();

        // Boton Atras
        btnAtras.setOnClickListener(v -> finish());

        // Busqueda
        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adp.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Seleccion de item
        lstContactos.setOnItemClickListener((parent, view, position, id) -> {
            indexSeleccionado = position;
            // Como el adapter puede estar filtrado, buscamos por el texto
            String itemText = (String) parent.getItemAtPosition(position);
            for(Contactos c : lista) {
                String codigo = extraerCodigo(c.getPais());
                String compare = c.getNombre() + " | (+" + codigo + ") " + c.getTelefono();
                if(compare.equals(itemText)) {
                    contactoSeleccionado = c;
                    break;
                }
            }
            
            mostrarDialogoLlamar();
        });

        // Boton Compartir
        btnCompartir.setOnClickListener(v -> {
            if (contactoSeleccionado == null) {
                Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
                return;
            }
            compartirContacto();
        });

        // Boton Ver Imagen
        btnVerImagen.setOnClickListener(v -> {
            if (contactoSeleccionado == null) {
                Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
                return;
            }
            verImagen();
        });

        // Boton Eliminar
        btnEliminar.setOnClickListener(v -> {
            if (contactoSeleccionado == null) {
                Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
                return;
            }
            eliminarContacto();
        });

        // Boton Actualizar
        btnActualizar.setOnClickListener(v -> {
            if (contactoSeleccionado == null) {
                Toast.makeText(this, "Seleccione un contacto", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Intent intent = new Intent(this, ActivityActualizar.class);
            intent.putExtra("id", String.valueOf(contactoSeleccionado.getId()));
            intent.putExtra("nombre", contactoSeleccionado.getNombre());
            intent.putExtra("telefono", String.valueOf(contactoSeleccionado.getTelefono()));
            intent.putExtra("pais", contactoSeleccionado.getPais());
            intent.putExtra("nota", contactoSeleccionado.getNota());
            intent.putExtra("imagen", contactoSeleccionado.getImagen());
            startActivity(intent);
        });
    }

    private void obtenerListaContactos() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        lista = new ArrayList<>();
        listaString = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + SQLiteConexion.tablaContactos, null);

        while (cursor.moveToNext()) {
            Contactos contacto = new Contactos();
            contacto.setId(cursor.getInt(0));
            contacto.setPais(cursor.getString(1));
            contacto.setNombre(cursor.getString(2));
            contacto.setTelefono(cursor.getInt(3));
            contacto.setNota(cursor.getString(4));
            contacto.setImagen(cursor.getBlob(5));

            lista.add(contacto);
            String codigo = extraerCodigo(contacto.getPais());
            listaString.add(contacto.getNombre() + " | (+" + codigo + ") " + contacto.getTelefono());
        }
        cursor.close();

        adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, listaString);
        lstContactos.setAdapter(adp);
        lstContactos.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private String extraerCodigo(String paisString) {
        if (paisString != null && paisString.contains("(") && paisString.contains(")")) {
            return paisString.substring(paisString.indexOf("(") + 1, paisString.indexOf(")"));
        }
        return "";
    }

    private void mostrarDialogoLlamar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acción");
        builder.setMessage("Desea llamar a " + contactoSeleccionado.getNombre() + "?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(this, ActivityLlamar.class);
            intent.putExtra("telefono", String.valueOf(contactoSeleccionado.getTelefono()));
            intent.putExtra("pais", contactoSeleccionado.getPais());
            startActivity(intent);
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void compartirContacto() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Contacto: " + contactoSeleccionado.getNombre() + "\nTel: " + contactoSeleccionado.getTelefono());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Compartir vía"));
    }

    private void verImagen() {
        if (contactoSeleccionado.getImagen() == null) {
            Toast.makeText(this, "No hay imagen", Toast.LENGTH_SHORT).show();
            return;
        }
        
        ImageView image = new ImageView(this);
        Bitmap bitmap = BitmapFactory.decodeByteArray(contactoSeleccionado.getImagen(), 0, contactoSeleccionado.getImagen().length);
        image.setImageBitmap(bitmap);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Imagen de " + contactoSeleccionado.getNombre());
        builder.setView(image);
        builder.setPositiveButton("Cerrar", null);
        builder.show();
    }

    private void eliminarContacto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar");
        builder.setMessage("¿Desea eliminar a " + contactoSeleccionado.getNombre() + "?");
        builder.setPositiveButton("Si", (dialog, which) -> {
            SQLiteDatabase db = conexion.getWritableDatabase();
            db.delete(SQLiteConexion.tablaContactos, "id = ?", new String[]{String.valueOf(contactoSeleccionado.getId())});
            db.close();
            Toast.makeText(this, "Eliminado con éxito", Toast.LENGTH_SHORT).show();
            contactoSeleccionado = null;
            obtenerListaContactos();
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
}
