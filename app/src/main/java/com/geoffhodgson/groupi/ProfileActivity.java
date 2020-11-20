package com.geoffhodgson.groupi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private TextView txtEmail, txtVerified;
    private Button btnAddFriend;
    private ListView lstFriends;

    private ArrayList<User> friends;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        friends = new ArrayList<User>();

        if(mUser != null){
            androidx.appcompat.widget.Toolbar myToolbar = findViewById(R.id.tbMenu);
            if (myToolbar != null) {
                setSupportActionBar(myToolbar);
                getSupportActionBar().setTitle(mUser.getDisplayName());
            }

            txtEmail = findViewById(R.id.txtEmail);
            txtEmail.setText(mUser.getEmail());

            txtVerified = findViewById(R.id.txtVerified);
            txtVerified.setText(mUser.isEmailVerified() ? "Yes" : "No");

            btnAddFriend = findViewById(R.id.btnAddFriend);
            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), AddFriendActivity.class);
                    startActivity(intent);
                }
            });

            lstFriends = findViewById(R.id.lstFriends);
            userAdapter = new UserAdapter(this, R.layout.list_item, friends);
            lstFriends.setAdapter(userAdapter);


            final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference dbUserFriends = db.child("users").child(mUser.getUid()).child("friends");

            dbUserFriends.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("Geoff", "onChildAdded:" + snapshot.getKey());
                    String friendUid = snapshot.getKey();
                    DatabaseReference dbFriend = db.child("users").child(friendUid);
                    dbFriend.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User friend = snapshot.getValue(User.class);
                            friends.add(0, friend);

                            lstFriends.setAdapter(userAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else{
            Toast.makeText(this, "Not Logged In.", Toast.LENGTH_LONG).show();
        }
    }

    private class UserAdapter extends ArrayAdapter<User>{
        private ArrayList<User> items;

        public UserAdapter(Context context, int textViewResourceId, ArrayList<User> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }
            User o = items.get(position);
            if (o != null) {
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                if (tt != null) {
                    tt.setText(o.getName());
                }
                if (bt != null) {
                    bt.setText("Email: " + o.getEmail());
                }
            }
            return v;
        }

    }
}