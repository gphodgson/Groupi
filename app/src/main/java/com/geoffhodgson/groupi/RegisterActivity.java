package com.geoffhodgson.groupi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText edtEmail, edtPassword, edtNickname;
    private TextView txtError;
    private Button btnRegister;

    private final String TAG = "Geoff";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        androidx.appcompat.widget.Toolbar myToolbar = findViewById(R.id.tbMenu);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
            getSupportActionBar().setTitle("Register");

        }

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtNickname = findViewById(R.id.edtNickname);

        txtError = findViewById(R.id.txtError);

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(edtNickname.getText().toString())
                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "User profile updated.");
                                                            FirebaseUser user = mAuth.getCurrentUser();
                                                            FirebaseDatabase db  = FirebaseDatabase.getInstance();
                                                            String path = String.format("users/%s/", user.getUid());
                                                            DatabaseReference userRef = db.getReference(path+"name");
                                                            DatabaseReference emailRef = db.getReference(path+"email");
                                                            DatabaseReference friendsRef = db.getReference(path+"friends/");
                                                            DatabaseReference activeRef = db.getReference(path+"active");
                                                            DatabaseReference verifiedRef = db.getReference(path+"verified");

                                                            userRef.setValue(user.getDisplayName());
                                                            emailRef.setValue(user.getEmail());
                                                            activeRef.setValue(true);
                                                            verifiedRef.setValue(false);
                                                            friendsRef.setValue(null);
                                                            db.getReference("/usernames/"+user.getDisplayName()).setValue(user.getUid());
                                                            finish();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        showError(task.getException().getMessage());
                                    }

                                }
                            });
                }catch(IllegalArgumentException err){
                    showError(err.getMessage());
                }

            }
        });
    }

    private void showError(String _str){
        txtError.setVisibility(View.VISIBLE);
        txtError.setText(_str);
    }
}