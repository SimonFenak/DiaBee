package com.example.diabee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    private Context context;
    private ArrayList<Product> productList;

    public ProductAdapter(@NonNull Context context, ArrayList<Product> productList) {
        super(context, 0, productList);
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Product product = getItem(position);

        TextView itemName = convertView.findViewById(R.id.item_name);
        TextView itemValue = convertView.findViewById(R.id.item_value);
        CheckBox itemFavorite = convertView.findViewById(R.id.item_favorite);

        itemName.setText(product.getName());
        itemValue.setText(product.getValue());
        itemFavorite.setChecked(product.isFavorite());

        itemFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.setFavorite(isChecked);
            updateFavoriteStatus(product);
        });

        return convertView;
    }

    private void updateFavoriteStatus(Product product) {
        // Implementujte logiku pre aktualizáciu stavu obľúbeného produktu v JSON súbore
    }
}
