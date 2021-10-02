package com.example.nehaapplication1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import android.content.Intent;

public class symptom_activity extends AppCompatActivity {

    private RatingBar ratingBar;
    private Spinner spinner1;
    private TextView text1;
    private TextView text1_val;
    private float get_rating;
    private String selected;
    private Button buttonConfirm;
    private Hashtable<String,Float> symptom_values;
    private String[] symptomsList={"Fever", "Nausea", "Diarrhea", "Soar Throat","Muscle Ache","Loss of smell or taste", "Cough", "Shortness of Breath", "Feeling Tired"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom3);

        //Initialize variables from GUI
        ratingBar =(RatingBar) findViewById(R.id.ratingBar3);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        text1 = (TextView) findViewById(R.id.textView2);
        text1_val = (TextView) findViewById(R.id.textView5);
        buttonConfirm = (Button) findViewById(R.id.button);
        
        initialize();

        String val_HR = getIntent().getExtras().getString("HR_Value");
        symptom_values.put("Heart Rate",Float.parseFloat(val_HR));
        String val_RR = getIntent().getExtras().getString("RR_Value");
        symptom_values.put("Respiratory Rate",Float.parseFloat(val_RR));
        
        //symptom_hash = new Hashtable<String,Float>() {
        //    put("Heart", (float) 0);
        //};
        //Dropdown List
        //Creating the ArrayAdapter instance for dropdown list
        ArrayAdapter array1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,symptomsList);
        array1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(array1);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                get_rating = ratingBar.getRating();
                text1_val.setText(""+v);
                symptom_values.put((String) spinner1.getSelectedItem(),v);
                Log.d("NEHA :::", "---");
                Log.d(symptom_values.get("Fever").toString(), String.valueOf(v));
                Log.d((String) spinner1.getSelectedItem(), String.valueOf(v));
            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener (){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);
                selected = item.toString();
                text1.setText(selected);
                ratingBar.setRating(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                text1.setText("");
            }
        });


        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Float HR = symptom_values.get("Heart Rate");
                Float RR = symptom_values.get("Respiratory Rate");
                Float FEV = symptom_values.get("Fever");
                Float NAU = symptom_values.get("Nausea");
                Float HEA = symptom_values.get("Headache");
                Float DIA = symptom_values.get("Diarrhea");
                Float ST = symptom_values.get("Soar Throat");
                Float MA = symptom_values.get("Muscle Ache");
                Float LST = symptom_values.get("Loss of smell or taste");
                Float COU = symptom_values.get("Cough");
                Float SOB = symptom_values.get("Shortness Of Breath");
                Float FT = symptom_values.get("Feeling Tired");

                intent.putExtra("Heart Rate",HR);
                intent.putExtra("Respiratory Rate",RR);
                intent.putExtra("Fever",FEV);
                intent.putExtra("Nausea",NAU);
                intent.putExtra("Headache",HEA);
                intent.putExtra("Diarrhea",DIA);
                intent.putExtra("Soar Throat",ST);
                intent.putExtra("Muscle Ache",MA);
                intent.putExtra("Loss of smell or taste",LST);
                intent.putExtra("Cough",COU);
                intent.putExtra("Shortness Of Breath",SOB);
                intent.putExtra("Feeling Tired",FT);

                String filepath = "/sdcard/VadnereResults.csv";
                File csv = new File("/sdcard/VadnereResults.csv");
                if(!csv.exists()){
                    try {
                        csv.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    FileOutputStream fileout = new FileOutputStream(new File(filepath),true);
                    OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
                    outputWriter.write(HR.toString()+", ");
                    outputWriter.write(RR.toString()+", ");
                    outputWriter.write(FEV.toString()+", ");
                    outputWriter.write(NAU.toString()+", ");
                    outputWriter.write(HEA.toString()+", ");
                    outputWriter.write(DIA.toString()+", ");
                    outputWriter.write(ST.toString()+", ");
                    outputWriter.write(MA.toString()+", ");
                    outputWriter.write(LST.toString()+", ");
                    outputWriter.write(COU.toString()+", ");
                    outputWriter.write(SOB.toString()+", ");
                    outputWriter.write(FT.toString()+"\n");
                    outputWriter.close();
                }
                catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }

                intent.putExtra("symptom_table", symptom_values);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public void initialize() {
        symptom_values = new Hashtable<String,Float>()
        {{
            put("Heart Rate", (float) 0);
            put("Respiratory Rate", (float) 0);
            put("Fever", (float) 0);
            put("Nausea", (float) 0);
            put("Headache", (float) 0);
            put("Diarrhea", (float) 0);
            put("Soar Throat", (float) 0);
            put("Muscle Ache", (float) 0);
            put("Loss of smell or taste", (float) 0);
            put("Cough", (float) 0);
            put("Shortness Of Breath", (float) 0);
            put("Feeling Tired", (float) 0);
        }};
    }


}