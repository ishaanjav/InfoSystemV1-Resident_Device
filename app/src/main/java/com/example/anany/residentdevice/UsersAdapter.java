package com.example.anany.residentdevice;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.telephony.SmsManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UsersAdapter extends ArrayAdapter<ExistingListClass> {

    private Context mContext;
    private List<ExistingListClass> accountsList = new ArrayList<>();
    String rowid = "";
    Activity activity;
    CardView cardView;
    TextToSpeech t1;

    public UsersAdapter(Activity a, Context context, ArrayList<ExistingListClass> list) {
        super(context, 0, list);
        mContext = context;
        accountsList = list;
        activity = a;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.userlist_item, parent, false);


        final ExistingListClass accounts = accountsList.get(position);

        final ImageView imageView = listItem.findViewById(R.id.userpicture);
        StorageReference mImageRef =
                FirebaseStorage.getInstance().getReference("Visitors/" + accounts.getName() + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        if (mImageRef.getBytes(ONE_MEGABYTE) == null) {
            imageView.setImageResource(R.drawable.oneuser);

        } else {
            mImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    DisplayMetrics dm = new DisplayMetrics();

                       /* imageView.setMinimumHeight(dm.heightPixels);
                        imageView.setMinimumWidth(dm.widthPixels);*/
                    imageView.setImageBitmap(bm);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    imageView.setImageResource(R.drawable.oneuser);
                }
            });
        }

     /*   if (mImageRef == null) {
            imageView.setImageResource(R.drawable.oneuser);
        } else {
            mImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {

 Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    DisplayMetrics dm = new DisplayMetrics();

                       imageView.setMinimumHeight(dm.heightPixels);
                        imageView.setMinimumWidth(dm.widthPixels);
        imageView.setImageBitmap(bm);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    makeToast(exception.getMessage());
                }
            });
        }*/


        TextView description = listItem.findViewById(R.id.accountdescription);
        description.setText("        " + accounts.getDescription());

        TextView contactstitle = listItem.findViewById(R.id.contactstitle);


        SpannableString content = new SpannableString("Contact Info");
        content.setSpan(new UnderlineSpan(), 0, 12, 0);
        contactstitle.setText(content);
        TextView email = listItem.findViewById(R.id.accountemail);
        ImageView soundicon = listItem.findViewById(R.id.speak);

        soundicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t1 = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.US);
                            t1.setSpeechRate(0.9f);

                            t1.speak(accounts.getName() + " is your " + accounts.getRelations() + ".      Here is their description:     .   .   .  . . "  + accounts.getDescription(), TextToSpeech.QUEUE_FLUSH, null);

                        }
                    }
                });

            }
        });

        if (accounts.getEmail().isEmpty()) {
            email.setText("No email.");
        } else {
            email.setText(accounts.getEmail());
        }

        final TextView phone = listItem.findViewById(R.id.accountphone);

        if (accounts.getPhone_number().isEmpty()) {
            phone.setText("No phone #.");
        } else {
            String sphone = accounts.getPhone_number();
            String finalphone = "(" + sphone.substring(0, 3) + ") - " + sphone.substring(3, 6) + " - " + sphone.substring(6, 10);
            phone.setText(finalphone);
        }


        TextView name = listItem.findViewById(R.id.accountname);
        name.setText(accounts.getName() + ":   " + accounts.getRelations());

        Button sendphone = listItem.findViewById(R.id.sendphone);
        Button sendemail1 = listItem.findViewById(R.id.sendemail);
        sendemail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] TO = {"ishaanjav@gmail.com"};
                String[] CC = {""};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                try {
                    activity.startActivity(Intent.createChooser(emailIntent, "How do you want to send your email?"));

                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(activity.getApplicationContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sendphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:8482482353"));

                    activity.startActivity(callIntent);
                }
            }
        });


        ImageView sendemail = listItem.findViewById(R.id.emailimage);
        sendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] TO = {"ishaanjav@gmail.com"};
                String[] CC = {""};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                try {
                    activity.startActivity(Intent.createChooser(emailIntent, "How do you want to send your email?"));

                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(activity.getApplicationContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView phonecall = listItem.findViewById(R.id.phoneimage);
        phonecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:8482482353"));

                    activity.startActivity(callIntent);
                }
            }
        });


        return listItem;
    }

    private void makeToast(String S) {
        Toast.makeText(mContext, S, Toast.LENGTH_SHORT).show();
    }


}