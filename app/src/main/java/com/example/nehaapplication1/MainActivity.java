package com.example.nehaapplication1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button variables
        Button temp_button=findViewById(R.id.temp_button);
        Button heart_button=findViewById(R.id.heartbeat_button);
        Button respiratory_button=findViewById(R.id.respiratory_button);
        Button symptoms_button=findViewById(R.id.symptoms_button);
        Button upload_button=findViewById(R.id.upload_button);

        symptoms_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivitysymptoms();
            }
        });
    }

    public void openActivitysymptoms() {
        Intent intent = new Intent(this, symptom_activity.class);
        startActivity(intent);
    }
}