package com.nipusan.app.filtergenerator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nipusan.app.filtergenerator.databinding.ActivityMainBinding;
import com.nipusan.app.filtergenerator.utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements Constants {


    private static final String TAG_FIREBASE = "FIREBASE";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private TextView email;
    private ImageView imageProfile;
    private FirebaseFirestore db;
    private SharedPreferences preferences;

    FirebaseAuth fAuth;
    FirebaseAuth.AuthStateListener listener;
    FirebaseUser user;

    public static final int REQUEST_CODE = 34755;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build()
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_collections, R.id.nav_blocks, R.id.nav_fields)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View hView = navigationView.getHeaderView(0);
        email = (TextView) hView.findViewById(R.id.tvEmailUser);
        imageProfile = (ImageView) hView.findViewById(R.id.ivProfileUser);

        preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        fAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        listener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                Log.println(Log.DEBUG, "onAuthStateChanged", "start method");
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(getApplicationContext(), "Welcome " + user.getDisplayName() + "!", Toast.LENGTH_SHORT).show();
                    try {
                        email.setText(user.getEmail());
                        Log.i("url", user.getPhotoUrl().toString());
                        Glide.with(getApplicationContext()).load(user.getPhotoUrl().toString()).into(imageProfile);
                        preferences.edit().putString(USER_UID, user.getUid()).apply();
                        findCollection(user.getUid());
                    } catch (Exception e) {
                        Log.println(Log.ERROR, "Exception", e.getMessage());
                    }
                } else {
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build(), REQUEST_CODE
                    );
                }
            }
        };
    }

    private void findCollection(String userId) {
        db.collection("collection")
                .whereEqualTo("owner_user", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG_FIREBASE, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG_FIREBASE, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        fAuth.removeAuthStateListener(listener);
        Log.println(Log.DEBUG, "onPause", "start method");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        fAuth.addAuthStateListener(listener);
        Log.println(Log.DEBUG, "onResume", "start method");

        try {
            preferences = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
            String collectionActive = preferences.getString(COLLECTION_UID, "");
            Log.println(Log.INFO, TAG_FIREBASE, "collectionActive:" + collectionActive);
        } catch (Exception e) {
            Log.e(TAG_EXCEPTION, e.getMessage());
        }
    }

    public void logout(MenuItem item) {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                preferences.edit().putStringSet(COLLECTION_UID, null).apply();
                preferences.edit().putStringSet(COLLECTION_NAME, null).apply();
                Toast.makeText(MainActivity.this, "The session has been closed!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        });
    }

}