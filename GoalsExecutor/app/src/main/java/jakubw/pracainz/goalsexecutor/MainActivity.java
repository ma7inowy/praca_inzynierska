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
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jakubw.pracainz.goalsexecutor.Model.CalendarEvent;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private TextView userEmail;
    private CircleImageView userPhoto;
    private TextView nickname;
    private GoogleSignInClient mGoogleSignInClient;

    final int callbackId = 42;

    //google signin
    GoogleSignInAccount signInAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        // do wylogowaina
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout); // to te 3 kreseczki
        // wciskanie navigation views
        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        userEmail = headerView.findViewById(R.id.userEmail);
        userEmail.setText(signInAccount.getEmail());

        userPhoto = headerView.findViewById(R.id.userPhoto);
        userPhoto.getLayoutParams().width = 150;
        userPhoto.getLayoutParams().height = 150;
        Glide.with(headerView).load(signInAccount.getPhotoUrl()).into(userPhoto);

        nickname = headerView.findViewById(R.id.nickname);
        nickname.setText(signInAccount.getDisplayName());

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BoxFragment()).commit();


//        if(signInAccount!= null){
//            userEmail.setText(signInAccount.getEmail());
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NextActionFragment()).commit();
                break;
            case R.id.nav_kontener:
//                Intent intent5 = new Intent(MainActivity.this, BoxFragment.class);
//                startActivity(intent5);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BoxFragment()).commit();
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
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarFragment()).commit();

                }
                checkPermission(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
                break;
            case R.id.nav_labels:
                Intent intent3 = new Intent(MainActivity.this, LabelActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_help:
                Intent intent4 = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent4);
                break;
            case R.id.nav_projects:
//                Intent intent4 = new Intent(MainActivity.this, ProjectsFragment.class);
//                startActivity(intent4);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProjectsFragment()).commit();
                break;
            case R.id.nav_groups:
//                Intent intent5 = new Intent(MainActivity.this, GroupsFragment.class);
//                startActivity(intent5);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GroupsFragment()).commit();
                break;
            case R.id.nav_someday:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SomedayFragment()).commit();
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
        GoogleCalendarReader.getEventsFromGoogleCalendar(this, signInAccount.getEmail());
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CalendarFragment()).commit();
            }
        }

    }

    private void handleEventsFromPhoneCalendars() {
        DatabaseReference reference;
        ArrayList<CalendarEvent> events = GoogleCalendarReader.getEventsFromGoogleCalendar(MainActivity.this, signInAccount.getEmail());
        if (!events.isEmpty()) {
            for (CalendarEvent event : events) {
                reference = FirebaseDatabase.getInstance().getReference().child("GoalsExecutor").child("Tasks").child("Calendar").child(signInAccount.getId()).child("Ca" + event.getId());
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
