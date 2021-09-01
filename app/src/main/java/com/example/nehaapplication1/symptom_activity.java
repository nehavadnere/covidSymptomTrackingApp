package com.example.nehaapplication1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Hashtable;

public class symptom_activity extends AppCompatActivity {

    private RatingBar ratingBar;
    private Spinner spinner1;
    private TextView text1;
    private TextView text1_val;
    private float get_rating;
    private String selected;
    private Hashtable<String,Float> symptom_hash;
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
            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener (){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);
                selected = item.toString();
                text1.setText(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                text1.setText("");
            }
        });
    }

}