package com.example.josueklisman.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.josueklisman.Models.Product;
import com.example.josueklisman.R;

import java.util.ArrayList;

public class ListViewProductAdapter extends BaseAdapter {

    Context context;
    ArrayList<Product> products;
    LayoutInflater layoutInflater;
    Product productModel;

    public ListViewProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
        layoutInflater = (LayoutInflater) context.getSystemService(
                context.LAYOUT_INFLATER_SERVICE
        );
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return products.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        if (rowView == null){
            rowView = layoutInflater.inflate(R.layout.list_products,
                    null,true);
        }
        TextView names = rowView.findViewById(R.id.names);
        TextView prices = rowView.findViewById(R.id.prices);

        productModel = products.get(i);

        names.setText(productModel.getName());
        prices.setText(productModel.getPrice().toString());

        return rowView;
    }
}