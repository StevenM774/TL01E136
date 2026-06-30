package com.ucenm.tl01e136;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    SQLiteConexion conexion;
    EditText txtNombre, txtTelefono, txtNota;
    Spinner cmbPais;
    ImageView imgContacto;
    Button btnSalvar, btnLista, btnTomarFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtNombre = findViewById(R.id.txtNombre);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtNota = findViewById(R.id.txtNota);
        cmbPais = findViewById(R.id.cmbPais);
        imgContacto = findViewById(R.id.imgContacto);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnLista = findViewById(R.id.btnLista);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);

        // Configurar Spinner
        String[] paises = {"Honduras (504)", "Costa Rica (506)", "Guatemala (502)", "El Salvador (503)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbPais.setAdapter(adapter);


        //Evento  tomar foto
        ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            imgContacto.setImageBitmap(imageBitmap);
                        } else {
                            Toast.makeText(this, "No se pudo obtener la foto", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        btnTomarFoto.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(intent);
        });

        // Boton Salvar
        btnSalvar.setOnClickListener(v -> salvarContacto());

        // Boton Lista
        btnLista.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityListado.class);
            startActivity(intent);
        });
    }

    private void salvarContacto() {
        String nombre = txtNombre.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String nota = txtNota.getText().toString().trim();
        String pais = cmbPais.getSelectedItem().toString();

        // Validacion y alertas
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Debe escribir un nombre", Toast.LENGTH_LONG).show();
            return;
        }

        if (telefono.isEmpty()) {
            Toast.makeText(this, "Debe escribir un telefono", Toast.LENGTH_LONG).show();
            return;
        }

        if (nota.isEmpty()) {
            Toast.makeText(this, "Debe escribir una nota", Toast.LENGTH_LONG).show();
            return;
        }

        // Validaciones con Regex
        if (!Pattern.matches("^[a-zA-Z\\s]+$", nombre)) {
            Toast.makeText(this, "El nombre solo debe contener letras", Toast.LENGTH_LONG).show();
            return;
        }

        if (!Pattern.matches("^[0-9]+$", telefono)) {
            Toast.makeText(this, "El telefono solo debe contener numeros", Toast.LENGTH_LONG).show();
            return;
        }

        // Guardado
        try {
            conexion = new SQLiteConexion(this, SQLiteConexion.NameDatabase, null, 1);
            SQLiteDatabase db = conexion.getWritableDatabase();

            ContentValues valores = new ContentValues();
            valores.put(SQLiteConexion.pais, pais);
            valores.put(SQLiteConexion.nombre, nombre);
            valores.put(SQLiteConexion.telefono, telefono);
            valores.put(SQLiteConexion.nota, nota);
            valores.put(SQLiteConexion.imagen, imageViewToByte(imgContacto));

            Toast.makeText(this, "Contacto Guardado con exito", Toast.LENGTH_LONG).show();
            limpiarPantalla();
            db.close();

        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void limpiarPantalla() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtNota.setText("");
        imgContacto.setImageResource(android.R.drawable.ic_menu_gallery);
    }
}
