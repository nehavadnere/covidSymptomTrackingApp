package com.example.nehaapplication1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Hashtable;
import java.util.List;

import  com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class MainActivity<sensorManager> extends AppCompatActivity implements SensorEventListener {

    private static final int MY_VIDEO_REQUEST_CODE = 100;
    private static final int SYMPTOM_REQUEST_CODE = 101;
    private  int DEFAULT_VIDEO_TIME = 45;
    private String APP_FILES_NAME = "CHA-HR";
    private float val_HR;
    private float RRdata;
    private int FRAMES = 21;
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Float HR, RR,FEV,NAU, DIA, HEA, ST, MA, LST, COU, SOB, FT;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float [] accValues= new float [3];
    private Hashtable<String,Float> symptom_values;

    //sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    //String context1 = Context.SENSOR_SERVICE;
    //sensorManager = (SensorManager)getSystemService(context1);
    //private float [] accValues= new float [3];
    //accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button variables
        //Button temp_button=findViewById(R.id.temp_button);
        Button heart_button=findViewById(R.id.heartbeat_button);
        Button respiratory_button=findViewById(R.id.respiratory_button);
        Button symptoms_button=findViewById(R.id.symptoms_button);
        Button upload_button=findViewById(R.id.upload_button);

        initalizaDB();

        heart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_cam = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
                intent_cam.putExtra(android.provider.MediaStore.EXTRA_DURATION_LIMIT,45);
                //File videoUri = saveFile(APP_FILES_NAME);
                //Uri videoUri = saveFile(APP_FILES_NAME);
                //Uri videoUri= Uri.fromFile(video_file);
                //intent_cam.putExtra(MediaStore.EXTRA_OUTPUT, videoUri.toString());
                //intent_cam.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                //Log.d("Location","- " + videoUri);
                startActivityForResult(intent_cam,MY_VIDEO_REQUEST_CODE);
                checkPermissions();
                val_HR = calculateHR();
                Toast.makeText(getApplicationContext(), "Processing saved video for heartbeat calculation ...", Toast.LENGTH_LONG).show();
                Log.d("HEART_RATE__", ""+ val_HR);
                Toast.makeText(getApplicationContext(), "Heart Rate is " +val_HR, Toast.LENGTH_LONG).show();

            }

        });

        respiratory_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener((SensorEventListener) MainActivity.this,accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                RRdata = calculateRR();
                Toast.makeText(getApplicationContext(), "Respiratory Rate is " +RRdata, Toast.LENGTH_LONG).show();
            }
        });

        symptoms_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivitysymptoms();
            }

        });

        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Saved measurements in File" + "/sdcard/VadnereResults.csv", Toast.LENGTH_LONG).show();
            }
        });
    }

    private Uri saveFile(String APP_FILES_NAME) {
        Uri VideoUri = null;
        File folder = new File(Environment.getExternalStorageDirectory() + "/Download");
        //File folder = new File(sdcard.getAbsolutePath() + "/CHA-NEHA");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File video_file = new File(folder+"/"+APP_FILES_NAME);
        Toast.makeText(getApplicationContext(), "video_file loc" + Uri.fromFile(video_file).toString(), Toast.LENGTH_LONG).show();
        //VideoUri =  Uri.fromFile(video_file);
        VideoUri = Uri.parse(Uri.fromFile(video_file).toString());
        return VideoUri;
        //return video_file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Video Successfully recorded" + data.getData(), Toast.LENGTH_LONG).show();
                //checkPermissions();
                //heart_rate_value=Extraction_Calculation();
                //Log.d("Heart Rate", ""+ heart_rate_value);
            } else {
                Toast.makeText(getApplicationContext(), "Video Failed", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == SYMPTOM_REQUEST_CODE && requestCode == RESULT_OK) {
            //symptom_values
            String symptomData = getIntent().getExtras().getString("symptom_table");
            HR = Float.parseFloat(getIntent().getExtras().getString("Heart Rate"));
            RR = Float.parseFloat(getIntent().getExtras().getString("Respiratory Rate"));
            FEV = Float.parseFloat(getIntent().getExtras().getString("Fever"));
            NAU = Float.parseFloat(getIntent().getExtras().getString("Nausea"));
            HEA = Float.parseFloat(getIntent().getExtras().getString("Headache"));
            DIA = Float.parseFloat(getIntent().getExtras().getString("Diarrhea"));
            ST = Float.parseFloat(getIntent().getExtras().getString("Soar Throat"));
            MA = Float.parseFloat(getIntent().getExtras().getString("Muscle Ache"));
            LST = Float.parseFloat(getIntent().getExtras().getString("Loss of smell or taste"));
            COU = Float.parseFloat(getIntent().getExtras().getString("Cough"));
            SOB = Float.parseFloat(getIntent().getExtras().getString("Shortness Of Breath"));
            FT = Float.parseFloat(getIntent().getExtras().getString("Feeling Tired"));

            Log.d("HR", String.valueOf(HR));
            Log.d("FEV", String.valueOf(FEV));

            //symptom_values = (Hashtable<String, Float>) getIntent().getSerializableExtra("symptom_table");
            //Toast.makeText(getApplicationContext(), "***** database values: " + symptom_values, Toast.LENGTH_LONG).show();
        }
    }



    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    public float calculateHR() {
        //Location of sample video file
        String videoUri = "/sdcard/Download/FingertipVideo.mp4";
        //to store the pixel values
        ArrayList<Float> bitmap = new ArrayList<>();
        //retrieve video file
        MediaMetadataRetriever mediaR = new MediaMetadataRetriever();
        mediaR.setDataSource(videoUri);
        //according to sample code provided, initialize the values
        int frame_rate = 30000000;
        ArrayList<Float[]> result = new ArrayList<>();
        int window = 5;
        int bpsec=10;
        int frame_count1 = 0;
        float sumP = (float) 0.0;
        int pixel, R;
        int redP,i, j;
        ArrayList<Float> fr_arr;
        Float[] fr_arr_float;

        while (frame_count1 < FRAMES) {
            sumP = 0;
            Bitmap bF = mediaR.getFrameAtTime(frame_rate);
            if (bF == null)
                break;
            frame_rate += 1000000;
            //Get all the red pixels from input frames
            for (i=0; i< bF.getWidth();i++) {
                for (j=0; j< bF.getHeight(); j++) {
                    pixel = bF.getPixel(i,j);
                    redP = Color.red(pixel);
                    sumP += redP;
                }
            }

            //red pixel sum per area
            bitmap.add(sumP / (bF.getWidth() * bF.getHeight()));
            sumP = 0;
            frame_count1++;
            Log.d("COmputing frames",".......");
        }
        //find moving average of pixels data
        fr_arr = mov_avg(bitmap);
        fr_arr_float = new Float[fr_arr.size()];
        fr_arr_float =fr_arr.toArray(fr_arr_float);

        int len1 = fr_arr_float.length;
        Float[] new_arr;

        // based on how much space window we want to put, add the pixel values into result
        for (int frame = 0; frame <= len1 - window; frame += window) {
            new_arr = Arrays.copyOfRange(fr_arr_float, frame, frame + window);
            result.add(new_arr);
        }

        ArrayList<Integer> peakArr = new ArrayList<>();

        for (i = 0; i < result.size(); i++) {
            int zc1 = findPeak(result.get(i));
            peakArr.add(zc1);
        }

        for (i = 0; i < peakArr.size(); i++) {
            sumP += peakArr.get(i);
        }
        sumP /= 2;
        float heart_Rate = (sumP / peakArr.size()) * 12;
        heart_Rate *= bpsec;
        return heart_Rate;
    }

    //Find the summation of differences in the given pixel data
    public static int findPeak(Float[] div) {
        ArrayList<Float> diff = new ArrayList<>();
        for (int i = 0; i < div.length - 1; i++) {
            diff.add((div[i] - div[i + 1]));
        }
        return nextStep(diff);
    }

    public static int nextStep(ArrayList<Float> diff) {
        ArrayList<Integer> zc = new ArrayList<>();
        for (int i = 0; i < diff.size() - 1; i++) {
            zc.add(find(diff.get(i), diff.get(i + 1)));
        }
        int sum = 0;
        for (int i = 0; i < zc.size(); i++) {
            sum += zc.get(i);
        }
        return sum;
    }

    public static int find(float i, float j) {
        if (i * j < 0)
            return 1;
        else
            return 0;
    }

    public ArrayList<Float> mov_avg(ArrayList<Float> bitmapArray) {
        int size = 5;
        ArrayList<Float> movingAvgArray = new ArrayList<>();

        for (int i = 0; i + size <= bitmapArray.size(); i++) {
            float sum = 0;
            for (int j = i; j < i + size; j++) {
                float temp = bitmapArray.get(j);
                sum += temp;
            }

            float average = sum / size;
            movingAvgArray.add(average);
        }
        return movingAvgArray;
    }

    // RR
    public float calculateRR() {

        ArrayList<Float> dataCSV = null;
        int acc =0;
        ArrayList<Float> x_axis = new ArrayList<>();
        //ArrayList<Float> y_axis = new ArrayList<>();
        int x_max =1280;
        dataCSV = readFile();

        for (int i=0; i<x_max;i++)
        {
            x_axis.add(dataCSV.get(i));
        }
        ArrayList<Float> mov_avg = mov_avg(dataCSV);
        Float[] mtempArray = new Float[mov_avg.size()];
        mtempArray = mov_avg.toArray(mtempArray);

        acc = findPeak(mtempArray);
        acc = (acc/2) *60;
        float result = acc / dataCSV.size();
        Log.d("RESPIRATORY_RATE__", String.valueOf(result));
        return result;
    }

    public ArrayList<Float> readCSV() throws IOException, CsvValidationException {
        File csv_file = new File("/sdcard/CSVBreathe19.csv");
        ArrayList<Float> dataCSV = new ArrayList<>();
        CSVReader csvreader = null;
        try {
            csvreader = new CSVReader(new FileReader(csv_file.getAbsolutePath()));

        }catch (FileNotFoundException e){e.printStackTrace();}
        String[] line;
        ArrayList<Float> value = new ArrayList<>();
        assert csvreader != null;
        while ((line = csvreader.readNext()) != null){
            value.add(Float.parseFloat(line[0]));
        }
        return value;
    }

    public ArrayList<Float> readFile() {
        File csv_file = new File("/sdcard/CSVBreathe19.csv");
        Context context = getApplicationContext();
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        ArrayList<Float> value = new ArrayList<>();
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(csv_file));
            while ((line = in.readLine()) != null) value.add(Float.parseFloat(line)); ;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return value;
    }


    public void openActivitysymptoms() {
        Intent intent = new Intent(this, symptom_activity.class);
        intent.putExtra("HR_Value", Float.toString(val_HR));
        intent.putExtra("RR_Value", Float.toString(RRdata));
        //startActivity(intent);
        startActivityForResult(intent,SYMPTOM_REQUEST_CODE);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        accValues[0]=sensorEvent.values[0];
        accValues[1]=sensorEvent.values[1];
        accValues[2]=sensorEvent.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void initalizaDB() {
        String filepath = "/sdcard/VadnereResults.csv";
        File csv = new File("/sdcard/vadnereResults.csv");

        try {
            FileOutputStream fileout = new FileOutputStream(new File(filepath),true);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write("HeartRate, ");
            outputWriter.write("RespiratoryRate, ");
            outputWriter.write("Fever, ");
            outputWriter.write("Nausea, ");
            outputWriter.write("Headache, ");
            outputWriter.write("Diarrhea, ");
            outputWriter.write("SoarThroat, ");
            outputWriter.write("MuscleAche, ");
            outputWriter.write("LossSmellTaste, ");
            outputWriter.write("Cough, ");
            outputWriter.write("ShortnessOfBreath, ");
            outputWriter.write("FeelingTired\n");
            outputWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}