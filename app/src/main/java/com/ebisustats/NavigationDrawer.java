package com.ebisustats;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import com.ebisustats.ui.LoginActivity;
import com.ebisustats.ui.addsell.AddSellActivity;
import com.ebisustats.ui.sells.SellsFragment;
import com.ebisustats.ui.settings.SettingsActivity;
import com.ebisustats.ui.stock.AddProductActivity;
import com.ebisustats.ui.stock.EditProductActivity;
import com.ebisustats.ui.stock.StockFragment;

public class NavigationDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Creacion de Variables
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        //asignacion de variables
        fab = findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);

        //On Item selected del BottomNavigation
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.item_sell){
                    openFragment(new SellsFragment());
                    return true;
                } else if (itemId == R.id.item_stock) {
                    openFragment(new StockFragment());
                    return true;
                }
                return false;
            }
        });

        fragmentManager = getSupportFragmentManager();
        openFragment(new SellsFragment());

        //fab (boton flotante para añadir venta)
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            //llamado de funcion con la logica del bottomsheetlayout
            public void onClick(View view) {
                showBottomDialog();
            }
        });
    }

    //Funcion con la logica del BottomSheetLayout
    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottommshettlayout);

        //Layout (opciones del BottomShett)
        LinearLayout AddSellLayout = dialog.findViewById(R.id.layoutSell);
        LinearLayout AddProductLayout = dialog.findViewById(R.id.LayoutAddProduct);
        LinearLayout EditProductLayout = dialog.findViewById(R.id.LayoutEditProduct);

        //Img para cerrar el bottomShett
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        ImageView topBottomShettImg = dialog.findViewById(R.id.TopBottomShett_img);

        AddSellLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pasar a actividad de añadir Ventas
                Intent i = new Intent(NavigationDrawer.this, AddSellActivity.class);
                startActivity(i);
            }
        });
        AddProductLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasar a la actividad de Añadir producto
                Intent i = new Intent(NavigationDrawer.this, AddProductActivity.class);
                startActivity(i);
            }
        });
        EditProductLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pasar a la actividad de Editar producto
                Intent i = new Intent(NavigationDrawer.this, EditProductActivity.class);
                startActivity(i);
            }
        });

        //Funciones para cerrar el BottomShett
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        topBottomShettImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.nav_home){
            openFragment(new SellsFragment());
        } else if (itemId == R.id.nav_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        } else if (itemId == R.id.nav_logout) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    //funcion con la logica para pasar fragments en los menus
    private void openFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}




