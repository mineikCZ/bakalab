package michaelbrabec.bakalab.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import michaelbrabec.bakalab.R;
import michaelbrabec.bakalab.fragments.MainScreenFragment;
import michaelbrabec.bakalab.fragments.UkolyFragment;
import michaelbrabec.bakalab.fragments.ZnamkyFragment;
import michaelbrabec.bakalab.utils.BakaTools;
import michaelbrabec.bakalab.utils.RozvrhFragment;
import michaelbrabec.bakalab.utils.SharedPrefHandler;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainScreenFragment.OnFragmentInteractionListener {

    Float defaultElevation;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BakaTools.getToken(this) == null) {
            startLogin();
        }

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        defaultElevation = getResources().getDimension(R.dimen.toolbar_elevation);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String loginJmeno = SharedPrefHandler.getString(this, "loginJmeno");

        TextView navJmeno = navigationView.getHeaderView(0).findViewById(R.id.loginJmeno);

        navJmeno.setText(loginJmeno);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new MainScreenFragment()).commit();


    }

    private void startLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (!navigationView.getMenu().findItem(id).isChecked()) {


            if (id == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new MainScreenFragment()).commit();

            } else if (id == R.id.nav_ukoly) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new UkolyFragment()).commit();

            } else if (id == R.id.nav_rozvrh) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new RozvrhFragment()).commit();

            } else if (id == R.id.nav_znamky) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new ZnamkyFragment()).commit();
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, PreferenceActivity.class));
                drawer.closeDrawer(GravityCompat.START);
                return true;
            } else if (id == R.id.nav_sign_out) {
                BakaTools.resetToken();
                getSharedPreferences("cz.michaelbrabec.fossbakalari", MODE_PRIVATE).edit().clear().apply();
                startLogin();
                return true;
            }

            if (id == R.id.nav_ukoly) {
                toolbar.setElevation(0);
            } else {
                toolbar.setElevation(defaultElevation);
            }

            setTitle(item.getTitle());
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}