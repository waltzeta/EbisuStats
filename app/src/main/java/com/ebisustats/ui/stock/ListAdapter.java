package com.ebisustats.ui.stock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ebisustats.R;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    //Atributos
    private List<ListElement> mData;
    private LayoutInflater mInflater;
    private Context context;

    //constructor
    public ListAdapter(List<ListElement> itemList, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
    }

    //Metodo que devuelve el size de la lista
    @Override
    public int getItemCount(){return mData.size();}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.list_products_cardview,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){holder.bindData(mData.get(position));}

    //metodo para redifinir los items de la lista
    public void setItems(List<ListElement> items){ mData = items;}

    public class ViewHolder extends RecyclerView.ViewHolder{
        //elementos en la cardview
        TextView nombre, stock;
        ViewHolder(View itemView){
            super(itemView);
            nombre = itemView.findViewById(R.id.textview_nombre_producto);
            stock = itemView.findViewById(R.id.textview_cantidad_producto);
        }

        //aqui los cambios de cada item
        void bindData(final ListElement item){
            nombre.setText(item.getName());
            stock.setText(item.getStock());

        }
    }
}
