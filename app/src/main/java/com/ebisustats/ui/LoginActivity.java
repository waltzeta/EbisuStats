package com.ebisustats.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ebisustats.R;

import java.util.List;

import com.ebisustats.BBDD.DataStatusManager;
import com.ebisustats.BBDD.FirebaseHelper;
import com.ebisustats.BBDD.User;
import com.ebisustats.MQTT.MqttHandler;
import com.ebisustats.NavigationDrawer;

public class LoginActivity extends AppCompatActivity {

    private Button btnInicioSesion, btnCreateAccount;
    private EditText txtEmail, txtPassword;
    private FirebaseHelper firebaseHelper;

    // MQTT ======================================================================
    private static final String BROKER_URL = "tcp://androidteststiqq.cloud.shiftr.io:1883";
    private static final String CLIENT_ID = "EbisuStats Walter";
    private MqttHandler mqttHandler;
    // MQTT ======================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnInicioSesion = findViewById(R.id.iniciar_sesion_button);
        btnCreateAccount = findViewById(R.id.createAccount_Button);
        txtEmail = findViewById(R.id.email_txt);
        txtPassword = findViewById(R.id.password_txt);

        // MQTT ====================================================================
        try{
            mqttHandler = new MqttHandler();
            mqttHandler.connect(BROKER_URL,CLIENT_ID);

            subscribeToTopic("Tema1");
            publishMessage("Tema1", "wola");
        }catch (Exception E){
            String mensaje = E.getMessage().toString();
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        }
        // MQTT ====================================================================
        firebaseHelper = new FirebaseHelper();

        btnInicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyUser();
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SingupActivity.class);
                startActivity(i);
            }
        });


    }
    // MQTT ======================================================================
    private void publishMessage(String topic, String message){
        Toast.makeText(this, "Publishing message: " + message, Toast.LENGTH_SHORT).show();
        mqttHandler.publish(topic,message);
    }
    private void subscribeToTopic(String topic){
        Toast.makeText(this, "Subscribing to topic "+ topic, Toast.LENGTH_SHORT).show();
        mqttHandler.subscribe(topic);
    }
    // MQTT ====================================================================

    private void verifyUser() {
        String userEmail = txtEmail.getText().toString();
        String userPassword = txtPassword.getText().toString();

        firebaseHelper.readUsers(new DataStatusManager.ReadUserStatus() {
            @Override
            public void onUsersLoaded(List<User> listaUsuarios) {
                for (User usuario : listaUsuarios) {
                    Log.d("LoginActivity", "Usuario: " + usuario.getEmail() + " - " + usuario.getPassword());
                    Log.d("LoginActivity", "Comparando: " + usuario.getEmail() + " con " + userEmail);
                    Log.d("LoginActivity", "Comparando: " + usuario.getPassword() + " con " + userPassword);

                    if (usuario.getEmail() != null && usuario.getPassword() != null &&
                            usuario.getEmail().equals(userEmail) && usuario.getPassword().equals(userPassword)) {
                        Intent i = new Intent(LoginActivity.this, NavigationDrawer.class);
                        startActivity(i);
                        return;  // Importante: Sale del método después de iniciar la actividad
                    }
                }

                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                alert.setMessage("Usuario o Contraseña Incorrecto");
                alert.setCancelable(false);
                alert.setNegativeButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
                    txtEmail.setText("");
                    txtPassword.setText("");
                    dialog.cancel();
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }

            @Override
            public void onUsersLoadFailed(String errorMessage) {
                Toast.makeText(LoginActivity.this, "Error al cargar la lista de Usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
