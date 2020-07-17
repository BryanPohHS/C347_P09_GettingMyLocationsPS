package sg.edu.rp.c346.c347_p09_gettingmylocationsps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity
{

  TextView tvLastKnown, tvLat;
  Button btnStart, btnStop, btnCheck;
  FusedLocationProviderClient client;
  double LAT_UPDATE, LONG_UPDATE;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    tvLastKnown = findViewById(R.id.tvLastKnownLoc);
    tvLat = findViewById(R.id.tvLat);
    btnStart = findViewById(R.id.btnStart);
    btnStop = findViewById(R.id.btnStop);
    btnCheck = findViewById(R.id.btnCheck);

    // Location is triggered once upon display activity
    client = LocationServices.getFusedLocationProviderClient(this);

    // Check if permissions granted
    if (checkPermission()){
      Task<Location> task = client.getLastLocation();
      task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>()
      {
        @Override
        public void onSuccess(Location location)
        {
          if(location != null){
            String msg = "Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLatitude();
            tvLat.setText(msg);
          } else {
            String msg = "No last known location found";
            tvLat.setText(msg);
          }
        }
      });
    }

    // Detector will run even when app close
    btnStart.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        Intent i = new Intent(MainActivity.this, MyService.class);
        startService(i);
      }
    });

    btnStop.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        Intent i = new Intent(MainActivity.this, MyService.class);
        stopService(i);
      }
    });


    // Check buttons will read the file and toast
    btnCheck.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        // File reading
        String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GettingMyLocation";
        File targetFile = new File(folderLocation, "problemstatement.txt");

        if(targetFile.exists() == true){
          String data = "";
          try {
            FileReader reader = new FileReader(targetFile);
            BufferedReader br = new BufferedReader(reader);

            String line = br.readLine();
            while(line != null){
              data += line + "\n";
              line = br.readLine();
            }

            Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
            br.close();
            reader.close();
          } catch (Exception e){
            Toast.makeText(MainActivity.this, "Failed to read", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
          }

        }
      }
    });
  }

  private boolean checkPermission(){
    int permissionCheck_Coarse = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
    int permissionCheck_Fine = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

    if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
      return true;
    } else {
      return false;
    }
  }
}