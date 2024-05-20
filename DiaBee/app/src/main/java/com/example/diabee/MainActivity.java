package com.example.diabee;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout gridLayout = findViewById(R.id.gridLayout);

        try {
            JSONArray jsonArray = loadJSONFromAsset("kategorie.json");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String category = jsonArray.getString(i);
                    Log.d(TAG, "Category: " + category); // Kontrolný výpis
                    Button button = createButtonForCategory(category);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startDetailActivity(category);
                        }
                    });
                    gridLayout.addView(button);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Button createButtonForCategory(String category) {
        Button button = new Button(this);
        button.setLayoutParams(new GridLayout.LayoutParams());
        button.setText(category); // Nastaví text tlačidla na názov kategórie
        button.setContentDescription(category);
        return button;
    }

    private JSONArray loadJSONFromAsset(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open(fileName);
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

    private void startDetailActivity(String category) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
