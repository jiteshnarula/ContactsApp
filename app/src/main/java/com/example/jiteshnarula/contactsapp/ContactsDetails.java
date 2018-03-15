package com.example.jiteshnarula.contactsapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ContactsDetails extends AppCompatActivity {

    TextInputLayout nameInputLayout;
    TextInputLayout phoneNumberInputLayout;
    TextInputLayout emailInputLayout;
    ImageView profileImageView, cameraImageView;
    DatabaseHelper mDatabaseHelper;
    Button addButton;
    Button cancelButton;
    MyDatabase myDatabase;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Add Contact");
        setContentView(R.layout.contact_details_layout);
        nameInputLayout = (TextInputLayout) findViewById(R.id.nameTextLayout);
        phoneNumberInputLayout = (TextInputLayout) findViewById(R.id.phoneTextLayout);
        emailInputLayout = (TextInputLayout) findViewById(R.id.emailTextLayout);
        mDatabaseHelper = new DatabaseHelper(this);
        addButton = (Button) findViewById(R.id.addButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        myDatabase = new MyDatabase(this);
        profileImageView = (ImageView) findViewById(R.id.profile_image);
        cameraImageView = (ImageView) findViewById(R.id.cameraImage);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ContactsDetails.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    profileImageView.setEnabled(false);
                    ActivityCompat.requestPermissions(ContactsDetails.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    profileImageView.setEnabled(true);
                }

                new MaterialDialog.Builder(ContactsDetails.this)
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


    public void addButtonData(View view) {
        String name = nameInputLayout.getEditText().getText().toString();
        String phoneNumber = phoneNumberInputLayout.getEditText().getText().toString();
        String email = emailInputLayout.getEditText().getText().toString();
        profileImageView.setDrawingCacheEnabled(true);
        profileImageView.buildDrawingCache();
        Bitmap bitmap = profileImageView.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//        bitmap.compress(Bitmap.CompressFormat.JPEG,200,byteArrayOutputStream);
        byte[] newEntryImg = byteArrayOutputStream.toByteArray();


//       byte[] newEntryImg = imageViewToByte(profileImageView);

        if (name.length() != 0 || phoneNumber.length() != 0 || email.length() != 0) {
            AddData(name, phoneNumber, email, newEntryImg);
            //  Toast.makeText(this, "Image Succesfully Updated", Toast.LENGTH_LONG).show();

        } else {
            toastMessage("You must put something in TextField");
        }

    }

//    private byte[] imageViewToByte(ImageView profileImageView) {
//
//
//        Bitmap bitmap = ((BitmapDrawable) profileImageView.getDrawable()).getBitmap();
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream.toByteArray();
//        return byteArray;
//    }
//

    public void AddData(String name, String phoneNumber, String email, byte[] image) {
        boolean insertData = myDatabase.addContact(name, phoneNumber, email, image);
        if (insertData) {
            toastMessage("Contact Inserted Succesfully!!");
            startActivity(new Intent(ContactsDetails.this, MainActivity.class));


        } else {
            toastMessage("Something went wrong !!");


        }
    }


    public void cancelData(View view) {
        startActivity(new Intent(ContactsDetails.this, MainActivity.class));


    }

    public void toastMessage(String message) {
        Toast.makeText(ContactsDetails.this, message, Toast.LENGTH_LONG).show();
    }
}
