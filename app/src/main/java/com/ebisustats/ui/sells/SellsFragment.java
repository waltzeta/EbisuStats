package com.ebisustats.ui.sells;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ebisustats.R;

import java.util.ArrayList;
import java.util.List;

import com.ebisustats.BBDD.DataStatusManager;
import com.ebisustats.BBDD.FirebaseHelper;
import com.ebisustats.BBDD.Sale;

public class SellsFragment extends Fragment {

    private RadioButton rb_VentasDiarias, rb_VentasSemanales, rb_VentasMensuales;
    private TextView tv_NumeroVentas, tv_Promedio, tv_Ganancias;

    FirebaseHelper firebaseHelper;
    private List<Sale> salesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sells, container, false);

        // Vincular elementos mediante sus ID
        rb_VentasDiarias = view.findViewById(R.id.ventas_diarias_rb);
        rb_VentasSemanales = view.findViewById(R.id.ventas_semanales_rb);
        rb_VentasMensuales = view.findViewById(R.id.ventas_mensuales_rb);
        tv_NumeroVentas = view.findViewById(R.id.numeroventas_txt);
        tv_Promedio = view.findViewById(R.id.promedioventas_txt);
        tv_Ganancias = view.findViewById(R.id.ganancias_txt);

        firebaseHelper = new FirebaseHelper();

        // Obtener la lista de ventas desde Firebase
        readSalesFromDatabase();

        // Configurar listener para el cambio en la selección de frecuencia
        RadioGroup radioGroup = view.findViewById(R.id.radioGroupSellsFragment);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> updateSalesData());

        // Actualizar datos iniciales
        updateSalesData();

        return view;
    }
    // Método para obtener la lista de ventas desde Firebase
    private void readSalesFromDatabase() {
        firebaseHelper.readSales(new DataStatusManager.ReadSaleStatus() {
            @Override
            public void onSalesLoaded(List<Sale> sales) {
                // La lista de ventas se ha cargado exitosamente desde la base de datos
                salesList = sales;

                // Actualizar datos después de cargar la lista de ventas
                updateSalesData();
            }

            @Override
            public void onSalesLoadFailed(String errorMessage) {
                // Manejar el fallo en la carga de ventas desde la base de datos si es necesario
                Log.e("SellsFragment", "Error al cargar ventas desde la base de datos: " + errorMessage);
            }
        });
    }

    // Método para actualizar los datos de ventas en la interfaz de usuario
    private void updateSalesData() {
        if (salesList == null || salesList.isEmpty()) {
            // Manejar el caso en el que no hay ventas cargadas
            tv_NumeroVentas.setText("0");
            tv_Promedio.setText("0");
            tv_Ganancias.setText("0");
            return;
        }

        // Lógica para calcular y mostrar el número de ventas
        int numeroVentas = salesList.size();
        tv_NumeroVentas.setText(String.valueOf(numeroVentas));

        // Lógica para calcular y mostrar el promedio de ventas y las ganancias
        double totalVentas = 0;
        int totalGanancias = 0;

        // Obtener la frecuencia seleccionada
        int frecuencia = getFrecuenciaSeleccionada();

        // Calcular promedio de ventas y ganancias según la frecuencia
        for (Sale sale : salesList) {
            totalVentas += 1;
            totalGanancias += (int) (sale.getTotalSalePrice() - sale.getTotalCost());
        }

        // Calcular promedio
        int promedioVentas = (frecuencia > 0) ? (int) totalVentas / frecuencia : 0;
        tv_Promedio.setText(String.valueOf(promedioVentas));

        // Mostrar ganancias totales
        int promedioGanancias =  totalGanancias * frecuencia;
        tv_Ganancias.setText(String.valueOf(promedioGanancias));
    }

    private int getFrecuenciaSeleccionada() {
        // Obtener el RadioButton seleccionado
        if (rb_VentasDiarias.isChecked()) {
            return 1;  // Diario
        } else if (rb_VentasSemanales.isChecked()) {
            return 7;  // Semanal (dividido por 7 días)
        } else if (rb_VentasMensuales.isChecked()) {
            return 30;  // Mensual (dividido por 30 días)
        }
        return 1;  // Por defecto, diario
    }
}

