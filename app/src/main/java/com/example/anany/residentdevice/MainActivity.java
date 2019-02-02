package com.example.anany.residentdevice;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    ListView listview;
    CardView hidden;

    TextToSpeech t1;

    ImageView soundicon;

    int times = 1;
    Location currentLocation;
    FusedLocationProviderClient mFusedLocationProviderClient;

    ArrayList<String> update = new ArrayList<>();

    String classifier2, phones, email, name, description, relation, time;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private boolean mLocationPermissionGranted;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        String locationProvider = LocationManager.GPS_PROVIDER;
        currentLocation = locationManager.getLastKnownLocation(locationProvider);
        if (currentLocation != null) {
            final double lng = currentLocation.getLongitude();
            final double lat = currentLocation.getLatitude();
            makeToast("Location " + lng + " " + lat);
            Log.d("Log", "longtitude=" + lng + ", latitude=" + lat);
            //  makeToast(lat + " " + lng);
        /*    final LatLng home = new LatLng(lat, lng);
            LatLng texas = new LatLng(99.9018, 31.9686);
            mMap.addMarker(new MarkerOptions().position(home).title("Marker at Home."));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(texas));*/
        }

    }

    private void Speak(final String toSpeak, final float rate) {
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                    t1.setSpeechRate(rate);

                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = findViewById(R.id.listview);
        hidden = findViewById(R.id.hidden);

        getLocationPermission();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float) scale;
        //  makeToast(Float.toString(batteryPct));
        batteryPct *= 100;
        if (batteryPct < 40) {
            Speak(batteryPct + "%: your battery life is getting low. Consider charging your device.", 0.85f);
            makeToast(batteryPct + "%: your battery life is getting low. Consider charging your device.");
        } else if (batteryPct < 20) {
            Speak(batteryPct + "%: your battery life is really low. It is recommended that you charge your device.", 0.85f);
            makeToast(batteryPct + "%: your battery life is really low. It is recommended that you charge your device.");
        } else if (batteryPct < 7) {
            Speak(batteryPct + "%: your battery life is very low and the device may die soon. Charge your device.", 0.85f);
            makeToast(batteryPct + "%: your battery life is very low and the device may die soon. Charge your device.");
        }

        final FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database2.getReference("RLatest Login");
        final DatabaseReference databaseReference2 = database2.getReference("RWrong Login");
        final DatabaseReference databaseReference3 = database2.getReference("RLatest Create");
        final DatabaseReference databaseReference4 = database2.getReference("NRWrong Login");

        ValueEventListener valueEventListener0 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> times = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    times.add(child.child("Time").getValue(String.class));
                }
                if (times.isEmpty()) {
                    //break;
                } else {
                    DatabaseReference dbref = database2.getReference("Pending Requests");
                    ValueEventListener valueEventListener1 = new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                classifier2 = "Pending";

                                String tphone = child.child("Phone").getValue(String.class);
                                if (tphone == null) {
                                    phones = "";

                                } else {
                                    phones = (tphone);
                                }
                                String tname = child.child("Name").getValue(String.class);
                                String temail = child.child("Email").getValue(String.class);
                                if (temail == null) {
                                    email = ("");
                                } else {
                                    email = (temail);

                                }
                                String tdescript = child.child("Description").getValue(String.class);
                                if (tdescript == null) {
                                    description = ("");
                                } else {
                                    description = (tdescript);
                                }
                                String trelation = child.child("Relation").getValue(String.class);
                                if (trelation == null) {
                                    relation = ("");
                                } else {
                                    relation = (trelation);
                                }
                                name = (tname);
                            }
                            makeAlertDialog(name, phones, email, relation, description, databaseReference3, classifier2);

                        }
                        //    makeToast(name);


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    };
                    dbref.addValueEventListener(valueEventListener1);


                    //Read from Visitor Logins. Get the latest login. Display in custom Alert Dialog using method above with parameters.
                    //Delete everything in Latest Login
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference3.addValueEventListener(valueEventListener0);

        ValueEventListener valueEventListener334 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> value = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    value.add(child.child("Time").getValue(String.class));
                    String st = child.child("Time").getValue(String.class);
                    //   makeToast(st);
                }

                if (value.isEmpty()) {
                    //   makeToast("NULL " + value);
                    //        notifyForChange();
                } else {
                    //  makeToast(value.toString());
                    warnNotification(databaseReference4, value.get(0));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference4.addValueEventListener(valueEventListener334);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> times = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    times.add(child.child("Time").getValue(String.class));
                }
                if (times.isEmpty()) {
                    //break;
                } else {
                    final DatabaseReference dbref = database2.getReference("Events Log");
                    ValueEventListener valueEventListener1 = new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {

                                classifier2 = child.child("Classifier").getValue(String.class);

                                if (classifier2.equals("Visitor")) {
                                    String tphone = child.child("Phone").getValue(String.class);
                                    if (tphone == null) {
                                        phones = "";

                                    } else {
                                        phones = (tphone);
                                    }
                                    String tname = child.child("Name").getValue(String.class);
                                    String temail = child.child("Email").getValue(String.class);
                                    if (temail == null) {
                                        email = ("");
                                    } else {
                                        email = (temail);
                                    }
                                    String tdescript = child.child("Description").getValue(String.class);
                                    if (tdescript == null) {
                                        description = ("");
                                    } else {
                                        description = (tdescript);
                                    }
                                    String trelation = child.child("Relation").getValue(String.class);
                                    if (trelation == null) {
                                        relation = ("");
                                    } else {
                                        relation = (trelation);
                                    }
                                    name = (tname);
                                }

                            }

                            databaseReference.removeValue();
                            //    makeToast(name);
                            makeAlertDialog(name, phones, email, relation, description, databaseReference, classifier2);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    };
                    dbref.addValueEventListener(valueEventListener1);

                    //Read from Visitor Logins. Get the latest login. Display in custom Alert Dialog using method above with parameters.
                    //Delete everything in Latest Login
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);

        ValueEventListener valueEventListener3 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> times = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    times.add(child.child("Time").getValue(String.class));
                }
                if (times.isEmpty()) {
                    //break;
                } else {
                    DatabaseReference dbref = database2.getReference("Events Log");
                    ValueEventListener valueEventListener1 = new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {

                                classifier2 = child.child("Classifier").getValue(String.class);

                                if (classifier2.equals("Failed")) {
                                    name = child.child("Name").getValue(String.class);
                                    time = child.child("Time").getValue(String.class);
                                }

                            }
                            //    makeToast(name);

                            makeFailedDialog(name, time, databaseReference2, classifier2);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    };
                    dbref.addValueEventListener(valueEventListener1);


                    //Read from Visitor Logins. Get the latest login. Display in custom Alert Dialog using method above with parameters.
                    //Delete everything in Latest Login
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference2.addValueEventListener(valueEventListener3);

        FirebaseDatabase firebaseDatabase3 = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference6 = firebaseDatabase3.getReference("RNotify Login");


        ValueEventListener valueEventListener6 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> value = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    value.add(child.child("Name").getValue(String.class));
                    String st = child.child("Name").getValue(String.class);
                    //   makeToast(st);
                }

                if (value.isEmpty()) {
                    //   makeToast("NULL " + value);
                    notifyForChange();
                } else {
                    //  makeToast(value.toString());
                    makeNotification(databaseReference6, value.get(0));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference6.addValueEventListener(valueEventListener6);


        readToListView();
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    105);
        } else {
            readToListView();
        }
        checkLocation();

    }

    private void warnNotification(DatabaseReference databaseReference102, String value) {
        //  makeToast("IN MAKE NOTIFICATION!");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "Channel")
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("A Visitor is at the Front Door and they failed to log in!")
                .setContentText("A person is at your front door and they failed to log in.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("A person is at your front door and they failed to log in."))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        times = 0;
        notificationManager.notify(times, mBuilder.build());
        databaseReference102.removeValue();
        //   notifyForChange();
    }

    private void checkLocation() {
      /*  Runnable r = new Runnable() {
            @Override
            public void run() {
*/
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dbref = firebaseDatabase.getReference("Update");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                update.clear();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    update.add(child.child("Value").getValue(String.class));
                }

                if (update.isEmpty()) {
                    //   makeToast("EMPTY");
                } else {
                    if (update.get(update.size() - 1).equals("True")) {
                        FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference13 = firebaseDatabase1.getReference("Location");
                        HashMap<String, Double> hashMap = new HashMap<>();
                        //makeToast("TRUE");

                        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling

                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        String locationProvider = LocationManager.GPS_PROVIDER;
                        currentLocation = locationManager.getLastKnownLocation(locationProvider);
                        if (currentLocation != null) {
                            final double lng = currentLocation.getLongitude();
                            final double lat = currentLocation.getLatitude();
                            // makeToast("LOCATION: Line 159 " + lng + " " + lat);
                            hashMap.put("Latitude", lat);
                            hashMap.put("Longitude", lng);
                            //      makeToast(hashMap.toString());
                            databaseReference13.push().setValue(hashMap);

                        }
                    } else {
                        //Caretaker is not in GPS page.
                        //checkLocation();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        dbref.addValueEventListener(valueEventListener);
/*
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 7000);*/

    }

    private void makeNotification(DatabaseReference databaseReference102, String value) {
        //  makeToast("IN MAKE NOTIFICATION!");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "Channel")
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("A Visitor Logged In!")
                .setContentText(value + " just logged in! They are at the front door.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(value + " just logged in! They are at the front door."))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        times++;
        //Changed it from times to 0 in order to avoid notification spam in the tray.
        notificationManager.notify(0, mBuilder.build());
        databaseReference102.removeValue();
        //   notifyForChange();
    }

    private void notifyForChange() {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                final DatabaseReference databaseReference6 = firebaseDatabase.getReference("RNotify Login");

                ValueEventListener valueEventListener6 = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> value = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            value.add(child.child("Name").getValue(String.class));
                            String st = child.child("Name").getValue(String.class);
                            //   makeToast(st);
                        }

                        if (value.isEmpty()) {
                            //   makeToast("NULL " + value);
                            //  notifyForChange();
                        } else {
                            //  makeToast(value.toString());
                            makeNotification(databaseReference6, value.get(0));

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };

                databaseReference6.addValueEventListener(valueEventListener6);

                //Write the code to read from database path and check if it not empty.
                //If it is not empty. Get the data at the path and make a notification from it by passing the info into a notification method.
                // Also, in that notification method, delete the databaseReference. Then, call the notifyForChange() method.
                //If it is empty, call this method again.


            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 5000);

    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }

    }


    private void makeFailedDialog(String name, final String time, final DatabaseReference databaseReference10, String classifier2) {
        final Dialog alert = new Dialog(this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.alertfailedlogin);
        alert.setCancelable(true);

        TextView title = alert.findViewById(R.id.title);

        title.setText("Be Aware!");


        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                    t1.speak("A visitor is at the front door and they failed to sign in. Would you like to call your caretaker?", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        MediaPlayer ring2 = MediaPlayer.create(MainActivity.this, R.raw.danger);
        ring2.start();

        TextView sphone = alert.findViewById(R.id.phone);
        String[] split = time.split(" ");
        String date = split[0] + " " + split[1] + " " + split[2];
        String numtime = split[3] + " " + split[4];

        sphone.setText("The visitor tried entering in this username: " + name + "\non: " + date + "\nat: " + numtime + ".");

        Button cancel = alert.findViewById(R.id.cancel_action);

        Button warn = alert.findViewById(R.id.warn);
        // Button view = alert.findViewById(R.id.existingusers);
//Oct 28, 2018 8:24:19 PM"

        warn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:8482482353"));

                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            100);
                } else {
                    startActivity(callIntent);
                }
                alert.dismiss();
                databaseReference10.removeValue();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                databaseReference10.removeValue();
            }
        });
        alert.show();

    }

    private void makeAlertDialog(final String name, String phone, String email, String relation, final String description, final DatabaseReference databaseReference, String classifier) {
        //Take the parameters and put them in Alert Dialog.
        final Dialog alert = new Dialog(this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.visitorsignedin);
        alert.setCancelable(true);

        Button moreinfo = alert.findViewById(R.id.getinfo);
        TextView title = alert.findViewById(R.id.title);
        final String tm = name;
        final String rm = relation;
        if (classifier.equals("Pending")) {
            title.setText("Account Pending Approval!");
            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.US);
                        t1.setSpeechRate(0.9f);

                        t1.speak(tm + " is at the front door. They are your " + rm, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });
        } else if (classifier.equals("Visitor")) {
            title.setText("A Visitor Signed In!");
            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.US);
                        t1.setSpeechRate(0.9f);

                        t1.speak(tm + " is at the front door. They are your " + rm, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });
        }

        MediaPlayer ring2 = MediaPlayer.create(MainActivity.this, R.raw.doorbelltone);
        ring2.start();

        TextView sname = alert.findViewById(R.id.namer);
        TextView sphone = alert.findViewById(R.id.phone);
        TextView semail = alert.findViewById(R.id.email);
        TextView srelation = alert.findViewById(R.id.relation);
        final StorageReference mImageRef =
                FirebaseStorage.getInstance().getReference("Visitors/" + name + ".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        final ImageView imageView2 = alert.findViewById(R.id.image);

        if (mImageRef.getBytes(ONE_MEGABYTE) == null) {
            ImageView imageView = alert.findViewById(R.id.image);
            imageView.setImageResource(R.drawable.oneuser);
        } else {
            mImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    DisplayMetrics dm = new DisplayMetrics();

                       /* imageView.setMinimumHeight(dm.heightPixels);
                        imageView.setMinimumWidth(dm.widthPixels);*/
                    imageView2.setImageBitmap(bm);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    imageView2.setImageResource(R.drawable.oneuser);
                }
            });
        }

        sname.setText(name);
        if (phone.isEmpty()) {
            sphone.setText("No Phone #");
        } else {
            phone = "(" + phone.substring(0, 3) + ") - " + phone.substring(3, 6) + " - " + phone.substring(6, 10);
            sphone.setText("Phone: " + phone);

        }

        if (email.isEmpty()) {
            semail.setText("No Email Address");
        } else {
            semail.setText("Email: " + email);
        }

        srelation.setText("Relation: " + relation);

        Button cancel = alert.findViewById(R.id.cancel_action);

        moreinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                longToast(description);
                t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.US);
                            t1.setSpeechRate(0.9f);
                            t1.speak("Here is a description of " + tm + ". " + description, TextToSpeech.QUEUE_FLUSH, null);

                        }
                    }
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
                databaseReference.removeValue();
            }
        });
        alert.show();
    }

    private void longToast(String description) {
        Toast.makeText(getApplicationContext(), description, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if (requestCode == 105) {
            readToListView();

        }
    }


    public class Messaging extends FirebaseMessagingService {
        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {

            makeToast(remoteMessage.getNotification().getBody());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //and this to handle actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:8482482353"));

                startActivity(callIntent);
            }
            return true;
        } else if (id == R.id.text2speech) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 60);
                // TODO: Consider calling
                makeToast("No permission");
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details
            } else {
                Intent pick = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(pick, 100);
            }
            return true;
            //do code for launching camera and doing text to speech thing.
        } else if (id == R.id.objectdetect) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 60);
                // TODO: Consider calling
                makeToast("Grant permission");
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details
            } else {
                Intent pick = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(pick, 1);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    Boolean dismissed = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();

            recognizer.processImage(image)
                    .addOnSuccessListener(
                            new OnSuccessListener<FirebaseVisionText>() {
                                @Override
                                public void onSuccess(FirebaseVisionText texts) {

                                    processTextRecognitionResult(texts);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception

                                    e.printStackTrace();
                                }
                            });

        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            FirebaseVisionLabelDetectorOptions options =
                    new FirebaseVisionLabelDetectorOptions.Builder()
                            .setConfidenceThreshold(0.55f)
                            .build();
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

            // Or, to set the minimum confidence required:
            FirebaseVisionLabelDetector detector = FirebaseVision.getInstance()
                    .getVisionLabelDetector(options);

            Task<List<FirebaseVisionLabel>> result =
                    detector.detectInImage(image)
                            .addOnSuccessListener(
                                    new OnSuccessListener<List<FirebaseVisionLabel>>() {
                                        @Override
                                        public void onSuccess(List<FirebaseVisionLabel> labels) {
                                            String total = "No objects detected.";
                                            DecimalFormat decimalFormat = new DecimalFormat("#.##");
                                            for (FirebaseVisionLabel label : labels) {
                                                String text = label.getLabel();
                                                String entityId = label.getEntityId();
                                                float confidence = label.getConfidence();
                                                String tt = decimalFormat.format(confidence * 100);
                                                total += ("Object: " + text + " Confidence: " + tt + "\n");
                                            }
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
                                            builder1.setTitle("Detected Objects");
                                            Speak(total, 0.8f);
                                            builder1.setMessage(total);
                                            builder1.setCancelable(true);
                                            builder1.setNeutralButton(
                                                    "Ok",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                            dismissed = true;
                                                        }
                                                    });
                                            builder1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(final DialogInterface arg0) {
                                                    dismissed = true;
                                                }
                                            });

                                            AlertDialog alert11 = builder1.create();
                                            alert11.show();
                                            dismissAlert(alert11);

                                            if (labels.isEmpty()) {
                                                makeToast("EMPTY");
                                            } else {
                                                makeToast(labels.toString());
                                            }
                                        }
                                    })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            makeToast(e.toString());
                                        }
                                    });
        }
    }

    private void dismissAlert(final AlertDialog alert11) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (dismissed) {

                } else {
                    alert11.dismiss();
                }
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 15000);
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {
        final String pleaseWork = texts.getText();
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            makeToast("No text found");
            return;
        } else {
            longToast("The reading will begin shortly.");
            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.US);
                        t1.setSpeechRate(0.9f);
                        t1.speak(pleaseWork, TextToSpeech.QUEUE_FLUSH, null);

                    }
                }
            });

        }
    }

    private void readToListView() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("/Create Account");
        listview = findViewById(R.id.listview);


        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> name = new ArrayList<>();
                ArrayList<String> password = new ArrayList<>();
                ArrayList<String> username = new ArrayList<>();
                ArrayList<String> phone = new ArrayList<>();
                ArrayList<String> email = new ArrayList<>();
                ArrayList<String> description = new ArrayList<>();
                ArrayList<String> relation = new ArrayList<>();
                ArrayList<String> times = new ArrayList<>();


                ArrayList<ExistingListClass> arrayList = new ArrayList<>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String sname = child.child("Name").getValue(String.class);
                    String spassword = child.child("Password").getValue(String.class);
                    String susername = child.child("Username").getValue(String.class);
                    String sphone = child.child("Phone").getValue(String.class);
                    String semail = child.child("Email").getValue(String.class);
                    String sdescription = child.child("Description").getValue(String.class);
                    String srelation = child.child("Relation").getValue(String.class);
                    String stime = child.child("Approved").getValue(String.class);

                    if (sname.contains("Caretaker")) {

                    } else {
                        name.add(sname);
                        username.add(susername);
                        password.add(spassword);
                        phone.add(sphone);
                        email.add(semail);
                        description.add(sdescription);
                        relation.add(srelation);
                        times.add(stime);
                        arrayList.add(new ExistingListClass(sname, susername, spassword, sdescription, sphone, semail, srelation, stime));

                    }

                }

                ArrayList<ExistingListClass> arrayList2 = new ArrayList<>();

                for (int i = 0; i < arrayList.size(); i++) {
                    arrayList2.add(arrayList.get(arrayList.size() - 1 - i));
                }


                ArrayAdapter mAdapter = new UsersAdapter(MainActivity.this, getApplicationContext(), arrayList2);

                listview.setAdapter(mAdapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        makeToast("Entry clicked");
                       /* ExistingListClass entry = (ExistingListClass) parent.getItemAtPosition(position);
                        makeToast(entry.getName());*/
                    }
                });


                if (name.isEmpty()) {
                    hidden.setVisibility(View.VISIBLE);
                } else {
                    hidden.setVisibility(View.INVISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);

    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }


}
