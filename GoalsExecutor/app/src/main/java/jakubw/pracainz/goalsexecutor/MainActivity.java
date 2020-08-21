package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private TextView username;
    private CircleImageView userphoto;
    private TextView nickname;
    private GoogleSignInClient mGoogleSignInClient;

    final int callbackId = 42;

    //google signin
    GoogleSignInAccount signInAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        username = findViewById(R.id.username);
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        // do wylogowaina
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout); // to te 3 kreseczki
        // wciskanie navigation views
        NavigationView navigationView = findViewById(R.id.nav_view);
        //test
        View headerView = navigationView.getHeaderView(0);
        username = headerView.findViewById(R.id.username);
        username.setText(signInAccount.getEmail());

        userphoto = headerView.findViewById(R.id.userphoto);
        userphoto.getLayoutParams().width = 150;
        userphoto.getLayoutParams().height = 150;
        Glide.with(headerView).load(signInAccount.getPhotoUrl()).into(userphoto);

        nickname = headerView.findViewById(R.id.nickname);
        nickname.setText(signInAccount.getDisplayName());
        //
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

//        if(signInAccount!= null){
//            username.setText(signInAccount.getEmail());
//        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_next_action:
                Intent intent = new Intent(MainActivity.this, KontenerActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_kontener:
                break;
            case R.id.nav_logout:
                signOut();
                break;
            case R.id.nav_calendar:
                String requiredPermission = android.Manifest.permission.READ_CALENDAR;
                String requiredPermission2 = android.Manifest.permission.WRITE_CALENDAR;
                int checkValRead = this.checkCallingOrSelfPermission(requiredPermission);
                int checkValWrite = this.checkCallingOrSelfPermission(requiredPermission2);
                //jesli permisson zaakceptowane to cyk wlacz kalendarz i NIE wczytaj dane z kalendarza z tele
                if (checkValRead == PackageManager.PERMISSION_GRANTED && checkValWrite == PackageManager.PERMISSION_GRANTED) {
                    Intent intent2 = new Intent(MainActivity.this, CalendarActivity.class);
                    startActivity(intent2);
                }
                checkPermission(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
                break;
            case R.id.nav_profile:
                Intent intent3 = new Intent(MainActivity.this, LabelsActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_settings:
                checkPermission(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
                break;
        }
        return true;
    }

    private void checkPermission(int callbackId, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED;
        }

        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
    }

    private void showCalendarsData() {
        ReadCalendar.readCalendar(this);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Successfully signed out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
//        FirebaseAuth.getInstance().signOut();
//        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
//        startActivity(intent);
//        finish();

    }
    //jesli bede robil "permission check denied albo allow to wtedy sie wywoluje WIEC JESTLI ZAAKCEPTUJE TO WLACZ
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Integer> list = new ArrayList<>(grantResults.length);

        for (int i : grantResults) {
            list.add(Integer.valueOf(i));
        }

        if (callbackId == requestCode) {
            if (!list.contains(PackageManager.PERMISSION_DENIED)) {
                //all permissions have been granted
                handleEventsFromPhoneCalendars();
                Intent intent2 = new Intent(MainActivity.this, CalendarActivity.class);
                startActivity(intent2);

            }
        }

    }

    private void handleEventsFromPhoneCalendars() {
        DatabaseReference reference;
        ArrayList<CalendarEvent> events = ReadCalendar.readCalendar(MainActivity.this);
        if (!events.isEmpty()) {
            for (CalendarEvent event : events) {
                reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Calendar").child(signInAccount.getId()).child("Does" + event.getId());
                HashMap map = new HashMap();
                map.put("title", event.getTitle());
                map.put("year", event.getYear());
                map.put("month", event.getMonth());
                map.put("day", event.getDay());
                map.put("hour", event.getHour());
                map.put("minute", 0);
                map.put("description", "desc");
                map.put("id", event.getId());
                reference.updateChildren(map);
            }
            Toast.makeText(MainActivity.this, "Events loaded!", Toast.LENGTH_SHORT).show();
            finish();
        } else
            Toast.makeText(MainActivity.this, "No events to load!", Toast.LENGTH_SHORT).show();
    }
}
