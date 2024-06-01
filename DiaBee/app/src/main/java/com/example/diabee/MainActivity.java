package com.example.diabee;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final String TAG = "MainActivity";
    private static final int RC_NOTIFICATION = 99;
    private SearchView searchView;
    public DecimalFormat df = new DecimalFormat("#.##");
    private EditText unit;
    private EditText weight;
    private double result;
    private GridLayout gridLayout;
    private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        createNotificationChannel(); // Vytvara kanal pre notifikacie

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


         gridLayout = findViewById(R.id.gridLayout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                final View customLayout = getLayoutInflater().inflate(R.layout.activity_main, null);

                ArrayList<String> dataList = new ArrayList<>();
                ListView listView = findViewById(R.id.resultView);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray item = jsonArray.getJSONArray(i);
                    String name = item.getString(0);
                    if(name.toLowerCase().contains(textfind.toLowerCase()) && !textfind.isEmpty() && textfind.length()>=3){
                    String value = item.getString(2);
                    String dataString = name + ": " + value;
                    dataList.add(dataString);
                    listView.setVisibility(View.VISIBLE);


                    }

                    if(!dataList.isEmpty()){
                        gridLayout.setVisibility(View.GONE);
                    }
                    else{
                        gridLayout.setVisibility(View.VISIBLE);
                    }

                }
                System.out.println(dataList);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(this);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
// ---------------------------------- ALARM MANAGER -----------------------------------------------------
    // pri android verziach < 13 bude asi treba povolit notifikacie v nastaveniach
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.category){
            Toast.makeText(this, "kategorie", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.oblubene){
            Toast.makeText(this, "oblubene", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.voda) {
            if (item.isChecked()) {
                item.setChecked(false);
                cancelAlarm();
            } else {
                item.setChecked(true);
                startRepeatingAlarm();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, RC_NOTIFICATION);
                }

            }
            Toast.makeText(this, "voda", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    // overovanie
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RC_NOTIFICATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "ALLOWED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void startRepeatingAlarm() {
        Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long intervalMillis = 10; // 10 seconds POSIELA TO KAZDU MINUTU JE TO SRACKA NECHCE TO DAT MENEJ
        long triggerTime = System.currentTimeMillis() + intervalMillis;

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, intervalMillis, pendingIntent);
        }
    }

    private void cancelAlarm() {
        Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void createNotificationChannel() {
        CharSequence name = "VodaReminder";
        String description = "Oznamenie pre napitie sa vody";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("napiSaVody", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    // -----------------------------------------------------------------------------------------------------------

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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




                if (weight!=null){
                    try {
                        value =value.replace(",",".");

                        result = Double.parseDouble(value) * Double.parseDouble(weight.getText().toString()) / 100;
                        result=Double.parseDouble(df.format(result));
                        unit.setText(String.valueOf(result));
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "Hmotnosť musí byť číslo", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
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
}

