package com.example.jiteshnarula.contactsapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

/**
 * Created by JiteshNarula on 12-03-2018.
 */

public class ViewProfile extends AppCompatActivity {
    ImageView profileImageView;
    ImageView emailImageView;
    ImageView phoneImageView;
    ImageView messageImageView;
    TextView nameTextView;
    TextView phoneTextView;
    TextView emailTextView;
    private String selectedName, selectedPhone, selectedEmail;
    private int selectedID;
    DatabaseHelper databaseHelper;
    MyDatabase myDatabase;
    Bitmap bitmap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_profile_layout);

        profileImageView = (ImageView) findViewById(R.id.profileImageView);
        emailImageView = (ImageView) findViewById(R.id.emailImageView);
        phoneImageView = (ImageView) findViewById(R.id.phoneImageView);
        messageImageView = (ImageView) findViewById(R.id.messageImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        phoneTextView = (TextView) findViewById(R.id.phoneTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);

        messageImageView = (ImageView) findViewById(R.id.messageImageView);
        phoneImageView = (ImageView) findViewById(R.id.phoneImageView);
        emailImageView = (ImageView) findViewById(R.id.emailImageView);


        databaseHelper = new DatabaseHelper(this);
        myDatabase = new MyDatabase(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent receivedIntent = getIntent();

        selectedID = receivedIntent.getIntExtra("id", -1);

        selectedName = receivedIntent.getStringExtra("name");

        selectedPhone = receivedIntent.getStringExtra("phone");

        selectedEmail = receivedIntent.getStringExtra("email");
        if (getIntent().hasExtra("byteArray")) {
            bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            profileImageView.setImageBitmap(bitmap);
        }


        setTitle(selectedName);
        nameTextView.setText(selectedName);
        phoneTextView.setText(selectedPhone);
        emailTextView.setText(selectedEmail);


        emailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:" + selectedEmail + "?subject=" + "subject" + "&body=" + "body");
                intent.setData(data);
                startActivity(intent);
            }
        });

        messageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", selectedPhone);
                smsIntent.putExtra("sms_body", "Body of Message");
                startActivity(smsIntent);
            }
        });

        phoneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                String p = "tel:" + selectedPhone;
                i.setData(Uri.parse(p));
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.id_editContact) {

            Intent editContacts = new Intent(ViewProfile.this, EditContacts.class);
            editContacts.putExtra("id", selectedID);
            editContacts.putExtra("name", selectedName);
            editContacts.putExtra("phone", selectedPhone);
            editContacts.putExtra("email", selectedEmail);
            editContacts.putExtra("BitmapImage", bitmap);
            startActivity(editContacts);
            return true;
        } else if (id == R.id.id_deleteContact) {


            myDatabase.deleteName(selectedID, selectedName, selectedPhone, selectedEmail);
            Toast.makeText(ViewProfile.this, "Deleted Succesfully", Toast.LENGTH_LONG);
            Intent refresh = new Intent(ViewProfile.this, MainActivity.class);
            startActivity(refresh);//Start the same Activity
            finish(); //finish Activity.

            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return super.onOptionsItemSelected(item);

        }
        return true;
    }


}
