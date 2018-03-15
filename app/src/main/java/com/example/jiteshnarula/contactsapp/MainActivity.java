package com.example.jiteshnarula.contactsapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.support.v7.widget.ListViewCompat;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    FloatingActionButton fab;
    Button addContacts;
    ListView listView;
    DatabaseHelper databaseHelper;
    MyDatabase myDatabase;
    ConstraintLayout constraintLayout1, constraintLayout2;
    ArrayList<ListDataCollection> listData = new ArrayList<>();
    //    ArrayList<String> name = new ArrayList<>();
//    ArrayList<String> phone  = new ArrayList<>();
    private long backPressedTime;

    private Toast backToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        addContacts = (Button) findViewById(R.id.addContacts);
        listView = (ListView) findViewById(R.id.listView);
        myDatabase = new MyDatabase(this);
        databaseHelper = new DatabaseHelper(this);
        constraintLayout1 = (ConstraintLayout) findViewById(R.id.constraintLayout1);
        constraintLayout2 = (ConstraintLayout) findViewById(R.id.constraintLayout2);

        View emptyView = findViewById(R.id.constraintLayout1);
        listView.setEmptyView(emptyView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ContactsDetails.class));
            }
        });

        addContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ContactsDetails.class));
            }
        });


        populateListView();

        ContactListAdapter1 ListAdapter = new ContactListAdapter1(this, R.layout.adapter_view_layout, listData);
        listView.setAdapter(ListAdapter);
    }


    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();

    }

    private void populateListView() {


        Log.e("Populate list view", "Displaying all data in listview");
        Cursor data = myDatabase.getData();

//        ArrayList<String> listData  = new ArrayList<>();
//        ArrayList<String> listData1  = new ArrayList<>();

        while (data.moveToNext()) {
//            listData.add(data.getString(data.getColumnIndex(DatabaseHelper.COL2)));
//            listData1.add(data.getString(data.getColumnIndex(DatabaseHelper.COL3)));

//            name.add(data.getString(data.getColumnIndex(DatabaseHelper.COL2)));
//            phone.add(data.getString(data.getColumnIndex(DatabaseHelper.COL3)));


            int id = data.getInt(0);
            String name = data.getString(1);
            String phone = data.getString(2);
            byte[] image = data.getBlob(4);

            listData.add(new ListDataCollection(id, name, phone, image));

        }


    }


    public class ContactListAdapter1 extends BaseAdapter {

        private Context mContext;
        private int layout;
        ArrayList<ListDataCollection> textList;


        public ContactListAdapter1(Context mContext, int layout, ArrayList<ListDataCollection> textList) {
            this.mContext = mContext;
            this.layout = layout;
            this.textList = textList;
        }

        public int getCount() {
            // TODO Auto-generated method stub

            return textList.size();

        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return textList.get(position);
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        private class viewHolder {

            TextView textviewname;
            TextView textviewphone_number;
            ImageView imageView1;
        }

        public View getView(final int position, View child, final ViewGroup parent) {


            View row = child;
            viewHolder holder;
            if (row == null) {

                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = layoutInflater.inflate(layout, null);

                holder = new viewHolder();

                holder.textviewname = (TextView) row.findViewById(R.id.nameTextView);
                holder.textviewphone_number = (TextView) row.findViewById(R.id.phoneNumberTextView);
                holder.imageView1 = (ImageView) row.findViewById(R.id.defaultImageView);
                row.setTag(holder);

            } else {

                holder = (viewHolder) row.getTag();
            }

            final ListDataCollection ldc = textList.get(position);


            holder.textviewname.setText(ldc.getName());
            holder.textviewphone_number.setText(ldc.getPhone());
            final byte[] ldcImage = ldc.getImage();
            final Bitmap bitmap = BitmapFactory.decodeByteArray(ldcImage, 0, ldcImage.length);
            holder.imageView1.setImageBitmap(bitmap);


            final MyDatabase myDatabase = new MyDatabase(mContext);

            row.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor data = myDatabase.getItemId(ldc.getId());
                    int itemId = -1;
                    String name = "";
                    String phone = "";
                    String email = "";
                    byte[] image = null;

                    while (data.moveToNext()) {
                        itemId = data.getInt(0);
                        name = data.getString(1);
                        phone = data.getString(2);
                        email = data.getString(3);
                        image = data.getBlob(4);

                        Intent viewIntent = new Intent(MainActivity.this, ViewProfile.class);
                        viewIntent.putExtra("id", itemId);
                        viewIntent.putExtra("name", name);
                        viewIntent.putExtra("phone", phone);
                        viewIntent.putExtra("email", email);
                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                        viewIntent.putExtra("byteArray", bs.toByteArray());
                        startActivity(viewIntent);


                    }

                    if (itemId > -1) {
                        // toastMessage("id is "+ itemId+ name);
                    } else {
                        toastMessage("No id associate with this name");
                    }
                }
            });
            return row;
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
