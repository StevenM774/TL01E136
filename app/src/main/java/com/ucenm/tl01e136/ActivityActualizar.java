package com.ucenm.tl01e136;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class ActivityActualizar extends AppCompatActivity {

    SQLiteConexion conexion;
    EditText txtId, txtNombre, txtTelefono, txtNota;
    Spinner cmbPais;
    ImageView imgContacto;
    Button btnActualizar, btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar);

        conexion = new SQLiteConexion(this, SQLiteConexion.NameDatabase, null, 1);

        txtId = findViewById(R.id.txtIdActualizar);
        txtNombre = findViewById(R.id.txtNombreActualizar);
        txtTelefono = findViewById(R.id.txtTelefonoActualizar);
        txtNota = findViewById(R.id.txtNotaActualizar);
        cmbPais = findViewById(R.id.cmbPaisActualizar);
        imgContacto = findViewById(R.id.imgContactoActualizar);
        btnActualizar = findViewById(R.id.btnActualizarDatos);
        btnCancelar = findViewById(R.id.btnCancelarActualizar);

        // configuracion de spinner
        String[] paises = {"Honduras (504)", "Costa Rica (506)", "Guatemala (502)", "El Salvador (503)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbPais.setAdapter(adapter);

        // Cargar datos de intent
        txtId.setText(getIntent().getStringExtra("id"));
        txtNombre.setText(getIntent().getStringExtra("nombre"));
        txtTelefono.setText(getIntent().getStringExtra("telefono"));
        txtNota.setText(getIntent().getStringExtra("nota"));

        String paisSeleccionado = getIntent().getStringExtra("pais");
        if (paisSeleccionado != null) {
            int position = adapter.getPosition(paisSeleccionado);
            cmbPais.setSelection(position);
        }
        
        // Cargar imagen
        byte[] imagenBytes = getIntent().getByteArrayExtra("imagen");
        if(imagenBytes != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
            imgContacto.setImageBitmap(bitmap);
        }

        btnCancelar.setOnClickListener(v -> finish());

        btnActualizar.setOnClickListener(v -> actualizar());
    }

    private void actualizar() {
        SQLiteDatabase db = conexion.getWritableDatabase();
        String idStr = txtId.getText().toString();

        ContentValues valores = new ContentValues();
        valores.put(SQLiteConexion.pais, cmbPais.getSelectedItem().toString());
        valores.put(SQLiteConexion.nombre, txtNombre.getText().toString());
        valores.put(SQLiteConexion.telefono, txtTelefono.getText().toString());
        valores.put(SQLiteConexion.nota, txtNota.getText().toString());
        valores.put(SQLiteConexion.imagen, imageViewToByte(imgContacto));

        int resultado = db.update(SQLiteConexion.tablaContactos, valores, "id = ?", new String[]{idStr});

        if (resultado > 0) {
            Toast.makeText(this, "Contacto actualizado con exito", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    private byte[] imageViewToByte(ImageView image) {
        android.graphics.drawable.Drawable drawable = image.getDrawable();
        if (drawable == null) return null;

        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : 200;
            int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : 200;
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
