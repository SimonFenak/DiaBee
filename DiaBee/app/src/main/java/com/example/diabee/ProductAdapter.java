package com.example.diabee;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<String> {
    private boolean rob = false;
    private Context context;
    private ArrayList<String> dataList;
    String dataString = "";
    private EditText weight;
    private double result;
    String name;
    private EditText unit;
    private String value;

    public ProductAdapter(@NonNull Context context, ArrayList<String> dataList) {
        super(context, 0, dataList);
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        String data = getItem(position);

        TextView itemName = convertView.findViewById(R.id.item_name);
        TextView itemValue = convertView.findViewById(R.id.item_value);
        CheckBox itemFavorite = convertView.findViewById(R.id.item_favorite);

        // Assuming the format is "name: value"
        String[] parts = data.split(": ");
        name = parts[0];
        value = parts[1];


        itemName.setText(name);
        itemValue.setText(value);

        // Set OnClickListener for the entire item
        convertView.setOnClickListener(view -> {
            String item = dataList.get(position);
            final View customLayout = LayoutInflater.from(context).inflate(R.layout.dialog_box, null);

            final TextView TitleTextView = customLayout.findViewById(R.id.Nazov);
            final TextView SjTextView = customLayout.findViewById(R.id.sj_num);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            String[] itemArray = item.split(" ");
            name = null;
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
            CheckBox favoritka = customLayout.findViewById(R.id.favoritne);
            MaterialButton closeButton = customLayout.findViewById(R.id.close_button);
            MaterialButton calc_Button = customLayout.findViewById(R.id.calc_Button);
            MaterialButton delete_Button = customLayout.findViewById(R.id.delete_Button);
            if(overenie(name,favoritka)){
                favoritka.setChecked(true);
            }
            closeButton.setOnClickListener(v -> alertDialog.dismiss());
            calc_Button.setOnClickListener(v -> {
                weight = customLayout.findViewById(R.id.editHmotnost);
                unit = customLayout.findViewById(R.id.editSachJed);


                if (weight.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Musíte zadať hmotnosť", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        value = value.replace(",", ".");

                        double valueParsed = Double.parseDouble(value);
                        double weightParsed = Double.parseDouble(weight.getText().toString());
                        double result = valueParsed * weightParsed / 100;

                        // Format the result to #.##
                        DecimalFormat df = new DecimalFormat("#.##");

                        result = Double.parseDouble(value) * Double.parseDouble(weight.getText().toString()) / 100;
                        // Set the result to the unit TextView
                        unit.setText(String.valueOf(result));
                    } catch (NumberFormatException e) {
                        String errorMessage = e.getMessage();
                        System.out.println(errorMessage);
                        Toast.makeText(context, "Hmotnosť musí byť číslo", Toast.LENGTH_SHORT).show();
                    }
                }

            });
            delete_Button.setOnClickListener(v -> {
                weight = customLayout.findViewById(R.id.editHmotnost);
                unit = customLayout.findViewById(R.id.editSachJed);
                weight.setText("");
                unit.setText("");
            });
            favoritka.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    try {
                        String favorites = loadFavoritesFromJson();
                        String itemicek = dataList.get(position);

                        System.out.println(itemicek);

                        String[] itemiki = itemicek.split(":");

                        favorites += "[" + itemiki[0] + ",\n" + itemiki[1] + "]";
                        saveFavoritesToJson(favorites);
                        System.out.println(favorites);
                        Toast.makeText(context, "Item added to favorites", Toast.LENGTH_SHORT).show();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Failed to add item to favorites", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    try {
                        String favorites = loadFavoritesFromJson();
                        //System.out.println(favorites);
                        String itemicek = dataList.get(position);
                        itemFavorite.setChecked(false);
                        String[] itemiki = itemicek.split(":");
                        String skuska="[" + itemiki[0] + "," + itemiki[1] + "]";
                        System.out.println();
                        if(favorites.contains("[" + itemiki[0] + "," + itemiki[1] + "]")){
                            favorites=favorites.replace("[" + itemiki[0] + "," + itemiki[1] + "]","");}
                        System.out.println(favorites);
                        saveFavoritesToJson(favorites);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            });
            alertDialog.show();
        });
/*
        // Set OnCheckedChangeListener for the CheckBox
        itemFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (rob){
            if (isChecked) {
                try {
                    String favorites = loadFavoritesFromJson();
                    String item = dataList.get(position);

                    System.out.println(item);

                    String[] itemiki = item.split(":");

                    favorites += "[" + itemiki[0] + ",\n" + itemiki[1] + "]";
                    saveFavoritesToJson(favorites);
                    System.out.println(favorites);
                    Toast.makeText(context, "Item added to favorites", Toast.LENGTH_SHORT).show();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Failed to add item to favorites", Toast.LENGTH_SHORT).show();
                }
            } else {
                try {
                    String favorites = loadFavoritesFromJson();
                    //System.out.println(favorites);
                    String item = dataList.get(position);
                    itemFavorite.setChecked(false);
                    String[] itemiki = item.split(":");
                    if(favorites.contains("[" + itemiki[0] + "," + itemiki[1] + "]")){
                    favorites=favorites.replace("[" + itemiki[0] + "," + itemiki[1] + "]","");}
                    System.out.println(favorites);
                    saveFavoritesToJson(favorites);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }}
        });
        rob=true;
        */
        return convertView;}


        private String loadFavoritesFromJson () throws IOException, JSONException {
            String jsonString = "";
            try {
                // Otvorenie súboru 'data.json' na čítanie
                FileInputStream fis = context.openFileInput("data.json");
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);

                // Načítanie dát zo súboru
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                // Zatvorenie FileInputStream
                fis.close();

                // Reťazec s načítanými dátami
                jsonString = stringBuilder.toString();
                dataString = jsonString;
                //System.out.println(jsonString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonString;
        }

        private void saveFavoritesToJson (String favorites) throws IOException {
            try {
                // Vytvorenie (alebo otvorenie existujúceho) súboru 'data.json' v internom úložisku
                FileOutputStream fos = context.openFileOutput("data.json", MODE_PRIVATE);

                // Zapisovanie dát do súboru
                fos.write(favorites.getBytes());
                //System.out.println(favorites.getBytes());

                // Zatvorenie FileOutputStream
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private boolean overenie (String name, CheckBox itemFavorite){
            try {
                String ujo = loadFavoritesFromJson();
                System.out.println("UJO:");
                System.out.println(ujo);
                //System.out.println("Name"+name+", "+value);
                if (ujo.contains(name)) {
                    System.out.println(name + ", " + value + "      JE V ZOZNAME");
                    return true;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
    private void clearFavoritesFile() throws IOException {
        try {
            // Otvorenie (alebo vytvorenie nového) súboru 'data.json' v internom úložisku
            FileOutputStream fos = context.openFileOutput("data.json", MODE_PRIVATE);

            // Zatvorenie FileOutputStream bez zapisovania dát vyprázdni súbor
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }
