package com.example.jiteshnarula.contactsapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by JiteshNarula on 13-03-2018.
 */

public class EditContacts extends AppCompatActivity {

    EditText nameEditText, emailEditText, phoneEditText;
    ImageView profileImageView, cameraImageView;
    DatabaseHelper databaseHelper;
    MyDatabase myDatabase;
    private String selectedName, selectedPhone, selectedEmail;
    private int selectedID;
    Button updateButton, cancelButton;
    Bitmap bitmap;
    public static final int SELECT_PHOTO = 1;
    public static final int CAPTURE_PHOTO = 2;
    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();
    private boolean hasImageChanged = false;
    Bitmap thumbnail;
    final int REQUEST_CODE_GALLERY = 999;
    private Handler progressBarbHandler = new Handler();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Edit Contact");
        setContentView(R.layout.edit_contacts_layout);

        profileImageView = (ImageView) findViewById(R.id.profile_image);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        cameraImageView = (ImageView) findViewById(R.id.cameraImageView);
        phoneEditText = (EditText) findViewById(R.id.phoneEditText);
        updateButton = (Button) findViewById(R.id.updateButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        myDatabase = new MyDatabase(this);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent receivedIntent = getIntent();

        selectedName = receivedIntent.getStringExtra("name");
        selectedPhone = receivedIntent.getStringExtra("phone");
        selectedEmail = receivedIntent.getStringExtra("email");
        selectedID = receivedIntent.getIntExtra("id", -1);
        Bitmap bitmap = (Bitmap) receivedIntent.getParcelableExtra("BitmapImage");

        nameEditText.setText(selectedName);
        phoneEditText.setText(selectedPhone);
        emailEditText.setText(selectedEmail);
        profileImageView.setImageBitmap(bitmap);

        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(EditContacts.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    profileImageView.setEnabled(false);
                    ActivityCompat.requestPermissions(EditContacts.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    profileImageView.setEnabled(true);
                }

                new MaterialDialog.Builder(EditContacts.this)
                        .title(R.string.uploadImages)
                        .items(R.array.uploadImages)
                        .itemsIds(R.array.itemIds)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                        photoPickerIntent.setType("image/*");
                                        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                                        break;
                                    case 1:
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        startActivityForResult(intent, CAPTURE_PHOTO);
                                        break;
                                    case 2:
                                        profileImageView.setImageResource(R.drawable.defaultmaleimage);
                                        break;
                                }
                            }

                        })
                        .show();


            }

        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditContacts.this, MainActivity.class);
                startActivity(intent);
            }
        });


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String email = emailEditText.getText().toString();

//                byte[] newEntryImg = profileImageView.getDrawable();
//
//                //Bitmap bitmap = ((BitmapDrawable)profileImageView.getDrawable()).getBitmap();
//                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG,200,byteArrayOutputStream);
//                byte[] newEntryImg = byteArrayOutputStream.toByteArray();
//
//                Bitmap bitmap = profileImageView.getDrawingCache();
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                //        bitmap.compress(Bitmap.CompressFormat.JPEG,200,byteArrayOutputStream);
//                byte[] newEntryImg = byteArrayOutputStream.toByteArray();

                profileImageView.setDrawingCacheEnabled(true);
                profileImageView.buildDrawingCache();
                Bitmap bitmap = profileImageView.getDrawingCache();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//        bitmap.compress(Bitmap.CompressFormat.JPEG,200,byteArrayOutputStream);
                byte[] newEntryImg = byteArrayOutputStream.toByteArray();

                if (!name.equals("") || !phone.equals("") || !email.equals("")) {
                    myDatabase.updateData(selectedID, name, phone, email, newEntryImg);
                    toastmessage("Update Succesfully!!");
                    Intent refresh = new Intent(EditContacts.this, MainActivity.class);
                    startActivity(refresh);//Start the same Activity
                    finish(); //finish Activity.
                }

            }

        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                profileImageView.setEnabled(true);
            }
        }
    }


    public void setProgressBar() {
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Please wait...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressBarStatus < 100) {
                    progressBarStatus += 30;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    progressBarbHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressBarStatus);
                        }
                    });
                }
                if (progressBarStatus >= 100) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressBar.dismiss();
                }

            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    //set Progress Bar
                    setProgressBar();
                    //set profile picture form gallery
                    profileImageView.setImageBitmap(selectedImage);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        } else if (requestCode == CAPTURE_PHOTO) {
            if (resultCode == RESULT_OK) {
                onCaptureImageResult(data);
            }
        }
    }


    private void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");

        //set Progress Bar
        setProgressBar();
        //set profile picture form camera
        profileImageView.setMaxWidth(200);
        profileImageView.setImageBitmap(thumbnail);

    }


    public void toastmessage(String message) {

        Toast.makeText(EditContacts.this, message, Toast.LENGTH_LONG).show();
    }
}
