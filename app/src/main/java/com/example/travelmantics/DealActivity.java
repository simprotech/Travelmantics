package com.example.travelmantics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

//import android.support.v7.app.AppCompatActivity;


public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private static final int PICTURE_RESULT =42;

    EditText txtTitle;
    EditText txtDescription;
    EditText txtPrice;
    private TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);      // Changed this from R.Layout.activity_deal
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        txtTitle = (EditText) findViewById(R.id.txtTitle);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        txtPrice = (EditText) findViewById(R.id.txtPrice);
        Button btnimage  = findViewById (R.id.btnImage) ;
        btnimage.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (Intent.ACTION_GET_CONTENT);
                intent.setType ("image/jpeg");
                intent.putExtra(intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult (intent.createChooser (intent,"Insert Picture"),PICTURE_RESULT);

            }
        });


        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if(deal == null) {
            deal = new TravelDeal();
        }
        this.deal = deal;
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal removed", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (requestCode ==  PICTURE_RESULT && resultCode == RESULT_OK) {
                    }
        Uri imageUri = data.getData();
        StorageReference ref = FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());
        ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot> () {
            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                String url = taskSnapshot.getDownloadUrl().toString();
//                String pictureName = taskSnapshot.getStorage().getPath();
//                deal.setImageUrl(url);
//                deal.setImageName(pictureName);
//                Log.d("Url: ", url);
//                Log.d("Name", pictureName);
//                showImage(url);
//            }
        });

    }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater ();
        inflater.inflate (R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem (R.id.delete_menu).setVisible (true);
            menu.findItem (R.id.save_menu).setVisible (true);
            enableEditTexts (true);

        } else {
            menu.findItem (R.id.delete_menu).setVisible (false);
            menu.findItem (R.id.save_menu).setVisible (false);
            enableEditTexts (false);

        }
        return true;
    }

    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }
    private void deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Please save the menu before deleting", Toast.LENGTH_LONG).show();
            return;
        }
        mDatabaseReference.child(deal.getId()).removeValue();

    }
    private void saveDeal() {


        deal.setTitle(txtTitle.getText().toString());
        deal.setDescription(txtDescription.getText().toString());
        deal.setPrice(txtPrice.getText().toString());
        if (deal.getId() == null) {
            mDatabaseReference.push().setValue(deal);
        }
        else
        {
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }

    }
    private void clean() {
        txtTitle.setText("");
        txtPrice.setText("");
        txtDescription.setText("");
        txtTitle.requestFocus();
    }
    private void enableEditTexts(boolean isEnabled){
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled (isEnabled);
        txtPrice.setEnabled (isEnabled);

    }

}


