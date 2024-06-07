package com.example.diabee;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class DetailActivity extends AppCompatActivity {

    private ListView stuffGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        stuffGridView = findViewById(R.id.listView);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String category = getIntent().getStringExtra("category");

        JSONArray jsonArray = loadJSONFromAsset(category + ".json");
        if (jsonArray != null) {
            try {
                ArrayList<Product> productList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray item = jsonArray.getJSONArray(i);
                    String name = item.getString(0);
                    String value = item.getString(2);
                    productList.add(new Product(name, value, false));
                }

                ProductAdapter adapter = new ProductAdapter(this, productList);
                stuffGridView.setAdapter(adapter);
                stuffGridView.setOnItemClickListener((parent, view, position, id) -> {
                    Product product = productList.get(position);
                    showProductDialog(product);
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONArray loadJSONFromAsset(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = getAssets().open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return new JSONArray(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showProductDialog(Product product) {
        // Implementácia dialógu pre zobrazenie a úpravu produktu
        // Pridajte kód pre zobrazenie dialógu
    }

    public void setSupportActionBar(Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }

}
