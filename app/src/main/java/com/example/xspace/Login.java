package com.example.xspace;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.zip.Inflater;


public class Login extends AppCompatActivity {

    LoginDB LDB;
    Warehouse WH;
    private Button SignUp;
    private Button Login;
    private TextView fadingmessage;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginview);

        FirebaseApp.initializeApp(this);
        LDB = new LoginDB(this);




        SignUp = findViewById(R.id.NewUser);
        Login = findViewById(R.id.LoginB);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginButton();

            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignUpAlert();

            }
        });
    }
    private void SignUpAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        View SignUpAlertLayout = getLayoutInflater().inflate(R.layout.signupxml, null);

        // Get references to buttons in the XML
        Button positiveButton = SignUpAlertLayout.findViewById(R.id.xmlSignUP);
        Button negativeButton = SignUpAlertLayout.findViewById(R.id.xmlCancel);

        // Create the AlertDialog
        builder.setView(SignUpAlertLayout);
        AlertDialog dialog = builder.create();

        // Set up animations
        fadingmessage = SignUpAlertLayout.findViewById(R.id.FadingTextxml);
        final Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        final Animation fadeout = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText Username = SignUpAlertLayout.findViewById(R.id.xmlUsername);
                EditText Password = SignUpAlertLayout.findViewById(R.id.xmlPassword);
                EditText Email = SignUpAlertLayout.findViewById(R.id.xmlEmail);

                String UsernameS = Username.getText().toString();
                String PasswordS = Password.getText().toString();
                String EmailS = Email.getText().toString();

                LDB.checkEmailExists(EmailS, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot.isEmpty()) {
                                // Email does not exist, proceed to insert data
                                LDB.insertData(UsernameS, PasswordS, EmailS, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Add or update email in Profile database
                                            ProfileFirebase profile = new ProfileFirebase();
                                            profile.setEmail(EmailS);
                                            profile.addOrUpdateProfile(profile);


                                            dialog.dismiss();
                                        } else {
                                            // Handle the error
                                            Log.e("SignUpError", "Error inserting data", task.getException());
                                            Toast.makeText(Login.this, "Error inserting data", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // Send fading message that the email is already in use
                                fadingmessage.setVisibility(View.VISIBLE);
                                fadingmessage.startAnimation(fadein);

                                fadein.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {}

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        fadingmessage.startAnimation(fadeout);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {}
                                });

                                fadeout.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {}

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        fadingmessage.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {}
                                });
                            }
                        } else {
                            // Handle the error
                            Log.e("SignUpError", "Error checking email existence", task.getException());
                            Toast.makeText(Login.this, "Error checking email existence", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void LoginButton() {
        Button Login = findViewById(R.id.LoginB);
        LoginDB LDB = new LoginDB(this);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Access EditText fields directly from the activity's view hierarchy
                EditText Username = findViewById(R.id.Username);
                EditText Password = findViewById(R.id.Password);

                String UsernameS = Username.getText().toString();
                String PasswordS = Password.getText().toString();

                Log.d("LoginDebug", "Username: " + UsernameS);
                Log.d("LoginDebug", "Password: " + PasswordS);

                if (UsernameS != null && !UsernameS.isEmpty() && PasswordS != null && !PasswordS.isEmpty()) {
                    LDB.validateUser(UsernameS, PasswordS, new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (!querySnapshot.isEmpty()) {
                                    // User is validated, get the email and start HomePage activity
                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        String email = document.getString("email");
                                        Intent intent = new Intent(Login.this, HomePage.class);
                                        intent.putExtra("EXTRA_EMAIL", email);
                                        Log.d("Login", "Starting HomePage Activity with email: " + email);
                                        startActivity(intent);
                                    }
                                } else {
                                    // User not found, show error message
                                    Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Handle the error
                                Log.e("LoginError", "Error", task.getException());
                                if (task.getException() != null) {
                                    Log.e("LoginErrorDetails", "Exception: " + task.getException().getMessage());
                                }
                                Toast.makeText(Login.this, "Error  user", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                } else {
                    Toast.makeText(Login.this, "Username or Password cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



}
