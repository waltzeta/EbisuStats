package com.ebisustats.ui.addsell;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.List;

import com.ebisustats.BBDD.DataStatusManager;
import com.ebisustats.BBDD.FirebaseHelper;
import com.ebisustats.BBDD.Product;
import com.ebisustats.BBDD.Sale;
import com.ebisustats.NavigationDrawer;
import com.ebisustats.R;

public class AddSellActivity extends AppCompatActivity {

    // Creacion de variables
    Button btnCreateSell, AddtoSell, btnVolver;
    EditText et_numProducts;
    TextView tv_ProductosVenta, tv_CantProductosVenta, tv_PrecioFinal;

    // Auto Complete (Menu desplegable donde estarán los productos)
    AutoCompleteTextView autoCompleteTxt;

    // Lista de los productos obtenidos de la base de datos
    List<String> productList;

    // Lista para almacenar productos seleccionados y sus cantidades
    List<Sale.SaleItem> selectedProductsList;

    // Adaptador para el AutoCompleteTextView
    ArrayAdapter<String> adapterItems;

    // Instancia de FirebaseHelper
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sell);

        // Asignacion de variables
        btnCreateSell = findViewById(R.id.AddSell_button);
        btnVolver = findViewById(R.id.back_button);
        AddtoSell = findViewById(R.id.AddToSell_Button);
        et_numProducts = findViewById(R.id.numeroproductos_txt);
        tv_ProductosVenta = findViewById(R.id.tv_ProductosVenta);
        tv_CantProductosVenta = findViewById(R.id.tv_CantProductsVenta);
        tv_PrecioFinal = findViewById(R.id.preciofinal_txt);

        autoCompleteTxt = findViewById(R.id.auto_complete_products);

        // Inicializa la instancia de FirebaseHelper
        firebaseHelper = new FirebaseHelper();

        // Inicializa la lista de productos
        productList = new ArrayList<>();

        // Inicializa la lista de productos seleccionados
        selectedProductsList = new ArrayList<>();

        // Inicializa el adaptador para el AutoCompleteTextView
        adapterItems = new ArrayAdapter<>(this, R.layout.list_products, productList);
        autoCompleteTxt.setAdapter(adapterItems);

        // Carga los nombres de los productos desde la base de datos
        loadProductNames();

        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(AddSellActivity.this, "Producto Seleccionado: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        // Onclick Volver
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Volver al fragment Navigation Drawer
                Intent i = new Intent(AddSellActivity.this, NavigationDrawer.class);
                startActivity(i);
            }
        });

        // OnClick Boton Añadir a la Venta
        AddtoSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToSell();
            }
        });

        // OnClick Boton Crear Venta
        btnCreateSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSale();
            }
        });
    }

    private void loadProductNames() {
        firebaseHelper.readAvailableProducts(new DataStatusManager.ReadProductStatus() {
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
                Toast.makeText(AddSellActivity.this, "Error al cargar los productos: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToSell() {
        String productName = autoCompleteTxt.getText().toString().trim();
        String quantityStr = et_numProducts.getText().toString().trim();

        if (productName.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(AddSellActivity.this, "Ingrese un producto y una cantidad válida.", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);

        if (quantity <= 0) {
            Toast.makeText(AddSellActivity.this, "Ingrese una cantidad válida.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Utilizar el FirebaseHelper para obtener el producto por nombre
        firebaseHelper.getProductByName(productName, new DataStatusManager.GetProductByNameStatus() {
            @Override
            public void onProductGet(Product product) {
                if (product != null) {
                    // Verificar si el producto ya está en la lista y actualizar la cantidad
                    boolean productExists = false;

                    for (Sale.SaleItem saleItem : selectedProductsList) {
                        Product existingProduct = saleItem.getProduct();
                        if (existingProduct.getName().equals(product.getName())) {
                            int newQuantity = quantity + saleItem.getQuantity();

                            // Validar que la nueva cantidad no supere el stock
                            if (newQuantity <= existingProduct.getStock()) {
                                saleItem.setQuantity(newQuantity); // Actualizar la cantidad
                                productExists = true;
                            } else {
                                Toast.makeText(AddSellActivity.this, "Cantidad excede el stock disponible.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            break;
                        }
                    }

                    // Si el producto no está en la lista, agregarlo con la cantidad especificada
                    if (!productExists) {
                        Sale.SaleItem saleItem = new Sale.SaleItem(product, quantity);
                        selectedProductsList.add(saleItem);
                    }

                    autoCompleteTxt.setText("");
                    et_numProducts.setText("");

                    updateTextViews();
                } else {
                    Toast.makeText(AddSellActivity.this, "Producto no encontrado en la base de datos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProductGetFailed(String errorMessage) {
                Toast.makeText(AddSellActivity.this, "Error al obtener el producto: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTextViews() {
        // Actualizar los TextViews con la información de los productos seleccionados
        StringBuilder productosVentaText = new StringBuilder();
        StringBuilder cantProductosVentaText = new StringBuilder();
        int precioFinal = 0;

        for (Sale.SaleItem saleItem : selectedProductsList) {
            Product product = saleItem.getProduct();
            int quantity = saleItem.getQuantity();

            productosVentaText.append(product.getName()).append("\n");
            cantProductosVentaText.append(quantity).append("\n");

            // Calcular el precio total de venta para este producto y cantidad
            int totalSalePrice = product.getPrecioVenta() * quantity;
            precioFinal += totalSalePrice;
        }

        tv_ProductosVenta.setText(productosVentaText.toString());
        tv_CantProductosVenta.setText(cantProductosVentaText.toString());
        tv_PrecioFinal.setText(String.valueOf(precioFinal));
    }

    private void createSale() {
        // Crear una nueva instancia de Sale si hay productos seleccionados
        if (!selectedProductsList.isEmpty()) {
            Sale sale = new Sale();
            sale.setSaleItems(selectedProductsList);

            // Utilizar el FirebaseHelper para agregar la venta a la base de datos
            firebaseHelper.addSale(sale, new DataStatusManager.WriteSaleStatus() {
                @Override
                public void onSalesWriteSuccess() {
                    // Restar el stock de los productos vendidos
                    for (Sale.SaleItem saleItem : selectedProductsList) {
                        Product product = saleItem.getProduct();
                        int quantity = saleItem.getQuantity();

                        int newStock = product.getStock() - quantity;
                        product.setStock(newStock);

                        // Actualizar el stock en la base de datos
                        firebaseHelper.updateProductStock(product.getName(), newStock, new DataStatusManager.WriteProductStatus() {
                            @Override
                            public void onProductsWriteSuccess() {
                                // Éxito al actualizar el stock
                            }

                            @Override
                            public void onProductsWriteFailure(String errorMessage) {
                                Toast.makeText(AddSellActivity.this, "Error al actualizar el stock: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    Toast.makeText(AddSellActivity.this, "Venta añadida con éxito", Toast.LENGTH_SHORT).show();
                    // Volver al fragment Navigation Drawer
                    Intent i = new Intent(AddSellActivity.this, NavigationDrawer.class);
                    startActivity(i);
                }

                @Override
                public void onSalesWriteFailure(String errorMessage) {
                    Toast.makeText(AddSellActivity.this, "Error al agregar la venta: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(AddSellActivity.this, "No hay productos seleccionados para la venta.", Toast.LENGTH_SHORT).show();
        }
    }
}



