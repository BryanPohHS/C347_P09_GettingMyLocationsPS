package sg.edu.rp.c346.c347_p09_gettingmylocationsps;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileWriter;

public class MyService extends Service
{
  boolean started;
  FusedLocationProviderClient client;
  LocationRequest mLocationRequest;
  LocationCallback mLocationCallback;

  // Global variable for lat, long
  double LAT_UPDATE, LONG_UPDATE;

  // Global variable for folder location
  String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/GettingMyLocation";

  public MyService()
  {
  }

  @Override
  public IBinder onBind(Intent intent)
  {
    // TODO: Return the communication channel to the service.
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public void onCreate()
  {
    // Create folder
    File folder = new File(folderLocation);
    if(folder.exists() == false) {
      boolean result = folder.mkdir();

      if(result == true) {
        Log.d("File read/write", "Folder created");
        Log.d("Service", "Service created");
      }
    }

    super.onCreate();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId)
  {
    client = LocationServices.getFusedLocationProviderClient(this);

    // Configuring for constant location detection
    mLocationRequest = new LocationRequest();
    mLocationRequest = LocationRequest.create();
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    mLocationRequest.setInterval(10000);
    mLocationRequest.setFastestInterval(50000);
    mLocationRequest.setSmallestDisplacement(100);

    mLocationCallback = new LocationCallback(){
      @Override
      public void onLocationResult(LocationResult locationResult)
      {
        if(locationResult != null){
          Location location_update = locationResult.getLastLocation();

          // Set lat long
          LAT_UPDATE = location_update.getLatitude();
          LONG_UPDATE = location_update.getLongitude();

          // File creation and writing
          try{
            File targetFile = new File(folderLocation, "problemstatement.txt");
            FileWriter writer = new FileWriter(targetFile, true);
            String data = LAT_UPDATE + ", " + LONG_UPDATE;
            writer.write(data + "\n");
            writer.flush();
            writer.close();
          }catch (Exception e){
            Toast.makeText(MyService.this, "Failed to write", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
          }
        }
      }
    };

    // Check for permissions, if granted get requests
    if(checkPermission()){
      client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    if(started == false){
      started = true;
      Log.d("Service", "Service started");
      Toast.makeText(this, "Service is running", Toast.LENGTH_SHORT).show();
    } else {
      Log.d("Service", " Service is still running");
      Toast.makeText(this, "Service already running", Toast.LENGTH_SHORT).show();
    }
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy()
  {
    Log.d("Service", "Service exited");
    Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    super.onDestroy();
  }

  private boolean checkPermission(){
    int permissionCheck_Coarse = ContextCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_COARSE_LOCATION);
    int permissionCheck_Fine = ContextCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_FINE_LOCATION);

    if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
      return true;
    } else {
      return false;
    }
  }
}
