package com.example.diabee;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView StuffGridView;
    public DecimalFormat df = new DecimalFormat("#.##");

    private EditText weight;
    private double result;

    private EditText unit;
    private String value;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        StuffGridView = (ListView) findViewById(R.id.listView);
        StuffGridView.setOnItemClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String category = getIntent().getStringExtra("category");

        JSONArray jsonArray = loadJSONFromAsset(category + ".json");
        if (jsonArray != null) {
            try {
                ArrayList<String> dataList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray item = jsonArray.getJSONArray(i);
                    String name = item.getString(0);
                    value = item.getString(2);
                    String dataString = name + ": " + value;
                    dataList.add(dataString);
                }

                ProductAdapter adapter = new ProductAdapter(this, dataList);

                ListView listView = findViewById(R.id.listView);
                listView.setAdapter(adapter);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCategories(newText);
                return true;
            }
        });

        return true;
    }

    private void filterCategories(String textfind) {
        JSONArray jsonArray = loadJSONFromAsset("vsetko.json");
        if (jsonArray != null) {
            try {
                ArrayList<String> dataList = new ArrayList<>();
                ListView listResView = findViewById(R.id.resultView);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray item = jsonArray.getJSONArray(i);
                    String name = item.getString(0);
                    if (name.toLowerCase().contains(textfind.toLowerCase()) && !textfind.isEmpty() && textfind.length() >= 3) {
                        String value = item.getString(2);
                        String dataString = name + ": " + value;
                        dataList.add(dataString);
                        listResView.setVisibility(View.VISIBLE);
                    }

                    if (!dataList.isEmpty()) {
                        StuffGridView.setVisibility(View.GONE);
                    } else {
                        StuffGridView.setVisibility(View.VISIBLE);
                    }
                }
                System.out.println(dataList);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);

                listResView.setAdapter(adapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public void kliknutie_na_item(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_box, null);

        final TextView TitleTextView = customLayout.findViewById(R.id.Nazov);
        final TextView SjTextView = customLayout.findViewById(R.id.sj_num);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] itemArray = item.split(" ");
        String name = null;
        if (itemArray[1].contains("(")) {
            name = itemArray[0];
        } else if (itemArray[2].contains("(")) {
            name = itemArray[0] + " " + itemArray[1];
        } else if (itemArray[3].contains("(")) {
            name = itemArray[0] + " " + itemArray[1] + " " + itemArray[2];
        } else if (itemArray[4].contains("(")) {
            name = itemArray[0] + " " + itemArray[1] + " " + itemArray[2] + " " + itemArray[3];
        } else if (itemArray[5].contains("(")) {
            name = itemArray[0] + " " + itemArray[1] + " " + itemArray[2] + " " + itemArray[3] + " " + itemArray[4];
        } else {
            name = itemArray[0] + " " + itemArray[1] + " " + itemArray[2] + " " + itemArray[3] + " " + itemArray[4] + " " + itemArray[5];
        }

        value = itemArray[itemArray.length - 1];
        SjTextView.setText(value);
        TitleTextView.setText(name);
        builder.setView(customLayout);

        final AlertDialog alertDialog = builder.create();

        MaterialButton closeButton = customLayout.findViewById(R.id.close_button);
        MaterialButton calc_Button = customLayout.findViewById(R.id.calc_Button);
        MaterialButton delete_Button = customLayout.findViewById(R.id.delete_Button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss(); // Zavrieť dialog
            }
        });
        calc_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight = customLayout.findViewById(R.id.editHmotnost);
                unit = customLayout.findViewById(R.id.editSachJed);

                if (weight != null) {
                    try {
                        value = value.replace(",", ".");
                        value = value.replace(",", ".");

                        // Parse value and weight, then calculate the result
                        double valueParsed = Double.parseDouble(value);
                        double weightParsed = Double.parseDouble(weight.getText().toString());
                        double result = (valueParsed * weightParsed) / 100;

                        // Format the result to #.##
                        DecimalFormat df = new DecimalFormat("#.##");

                        result = Double.parseDouble(value) * Double.parseDouble(weight.getText().toString()) / 100;
                        result = Double.parseDouble(df.format(result));
                        // Set the result to the unit TextView
                        unit.setText(String.valueOf(result));
                    } catch (NumberFormatException e) {
                        String errorMessage = e.getMessage();
                        System.out.println(errorMessage);
                        Toast.makeText(getApplicationContext(), "Hmotnosť musí byť číslo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Musíte zadať hmotnosť", Toast.LENGTH_SHORT).show();
                }
            }
        });
        delete_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight = customLayout.findViewById(R.id.editHmotnost);
                unit = customLayout.findViewById(R.id.editSachJed);
                weight.setText("");
                unit.setText("");
            }
        });

        alertDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
