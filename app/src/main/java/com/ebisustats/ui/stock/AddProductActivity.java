package com.ebisustats.ui.stock;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ebisustats.R;

import com.ebisustats.BBDD.DataStatusManager;
import com.ebisustats.BBDD.FirebaseHelper;
import com.ebisustats.BBDD.Product;
import com.ebisustats.NavigationDrawer;

public class AddProductActivity extends AppCompatActivity {

    Button BackButton,AddProductBtn;
    EditText et_NombreProducto, et_PrecioCosto, et_PrecioVenta, et_Stock;

    // Agrega una instancia de FirebaseHelper
    private FirebaseHelper firebaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        BackButton = (Button) findViewById(R.id.back_button);
        AddProductBtn = (Button) findViewById(R.id.AddProduct_button);

        et_NombreProducto = (EditText) findViewById(R.id.et_NombreProducto);
        et_PrecioCosto = (EditText) findViewById(R.id.et_precioCosto);
        et_PrecioVenta = (EditText) findViewById(R.id.et_PrecioVenta);
        et_Stock= (EditText) findViewById(R.id.et_CantidadProductos);


        //instancia de FirebaseHelper
        firebaseHelper = new FirebaseHelper();

        AddProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Verificar que los datos no esten Vacios
                if (TextUtils.isEmpty(et_PrecioCosto.getText()) || TextUtils.isEmpty(et_PrecioVenta.getText()) || TextUtils.isEmpty(et_Stock.getText()) || TextUtils.isEmpty(et_NombreProducto.getText())){
                    // Mostrar un mensaje de error si algún campo está vacío
                    AlertDialog.Builder alert = new AlertDialog.Builder(AddProductActivity.this);
                    alert.setMessage("Debe de Completar Todos los Campos");
                    alert.setCancelable(false);
                    alert.setNegativeButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
                        et_NombreProducto.setText("");
                        et_PrecioCosto.setText("");
                        et_PrecioVenta.setText("");
                        et_Stock.setText("");
                        dialog.cancel();
                    });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                    return;
                }
                //Transformar los datos obtenidos a String y Enteros
                String Nombre = et_NombreProducto.getText().toString();
                int precioCosto = Integer.parseInt(et_PrecioCosto.getText().toString());
                int precioVenta = Integer.parseInt(et_PrecioVenta.getText().toString());
                int stock = Integer.parseInt(et_Stock.getText().toString());

                //crea objeto Producto
                Product product = new Product(Nombre, precioCosto,precioVenta,stock);

                // Agrega el nuevo producto a la base de datos
                firebaseHelper.addProduct(product, new DataStatusManager.WriteProductStatus() {
                    @Override
                    public void onProductsWriteSuccess() {
                        // Éxito al agregar el producto
                        Toast.makeText(AddProductActivity.this, "Producto Añadido con Éxito", Toast.LENGTH_SHORT).show();

                        // Volver al fragment Navigation Drawer
                        Intent i = new Intent(AddProductActivity.this, NavigationDrawer.class);
                        startActivity(i);
                    }

                    @Override
                    public void onProductsWriteFailure(String errorMessage) {
                        // Manejar el fallo al agregar el producto
                        Toast.makeText(AddProductActivity.this, "Error al añadir producto: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Volver al fragment Navigation Drawer
                Intent i = new Intent(AddProductActivity.this, NavigationDrawer.class);
                startActivity(i);
            }
        });
    }
}