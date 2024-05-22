package com.example.diabee;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ListView StuffGridView;
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
                    String value = item.getString(2);
                    String dataString = name + ": " + value;
                    dataList.add(dataString);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.category){
            Toast.makeText(this, "kategorie", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.oblubene){
            Toast.makeText(this, "oblubene", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.voda){
            Toast.makeText(this, "voda", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_box, null);

        final TextView TitleTextView = (TextView)customLayout.findViewById(R.id.Nazov);
        final TextView SjTextView = (TextView)customLayout.findViewById(R.id.sj_num);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] itemArray = item.split(" ");
        String name = null;
        if (itemArray[1].contains("(")){
            name = itemArray[0];
        }
        else if (itemArray[2].contains("(")){
            name = itemArray[0];
            name+=" "+itemArray[1];
        }
        else if (itemArray[3].contains("(")){
            name = itemArray[0];
            name+=" "+itemArray[1]+" "+itemArray[2];
        }
        else if (itemArray[4].contains("(")){
            name = itemArray[0];
            name+=" "+itemArray[1]+" "+itemArray[2]+" "+itemArray[3];
        }
        else if (itemArray[5].contains("(")){
            name = itemArray[0];
            name+=" "+itemArray[1]+" "+itemArray[2]+" "+itemArray[3]+" "+itemArray[4];
        }

        String value = itemArray[itemArray.length-1];
        SjTextView.setText(value);
        TitleTextView.setText(name);
        builder.setView(customLayout);

        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
