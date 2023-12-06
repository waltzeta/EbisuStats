package com.ebisustats.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ebisustats.BBDD.DataStatusManager;
import com.ebisustats.R;

import com.ebisustats.BBDD.FirebaseHelper;

public class SingupActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister,btnBackLogin;
    private FirebaseHelper firebaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        etEmail = findViewById(R.id.emailRegister_txt);
        etPassword = findViewById(R.id.passwordRegister_txt);
        etConfirmPassword = findViewById(R.id.passwordConfirmRegister_txt);
        btnRegister = findViewById(R.id.singUp_button);
        btnBackLogin = findViewById(R.id.VolverLogin_Button);
        firebaseHelper = new FirebaseHelper();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        btnBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // redirigir a la actividad de inicio de sesión
                Intent intent = new Intent(SingupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (isValidRegistration(email, password, confirmPassword)) {
            firebaseHelper.registerNewUser(email, password, new DataStatusManager.WriteUserStatus() {
                @Override
                public void onUsersWriteSuccess() {
                    // Registro exitoso
                    Toast.makeText(SingupActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    // redirigir a la actividad de inicio de sesión
                    Intent intent = new Intent(SingupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();  // Cierra la actividad actual para que el usuario no pueda regresar con el botón "Atrás"
                }

                @Override
                public void onUsersWriteFailure(String errorMessage) {
                    // Ocurrió un error durante el registro
                    Toast.makeText(SingupActivity.this, "Error durante el registro: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean isValidRegistration(String email, String password, String confirmPassword) {
        // Realiza validaciones adicionales si es necesario
        // Aquí puedes verificar la fortaleza de la contraseña, formato de correo electrónico, etc.

        // Ejemplo simple: Verifica que los campos no estén vacíos y que la contraseña coincida con la confirmación
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}