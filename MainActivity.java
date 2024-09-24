package com.example.farmerguidanceapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText inputSoilMoisture;
    Button btnSearch;
    TextView cropsRecommendation;
    TextView cropDetails;

    // SQLite Database
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputSoilMoisture = findViewById(R.id.input_soil_moisture);
        btnSearch = findViewById(R.id.btn_search);
        cropsRecommendation = findViewById(R.id.text_crops_recommendation);
        cropDetails = findViewById(R.id.text_crop_details);

        // Initialize Database
        db = openOrCreateDatabase("FarmDB", MODE_PRIVATE, null);
        createDatabase();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String moistureInput = inputSoilMoisture.getText().toString();
                if (!moistureInput.isEmpty()) {
                    double soilMoisture = Double.parseDouble(moistureInput);
                    recommendCrops(soilMoisture);
                }
            }
        });
    }

    // Decision tree logic to recommend crops based on soil moisture
    private void recommendCrops(double soilMoisture) {
        String recommendation = "";
        
        if (soilMoisture < 30) {
            recommendation = "Suitable Crops: Millet, Sorghum";
        } else if (soilMoisture >= 30 && soilMoisture <= 60) {
            recommendation = "Suitable Crops: Wheat, Barley";
        } else {
            recommendation = "Suitable Crops: Rice, Sugarcane";
        }

        cropsRecommendation.setText(recommendation);
        displayCropDetails();
    }

    // Display detailed information about crops and soil
    private void displayCropDetails() {
        Cursor cursor = db.rawQuery("SELECT * FROM Crops", null);
        StringBuilder cropInfo = new StringBuilder();

        if (cursor.moveToFirst()) {
            do {
                String cropName = cursor.getString(0);
                String soilType = cursor.getString(1);
                String climate = cursor.getString(2);
                cropInfo.append(cropName).append(" | Soil: ").append(soilType)
                        .append(" | Climate: ").append(climate).append("\n");
            } while (cursor.moveToNext());
        }

        cropDetails.setText(cropInfo.toString());
        cursor.close();
    }

    // Create Database Tables
    private void createDatabase() {
        db.execSQL("CREATE TABLE IF NOT EXISTS Crops (Name TEXT, SoilType TEXT, Climate TEXT);");
        db.execSQL("INSERT INTO Crops (Name, SoilType, Climate) VALUES ('Wheat', 'Loamy', 'Temperate');");
        db.execSQL("INSERT INTO Crops (Name, SoilType, Climate) VALUES ('Rice', 'Clay', 'Tropical');");
        db.execSQL("INSERT INTO Crops (Name, SoilType, Climate) VALUES ('Sugarcane', 'Alluvial', 'Tropical');");
    }
}
