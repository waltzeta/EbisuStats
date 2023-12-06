package com.ebisustats.ui.stock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ebisustats.R;

import java.util.ArrayList;
import java.util.List;

import com.ebisustats.BBDD.DataStatusManager;
import com.ebisustats.BBDD.FirebaseHelper;
import com.ebisustats.BBDD.Product;

public class StockFragment extends Fragment {

    List<ListElement> elements;
    private ListAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 1. Inicializar elementos de la lista
        init();

        // 2. Inflar el diseño del fragmento
        View rootView = inflater.inflate(R.layout.fragment_stock, container, false);

        // 3. Configurar el RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.listRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 4. Configurar el adaptador de la lista
        listAdapter = new ListAdapter(elements, requireContext()); // Usa la variable de instancia
        recyclerView.setAdapter(listAdapter);

        return rootView;
    }


    // Inicializar elementos en la lista
    public void init() {
        elements = new ArrayList<>();

        // 5. Obtener y mostrar los productos disponibles desde la base de datos
        readAvailableProducts();
    }

    // Obtener y mostrar los productos disponibles desde la base de datos
    public void readAvailableProducts() {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.readProducts(new DataStatusManager.ReadProductStatus() {
            @Override
            public void onProductsLoaded(List<Product> listaProductos) {
                // Iterar a través de los productos y agregar solo los disponibles a la lista de elementos
                for (Product producto : listaProductos) {
                    if (producto.isDisponibilidad()) {
                        String elementText = "Stock: " + producto.getStock();
                        elements.add(new ListElement(producto.getName(), elementText));
                    }
                }

                // Notificar al adaptador que los datos han cambiado
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onProductsLoadFailed(String errorMessage) {
                // Manejar el fallo al cargar los productos
                Toast.makeText(requireContext(), "Error al cargar productos: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}