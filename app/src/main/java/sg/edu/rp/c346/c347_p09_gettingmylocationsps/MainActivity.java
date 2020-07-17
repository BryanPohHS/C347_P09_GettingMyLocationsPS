package sg.edu.rp.c346.c347_p09_gettingmylocationsps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

  private GoogleMap map;

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



    // Fragment map
    FragmentManager fm = getSupportFragmentManager();
    SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
    // Set map
    mapFragment.getMapAsync(new OnMapReadyCallback()
    {
      @Override
      public void onMapReady(GoogleMap googleMap)
      {
        map = googleMap;
        // Check if permissions granted
        if (checkPermission()){
          Task<Location> task = client.getLastLocation();
          task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>()
          {
            @Override
            public void onSuccess(Location location)
            {
              if(location != null){
                LAT_UPDATE = location.getLatitude();
                LONG_UPDATE = location.getLongitude();

                String msg = "Latitude: " + LAT_UPDATE + "\nLongitude: " + LONG_UPDATE;
                Log.d("CHECK", "Lat: " + LAT_UPDATE + "long: " + LONG_UPDATE);
                tvLat.setText(msg);
              } else {
                String msg = "No last known location found";
                tvLat.setText(msg);
              }
            }
          });
        }



        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
          map.setMyLocationEnabled(true);
        } else {
          Log.e("GMap - Permission", "GPS access has not been granted");
          ActivityCompat.requestPermissions(MainActivity.this,
                  new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        Log.d("CHECK2", "Lat: " + LAT_UPDATE + "long: " + LONG_UPDATE);
        LatLng poi_userLastLoc = new LatLng(LAT_UPDATE, LONG_UPDATE);
        Marker userLastKnown = map.addMarker(new
                MarkerOptions()
                .position(poi_userLastLoc)
                .title("Lat: " + LAT_UPDATE + " Long: " + LONG_UPDATE)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
      }
    });

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