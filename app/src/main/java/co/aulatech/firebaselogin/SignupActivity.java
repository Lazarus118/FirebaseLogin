package co.aulatech.firebaselogin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SignupActivity extends Activity {

    String name_to_db_table;
    EditText inputEmail, inputPassword, start_name;
    Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        start_name = (EditText) findViewById(R.id.enter_start_name);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get info from EditText
                // -----------------------------------------------
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                name_to_db_table = start_name.getText().toString();
                // Generate random ID
                Random rand = new Random();
                int num_generator = rand.nextInt(10000) + 1;
                final String name = name_to_db_table + "_" + num_generator;

                // FIELD VALIDATION
                // ------------------------------------------------------
                if (TextUtils.isEmpty(start_name.getText().toString())) {
                    start_name.setHintTextColor(Color.RED);
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    inputEmail.setHintTextColor(Color.RED);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    inputPassword.setHintTextColor(Color.RED);
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // INSERT DB TABLE
                                    // -----------------------------------------------
                                    dbHelper = new DBHelper(getApplicationContext());
                                    dbHelper.update(0, name);

                                    // Write a message to the get_database (firebase)
                                    // -----------------------------------------------
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference new_user_record = database.getReference("users");

                                    Map<String, String> data = new HashMap<>();
                                    data.put("name", name);

                                    Map<String, String> personal = new HashMap<>();
                                    personal.put("name", "");
                                    personal.put("number", "...");
                                    personal.put("location", "...");

                                    new_user_record.child(name).setValue(data);
                                    new_user_record.child(name).child("personal").child(name).setValue(personal);


                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
