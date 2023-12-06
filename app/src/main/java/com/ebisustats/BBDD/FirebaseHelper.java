package com.ebisustats.BBDD;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {

    private DatabaseReference mDatabase;

    public FirebaseHelper() {
        // Referencia de la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    //FUNCIONES PARA EL MANEJO DE USUARIOS EN FIREBASE
    public void readUsers(final DataStatusManager.ReadUserStatus readDataStatus) {
        // Realiza una consulta a la base de datos
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> listaUsuarios = new ArrayList<>();
                // Itera a través de los hijos de "usuarios"
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtiene el email y la contraseña de cada usuario
                    String email = snapshot.child("email").getValue(String.class);
                    String password = snapshot.child("password").getValue(String.class);

                    // Crea un objeto Usuario y agrega a la lista
                    User usuario = new User(email, password);
                    listaUsuarios.add(usuario);
                }

                // Llama al método de interfaz para informar que los datos se han cargado
                readDataStatus.onUsersLoaded(listaUsuarios);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                readDataStatus.onUsersLoadFailed(databaseError.getMessage());
            }
        });
    }

    public void registerNewUser(String email, String password, final DataStatusManager.WriteUserStatus writeUserStatus) {
        // Crea un nuevo usuario
        User newUser = new User(email, password);

        // Referencia a la ubicación donde deseas almacenar el usuario en la base de datos
        DatabaseReference usersRef = mDatabase.child("Users");

        // Comprobar q el usuario no este duplicado
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // El correo electrónico ya está en uso
                    writeUserStatus.onUsersWriteFailure("El correo electrónico ya está registrado");
                } else {
                    // El correo electrónico no está en uso, proceder con el registro
                    String userId = usersRef.push().getKey();

                    usersRef.child(userId).setValue(newUser)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    writeUserStatus.onUsersWriteSuccess();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    writeUserStatus.onUsersWriteFailure("Error durante el registro de usuarios: " + e.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                writeUserStatus.onUsersWriteFailure(databaseError.getMessage());
            }
        });
    }

    //FUNCIONES PARA EL MANEJO DE PRODUCTOS EN FB

    //leer productos disponibles
    public void readAvailableProducts(final DataStatusManager.ReadProductStatus readProductStatus) {
        Query query = mDatabase.child("products").orderByChild("disponibilidad").equalTo(true);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Product> listaProductos = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    int stock = snapshot.child("stock").getValue(Integer.class);
                    int precioCosto = snapshot.child("precioCosto").getValue(Integer.class);
                    int precioVenta = snapshot.child("precioVenta").getValue(Integer.class);
                    boolean disponibilidad = snapshot.child("disponibilidad").getValue(Boolean.class);

                    Product producto = new Product(name, precioCosto, precioVenta, disponibilidad, stock);
                    listaProductos.add(producto);
                }

                readProductStatus.onProductsLoaded(listaProductos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                readProductStatus.onProductsLoadFailed(databaseError.getMessage());
            }
        });
    }
    //Leer TODOS los productos
    public void readProducts(final DataStatusManager.ReadProductStatus readProductStatus) {
        mDatabase.child("products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Product> listaProductos = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    int stock = snapshot.child("stock").getValue(Integer.class);
                    int precioCosto = snapshot.child("precioCosto").getValue(Integer.class);
                    int precioVenta = snapshot.child("precioVenta").getValue(Integer.class);
                    boolean disponibilidad = snapshot.child("disponibilidad").getValue(Boolean.class);

                    Product producto = new Product(name, precioCosto, precioVenta, disponibilidad, stock);
                    listaProductos.add(producto);
                }

                readProductStatus.onProductsLoaded(listaProductos);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                readProductStatus.onProductsLoadFailed(databaseError.getMessage());
            }
        });
    }
    // Método para obtener un producto por nombre
    public void getProductByName(String productName, final DataStatusManager.GetProductByNameStatus getProductByNameStatus) {
        Query query = mDatabase.child("products").orderByChild("name").equalTo(productName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Encontró el producto
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Obtener los datos del producto
                        String name = snapshot.child("name").getValue(String.class);
                        int stock = snapshot.child("stock").getValue(Integer.class);
                        int precioCosto = snapshot.child("precioCosto").getValue(Integer.class);
                        int precioVenta = snapshot.child("precioVenta").getValue(Integer.class);
                        boolean disponibilidad = snapshot.child("disponibilidad").getValue(Boolean.class);

                        Product product = new Product(name, precioCosto, precioVenta, disponibilidad, stock);
                        getProductByNameStatus.onProductGet(product);
                        return;
                    }
                } else {
                    // Producto no encontrado
                    getProductByNameStatus.onProductGet(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar error de lectura
                getProductByNameStatus.onProductGetFailed(databaseError.getMessage());
            }
        });
    }

    //función para agregar un nuevo producto
    public void addProduct(@NonNull Product product, final DataStatusManager.WriteProductStatus writeProductStatus) {
        // Verifica si el nombre del producto ya existe
        Query query = mDatabase.child("products").orderByChild("name").equalTo(product.getName());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Si ya existe un producto con el mismo nombre, informa el fallo
                    writeProductStatus.onProductsWriteFailure("Ya existe un producto con el nombre: " + product.getName());
                } else {
                    // Si no existe, agrega el nuevo producto
                    DatabaseReference productsRef = mDatabase.child("products").child(product.getName());

                    productsRef.setValue(product)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    writeProductStatus.onProductsWriteSuccess();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    writeProductStatus.onProductsWriteFailure("Error al agregar producto: " + e.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                writeProductStatus.onProductsWriteFailure(databaseError.getMessage());
            }
        });
    }

    //función para actualizar un producto
    public void updateProductByName(String productName, Product updatedProduct, final DataStatusManager.WriteProductStatus writeProductStatus) {
        DatabaseReference productsRef = mDatabase.child("products");

        // Verifica si el producto existe antes de intentar actualizarlo
        Query query = productsRef.orderByChild("name").equalTo(productName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean productExists = false;

                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    // Obtiene la referencia al nodo del producto específico
                    DatabaseReference productRef = productsRef.child(productSnapshot.getKey());

                    // Actualiza las propiedades del producto
                    productRef.child("stock").setValue(updatedProduct.getStock());
                    productRef.child("precioCosto").setValue(updatedProduct.getPrecioCosto());
                    productRef.child("precioVenta").setValue(updatedProduct.getPrecioVenta());
                    productRef.child("disponibilidad").setValue(updatedProduct.isDisponibilidad());

                    productExists = true;
                    break;  // Importante: Sale del bucle después de encontrar y actualizar el producto
                }

                // Si el producto no existe, informa el fallo
                if (!productExists) {
                    writeProductStatus.onProductsWriteFailure("No se encontró un producto con el nombre: " + productName);
                } else {
                    writeProductStatus.onProductsWriteSuccess();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                writeProductStatus.onProductsWriteFailure(databaseError.getMessage());
            }
        });
    }
    public void updateProductStock(String productName, int newStock, final DataStatusManager.WriteProductStatus writeProductStatus) {
        DatabaseReference productsRef = mDatabase.child("products");

        // Verifica si el producto existe antes de intentar actualizar su stock
        Query query = productsRef.orderByChild("name").equalTo(productName);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean productExists = false;

                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    // Obtiene la referencia al nodo del producto específico
                    DatabaseReference productRef = productsRef.child(productSnapshot.getKey());

                    // Actualiza el stock del producto
                    productRef.child("stock").setValue(newStock);

                    productExists = true;
                    break;  // Importante: Sale del bucle después de encontrar y actualizar el producto
                }

                // Si el producto no existe, informa el fallo
                if (!productExists) {
                    writeProductStatus.onProductsWriteFailure("No se encontró un producto con el nombre: " + productName);
                } else {
                    writeProductStatus.onProductsWriteSuccess();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                writeProductStatus.onProductsWriteFailure(databaseError.getMessage());
            }
        });
    }

    //FUNCIONES PARA EL MANEJO DE VENTAS EN FB
    //Leer Toas las ventas
    public void readSales(final DataStatusManager.ReadSaleStatus readSaleStatus) {
        mDatabase.child("sales").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Sale> sales = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Sale sale = snapshot.getValue(Sale.class);
                    sales.add(sale);
                }

                if (sales.isEmpty()) {
                    // No hay ventas cargadas
                    readSaleStatus.onSalesLoadFailed("No hay ventas disponibles.");
                } else {
                    // La lista de ventas se ha cargado exitosamente
                    readSaleStatus.onSalesLoaded(sales);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                readSaleStatus.onSalesLoadFailed(databaseError.getMessage());
            }
        });
    }
    //Añadir venta
    public void addSale(Sale sale, final DataStatusManager.WriteSaleStatus writeSaleStatus) {
        DatabaseReference salesRef = mDatabase.child("sales").push();

        salesRef.setValue(sale)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        writeSaleStatus.onSalesWriteSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        writeSaleStatus.onSalesWriteFailure("Error al agregar la venta: " + e.getMessage());
                    }
                });
    }
}