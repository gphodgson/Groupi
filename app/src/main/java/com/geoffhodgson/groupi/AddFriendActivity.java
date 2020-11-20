package com.geoffhodgson.groupi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddFriendActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private EditText edtEmail;
    private Button btnAddFriend;
    private TextView txtError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        androidx.appcompat.widget.Toolbar myToolbar = findViewById(R.id.tbMenu);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
            getSupportActionBar().setTitle("Add Friend");
        }

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        edtEmail = findViewById(R.id.edtEmail);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        txtError = findViewById(R.id.txtError);

        if(mUser != null){
            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DatabaseReference db  = FirebaseDatabase.getInstance().getReference("");
                    final FirebaseUser user = mAuth.getCurrentUser();
                    final DatabaseReference dbCurrentUser = db.child("users").child(user.getUid());
                    String otherUsername = edtEmail.getText().toString();
                    db.child("usernames").child(otherUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getValue() != null){
                                String otherUid = snapshot.getValue().toString();
                                final DatabaseReference dbOtherUser = db.child("users").child(otherUid);
                                dbCurrentUser.child("friends").child(otherUid).setValue(true);
                                dbOtherUser.child("friends").child(user.getUid()).setValue(true);
                                finish();

                            }else{
                                showError("User does not exist.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            showError(error.getMessage());
                        }
                    });


                }
            });
        }else {
            Toast.makeText(this, "Not Logged In.", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    private void showError(String err){
        txtError.setVisibility(View.VISIBLE);
        txtError.setText(err);
    }
}