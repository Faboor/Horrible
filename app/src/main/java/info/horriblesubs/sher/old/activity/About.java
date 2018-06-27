package info.horriblesubs.sher.old.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import info.horriblesubs.sher.BuildConfig;
import info.horriblesubs.sher.R;
import info.horriblesubs.sher.util.DialogX;

@SuppressWarnings("all")
@SuppressLint("StaticFieldLeak")
public class About extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ProgressBar progressBar;
    private Context context;
    private Button button;
    private String link;
    private String clog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        SearchView searchView = findViewById(R.id.searchView);
        EditText editText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        editText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        editText.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
        editText.setGravity(Gravity.CENTER);
        editText.setTextSize((float) 14.5);
        searchView.setEnabled(false);
        editText.setEnabled(false);
        searchView.setQueryHint("About App");

        button = findViewById(R.id.button);
        button.setText(R.string.no_update_available);
        button.setEnabled(false);
        button.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("HorribleSubs").document("AppInfo")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        Log.e("ASA", documentSnapshot.getId());
                        if (documentSnapshot.exists()) {
                            long ver = documentSnapshot.getLong("version");
                            link = documentSnapshot.getString("link");
                            Log.e("ASA", ver + link);
                            if (ver <= BuildConfig.VERSION_CODE) {
                                button.setEnabled(false);
                                button.setText(R.string.no_update_available);
                            } else {
                                button.setText(R.string.update_available);
                                button.setEnabled(true);
                            }
                        }
                    }
                });
        firestore.collection("HorribleSubs").document("changelog")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        Log.e("ASA", documentSnapshot.getId());
                        if (documentSnapshot.exists()) {
                            clog = documentSnapshot.getString("" + BuildConfig.VERSION_CODE);
                        }
                    }
                });
        TextView textView = findViewById(R.id.textViewVersion);
        String s = "HorribleSubs .v" + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE +
                "), " + BuildConfig.APPLICATION_ID;
        textView.setText(s);

        findViewById(R.id.textViewChangelog).setOnClickListener(this);
        findViewById(R.id.imageViewDrawer).setOnClickListener(this);
        findViewById(R.id.textViewSource).setOnClickListener(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void onClickChangelog() {
        clog = clog.replace("/", "\n");
        clog = "v" + clog;
        final DialogX dialogX = new DialogX(this);
        dialogX.setTitle("Changelog").setDescription(clog).positiveButton("Close", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogX.dismiss();
            }
        }).setDescriptionGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        dialogX.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                ActivityCompat.requestPermissions(About.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECEIVE_BOOT_COMPLETED}, 4869);
            else
                ActivityCompat.requestPermissions(About.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED}, 4869);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewSource:
                break;

            case R.id.textViewChangelog:
                onClickChangelog();
                break;

            case R.id.imageViewDrawer:
                DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else
                    drawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.button:
                if (link == null)
                    break;
                if (link.isEmpty())
                    break;
                new Download().execute();
                break;
        }
    }

    class Download extends AsyncTask<Void, Integer, Boolean> {

        private File file;

        Download() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            button.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL url = new URL(link);
                URLConnection urlConnection = url.openConnection();
                String ESD = Environment.getExternalStorageDirectory()
                        .toString();
                File folder = new File(ESD, "Horrible Subs");
                if (folder.mkdir())
                    file = new File(folder, "Update.apk");
                else
                    file = new File(folder, "Update.apk");
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();

                byte[] buffer = new byte[1024];
                int len1;
                while ((len1 = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, len1);
                }
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            if (b) {
                progressBar.setVisibility(View.GONE);
                button.setEnabled(false);
                button.setText(R.string.no_update_available);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(context, "FileProvider", file);
                Log.e("UriX", uri.toString());
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, 0);
            } else {
                progressBar.setVisibility(View.GONE);
                button.setEnabled(true);
                button.setText(R.string.update_available);
                Toast.makeText(About.this, "Error Downloading App Update", Toast.LENGTH_SHORT).show();
            }
        }
    }
}