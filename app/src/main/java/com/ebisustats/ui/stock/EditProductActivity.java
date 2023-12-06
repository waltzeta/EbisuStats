package com.ebisustats.ui.stock;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ebisustats.R;

import java.util.ArrayList;
import java.util.List;

import com.ebisustats.BBDD.DataStatusManager;
import com.ebisustats.BBDD.FirebaseHelper;
import com.ebisustats.BBDD.Product;
import com.ebisustats.NavigationDrawer;

public class EditProductActivity extends AppCompatActivity {
    Button BackButton, UpdateProductBtn;

    //Atributos del Producto
    EditText et_PrecioCosto, et_PrecioVenta, et_Stock;
    RadioButton rbNo;

    // Auto Complete (Menu desplegable donde estarán los productos)
    AutoCompleteTextView autoCompleteTxt;

    // Lista de los productos obtenidos de la base de datos
    List<String> productList;

    // Adaptador para el AutoCompleteTextView
    ArrayAdapter<String> adapterItems;

    // Instancia de FirebaseHelper
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        BackButton = findViewById(R.id.back_button);
        UpdateProductBtn = findViewById(R.id.UpdateProduct_button);
        et_PrecioCosto = (EditText) findViewById(R.id.et_precioCosto);
        et_PrecioVenta = (EditText) findViewById(R.id.et_PrecioVenta);
        et_Stock= (EditText) findViewById(R.id.et_CantidadProductos);
        rbNo = findViewById(R.id.rbDisponibleNo);

        autoCompleteTxt = findViewById(R.id.auto_complete_products);

        // Inicializa la instancia de FirebaseHelper
        firebaseHelper = new FirebaseHelper();

        // Inicializa la lista de productos
        productList = new ArrayList<>();

        // Inicializa el adaptador para el AutoCompleteTextView
        adapterItems = new ArrayAdapter<>(this, R.layout.list_products, productList);
        autoCompleteTxt.setAdapter(adapterItems);

        //Inicializar un Producto
        Product productUpdated = new Product();

        // Carga los nombres de los productos desde la base de datos
        loadProductNames();

        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                productUpdated.setName(item);//Actualizar nombre del objeto
                Toast.makeText(EditProductActivity.this, "Producto Seleccionado: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        //OnClick UpdateProduct Boton
        UpdateProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Verificar que los datos no esten Vacios
                if (TextUtils.isEmpty(et_PrecioCosto.getText()) || TextUtils.isEmpty(et_PrecioVenta.getText()) || TextUtils.isEmpty(et_Stock.getText()) || productUpdated.getName().isEmpty()){
                    // Mostrar un mensaje de error si algún campo está vacío
                    AlertDialog.Builder alert = new AlertDialog.Builder(EditProductActivity.this);
                    alert.setMessage("Debe de Completar Todos los Campos");
                    alert.setCancelable(false);
                    alert.setNegativeButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
                        et_PrecioCosto.setText("");
                        et_PrecioVenta.setText("");
                        et_Stock.setText("");
                        dialog.cancel();
                    });
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                    return;
                }
                //Transformar los datos obtenidos a Enteros y Boolean
                int precioCosto = Integer.parseInt(et_PrecioCosto.getText().toString());
                int precioVenta = Integer.parseInt(et_PrecioVenta.getText().toString());
                int stock = Integer.parseInt(et_Stock.getText().toString());
                Boolean disponible = true;
                if(rbNo.isChecked()){
                    disponible = false;
                }
                //Actualizar atributos de el Objeto
                productUpdated.setPrecioCosto(precioCosto);
                productUpdated.setPrecioVenta(precioVenta);
                productUpdated.setStock(stock);
                productUpdated.setDisponibilidad(disponible);

                //Actualizar Producto en la Base de Datos.
                firebaseHelper.updateProductByName(productUpdated.getName(), productUpdated, new DataStatusManager.WriteProductStatus() {
                    @Override
                    public void onProductsWriteSuccess() {
                        // Éxito al editar el producto
                        Toast.makeText(EditProductActivity.this, "Producto Actualizado con Éxito", Toast.LENGTH_SHORT).show();

                        // Volver al fragment Navigation Drawer
                        Intent i = new Intent(EditProductActivity.this, NavigationDrawer.class);
                        startActivity(i);
                    }

                    @Override
                    public void onProductsWriteFailure(String errorMessage) {
                        // Manejar el fallo al agregar el producto
                        Toast.makeText(EditProductActivity.this, "Error al añadir producto: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // OnClick Boton Volver
        BackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditProductActivity.this, NavigationDrawer.class);
                startActivity(i);
            }
        });
    }

    // Función para cargar los nombres de productos desde la base de datos
    private void loadProductNames() {
        firebaseHelper.readProducts(new DataStatusManager.ReadProductStatus() {
            @Override
            public void onProductsLoaded(List<Product> listaProductos) {
                // Limpia la lista actual
                productList.clear();

                // Agrega los nombres de los productos a la lista
                for (Product product : listaProductos) {
                    productList.add(product.getName());
                }

                // Notifica al adaptador que los datos han cambiado
                adapterItems.notifyDataSetChanged();
            }

            @Override
            public void onProductsLoadFailed(String errorMessage) {
                // Manejar el fallo al cargar los productos
                Toast.makeText(EditProductActivity.this, "Error al cargar los productos: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
