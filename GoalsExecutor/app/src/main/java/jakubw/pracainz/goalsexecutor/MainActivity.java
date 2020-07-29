package jakubw.pracainz.goalsexecutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private TextView username;
    private ImageView userphoto;
    private TextView nickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        username = findViewById(R.id.username);

         //google signin
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

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
        switch (menuItem.getItemId()){
            case R.id.nav_message:
                Intent intent = new Intent(MainActivity.this, KontenerActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
