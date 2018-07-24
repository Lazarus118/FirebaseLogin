package co.aulatech.firebaselogin;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.Map;


public class MainActivity extends AppCompatActivity  {
    DBHelper dbHelper;
    DatabaseReference database;
    TextView username;
    EditText number, location;
    public static String user_name;
    public static String NUMBER_FROM_FIREBASE;
    public static String LOCATION_FROM_FIREBASE;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    static boolean firebase_persistence_enable = false;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // CREATE DEFAULT USER IN INTERNAL DB
        ////////////////////////////////////////////////////////////////
        dbHelper = new DBHelper(getApplicationContext());
        dbHelper.insert("Default");

        // AUTHENTICATION
        ////////////////////////////////////////////////////////////////
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        // FIREBASE CACHE
        ////////////////////////////////////////////////////////////////
        if (!firebase_persistence_enable) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            firebase_persistence_enable = true;
        }

        // INIT
        ///////////////////////////////////////////////////////////////////
        username = findViewById(R.id.username);
        number = findViewById(R.id.number);
        location = findViewById(R.id.location);

        // GET DATA FROM FIREBASE
        ////////////////////////////////////////////////////////////////
        database = FirebaseDatabase.getInstance().getReference();
        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                NUMBER_FROM_FIREBASE = (String) dataSnapshot.child(getUserFromDB()).child("personal").child(getUserFromDB()).child("number").getValue();
                LOCATION_FROM_FIREBASE = (String) dataSnapshot.child(getUserFromDB()).child("personal").child(getUserFromDB()).child("location").getValue();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        username.setText(getUserFromDB());
        number.setText(NUMBER_FROM_FIREBASE);
        location.setText(LOCATION_FROM_FIREBASE);

        // UPDATE DATA FROM FIREBASE
        ///////////////////////////////////////////////////////////////////
        final Map<String, String> data = new HashMap<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference new_user_record = database.getReference("users");

        number.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                data.put("number", number.getText().toString());
                data.put("location", LOCATION_FROM_FIREBASE);
                new_user_record.child(getUserFromDB()).child("personal").child(getUserFromDB()).setValue(data);
                finish();
                startActivity(getIntent());
                return true;
            }
        });
        location.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                data.put("number",NUMBER_FROM_FIREBASE);
                data.put("location", location.getText().toString());
                new_user_record.child(getUserFromDB()).child("personal").child(getUserFromDB()).setValue(data);
                finish();
                startActivity(getIntent());
                return true;
            }
        });


    }

    /**********************************************************************************
     * RETRIEVE USERNAME FROM INTERNAL DB
     *********************************************************************************/
    public String getUserFromDB() {
        dbHelper = new DBHelper(this);
        final Cursor cursor = dbHelper.getRecord(1);
        if (cursor.moveToFirst()) {
            user_name = cursor.getString(1);
            cursor.close();
        }
        return user_name;
    }

    /**********************************************************************************
     * SIGN OUT LOGIC
     *********************************************************************************/
    public void signOut() {
        auth.signOut();
        finish();
        Toast.makeText(getApplicationContext(), "You're now logged out", Toast.LENGTH_LONG).show();
    }

    /**********************************************************************************
     * TOP NAVIGATION INIT & LOGIC
     *********************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.log_out:
                    // do action
                    //////////////////////
                    signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**********************************************************************************
     * NECESSARY FOR AUTHENTICATION SESSIONS
     *********************************************************************************/
    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
