package com.example.xspace;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.zip.Inflater;


public class Login extends AppCompatActivity {

    LoginDB LDB;
    private Button SignUp;
    private TextView fadingmessage;

    protected void onDestroy(){
        super.onDestroy();
        LDB.close();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginview);

        LDB = new LoginDB(this);
        SignUp = findViewById(R.id.NewUser);

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

                if (!LDB.checkEmailExists(EmailS)) {
                    LDB.insertData(UsernameS, PasswordS, EmailS);
                    dialog.dismiss();
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

    private void LoginButton(){
        Button Login = findViewById(R.id.LoginB);
        View LoginInflate = getLayoutInflater().inflate(R.layout.loginview, null);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText Username = LoginInflate.findViewById(R.id.Username);
                EditText Password = LoginInflate.findViewById(R.id.Password);


                String UsernameS = Username.getText().toString();
                String PasswordS = Password.getText().toString();


                if(LDB.validateUser(UsernameS, PasswordS)) {

                    Intent intent = new Intent(Login.this, HomePage.class);
                    startActivity(intent);
                }

            }
        });
    }
}
