package com.ucenm.tl01e136;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ActivityLlamar extends AppCompatActivity {

    TextView txtNumeroLlamar;
    Button btnAtrasLlamar;
    ImageButton btnIconoLlamar;
    String numero, pais;
    static final int CALL_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llamar);

        txtNumeroLlamar = findViewById(R.id.txtNumeroLlamar);
        btnAtrasLlamar = findViewById(R.id.btnAtrasLlamar);
        btnIconoLlamar = findViewById(R.id.btnIconoLlamar);

        // Recibir el número y país desde el Intent
        numero = getIntent().getStringExtra("telefono");
        pais = getIntent().getStringExtra("pais");

        if (numero != null && pais != null) {
            String codigo = extraerCodigo(pais);
            String numeroCompleto = "+" + codigo + numero;
            txtNumeroLlamar.setText(numeroCompleto);
            numero = numeroCompleto; // Actualizar el número con el código para la llamada
        } else if (numero != null) {
            txtNumeroLlamar.setText(numero);
        }

        btnAtrasLlamar.setOnClickListener(v -> finish());

        btnIconoLlamar.setOnClickListener(v -> {
            confirmarLlamada();
        });
    }

    private String extraerCodigo(String paisString) {
        if (paisString != null && paisString.contains("(") && paisString.contains(")")) {
            return paisString.substring(paisString.indexOf("(") + 1, paisString.indexOf(")"));
        }
        return "";
    }

    private void confirmarLlamada() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Llamada");
        builder.setMessage("¿Desea realizar la llamada a " + numero + "?");
        builder.setPositiveButton("Si", (dialog, which) -> {
            realizarLlamada();
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void realizarLlamada() {
        // Verificar permiso
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
        } else {
            // Intent ACTION_CALL
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + numero));
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                realizarLlamada();
            } else {
                Toast.makeText(this, "Permiso de llamada denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
