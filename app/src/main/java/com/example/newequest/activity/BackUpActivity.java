package com.example.newequest.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newequest.R;
import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class BackUpActivity extends AppCompatActivity {
    // Request code for selecting a PDF document.
    private static final int PICK_PDF_FILE = 2;
    private static final String FILENAME = "603anacarolini.json";
//    private static final String FILENAME = "626angelica.json";
//    private static final String FILENAME = "export.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        try{
            getSupportActionBar().hide();
        }catch (Exception e){
            e.printStackTrace();
        }

        ImageView restore = findViewById(R.id.restore_backup);
        restore.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                openFile();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void openFile() {
        String jsonStr = "";

        try {
            File file = new File(getExternalFilesDir(null), FILENAME);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            jsonStr = builder.toString();
            JSONObject jsonObject = new JSONObject(jsonStr);
            Log.i("carol", jsonObject.keys().next());
            Log.i("carol", jsonObject.keys().next());

//            JSONArray questionnaireJsonArray = new JSONArray(jsonStr);
//            Log.i("carol", String.valueOf(questionnaireJsonArray.getJSONObject(0)));
        }catch (Exception e){
            e.printStackTrace();
        }






    }
}
