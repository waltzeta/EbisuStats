package com.ebisustats.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ebisustats.R;

import com.ebisustats.NavigationDrawer;

public class SettingsActivity extends AppCompatActivity {
    //Creacion de variables
    Button btnSaveChanges;
    Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Asignacion de variables
        btnSaveChanges = (Button) findViewById(R.id.SaveChanges_button);
        btnVolver = (Button) findViewById(R.id.back_button);

        //Onclick Volver
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Volver al fragment Navigation Drawer
                Intent i = new Intent(SettingsActivity.this, NavigationDrawer.class);
                startActivity(i);
            }
        });

        //Onclick Boton Save Settings
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Aplicar los cambios

                //Volver al fragment Navigation Drawer
                Intent i = new Intent(SettingsActivity.this, NavigationDrawer.class);
                startActivity(i);
                Toast.makeText(SettingsActivity.this, "Cambios Aplicados" , Toast.LENGTH_SHORT).show();
            }
        });
    }
}