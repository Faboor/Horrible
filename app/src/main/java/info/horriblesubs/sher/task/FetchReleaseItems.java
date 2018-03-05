package info.horriblesubs.sher.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import info.horriblesubs.sher.BuildConfig;
import info.horriblesubs.sher.activity.Home;
import info.horriblesubs.sher.adapter.ReleaseRecycler;
import info.horriblesubs.sher.model.ReleaseItem;

@SuppressLint("StaticFieldLeak")
public class FetchReleaseItems extends AsyncTask<String, String, List<ReleaseItem>> {

    private Context context;
    private TextView textView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean b;
    private String[] strings = {"Initializing App...", "Connecting to Server...",
            "Fetching Latest Releases...", "Verifying Data...", "Parsing Received Data..."};
    private int i = 0;

    public FetchReleaseItems(Context context, TextView textView) {
        this.context = context;
        this.textView = textView;
        b = true;
    }

    public FetchReleaseItems(Context context, RecyclerView recyclerView,
                             SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.swipeRefreshLayout = swipeRefreshLayout;
        b = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        publishProgress(strings[i++]);
        if (!b)
            swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void onProgressUpdate(String... strings) {
        super.onProgressUpdate(strings);
        if (!b)
            return;
        String s = String.valueOf(textView.getText()).concat("\n").concat(strings[0]);
        textView.setText(s);
    }

    @Override
    protected List<ReleaseItem> doInBackground(String... strings) {
        publishProgress(this.strings[i++]);
        String s = BuildConfig.HAPI + strings[0];
        try {
            URL url = new URL(s);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            try {
                publishProgress(this.strings[i++]);
                httpURLConnection.connect();
                publishProgress(this.strings[i++]);
                BufferedReader bufferedReader = new BufferedReader(new
                        InputStreamReader(httpURLConnection.getInputStream()));
                return new Gson().fromJson(bufferedReader,
                        new TypeToken<List<ReleaseItem>>() {
                        }.getType());
            } finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<ReleaseItem> releaseItems) {
        super.onPostExecute(releaseItems);
        if (releaseItems != null)
            if (b) {
                publishProgress(strings[i++]);
                Intent intent = new Intent(context, Home.class);
                intent.putExtra("size", releaseItems.size());
                intent.putExtra("mode", "latest-all");
                int i = 0;
                for (ReleaseItem listItem : releaseItems) {
                    intent.putExtra("extra-" + i, listItem);
                    i++;
                }
                context.startActivity(intent);
                ((AppCompatActivity) context).finish();
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                recyclerView.setAdapter(new ReleaseRecycler(context, releaseItems));
                swipeRefreshLayout.setRefreshing(false);
            }
        else
            publishProgress("Error Fetching Data from Server\nTry Again Later...");
    }
}