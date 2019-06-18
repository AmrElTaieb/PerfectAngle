package iti.edge;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {

    Button login;
    Button register;
    private FirebaseAuth mAuth;
    static final String TAG = "mytag";
    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        login = findViewById(R.id.loginBtn);
        register = findViewById(R.id.registerBtn);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(RegistrationActivity.this,CameraActivity.class);
                startActivity(i);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if(email.getText().toString().trim()!=null &&password.getText().toString().trim()!=null ) {

                    if (email.getText().toString().trim().matches(emailPattern)) {


                        mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "createUserWithEmail:success");
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    Toast.makeText(RegistrationActivity.this, "Sign up is successfull.",
                                                            Toast.LENGTH_SHORT).show();
                                                    //   updateUI(user);
                                                } else {
                                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());

                                                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                                    Toast.makeText(RegistrationActivity.this, "Failed Registration: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                                    //  Toast.makeText(SignupActivity.this, "Authentication failed.",
                                                    //   Toast.LENGTH_SHORT).show();
                                                    //   updateUI(user);
                                                }

                                            }
                                        }
                                );
                    }
                    else {
                        Toast.makeText(RegistrationActivity.this, "Invalid Email" , Toast.LENGTH_SHORT).show();
                    }

                }



                else
                {
                    Toast.makeText(RegistrationActivity.this, "Enter Email and password.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}

