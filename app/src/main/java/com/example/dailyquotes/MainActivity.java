package com.example.dailyquotes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView quoteTextView;
    ImageView likeUnlike;
    ImageView share;
    TextView refresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quoteTextView = findViewById(R.id.quoteView);
        share = findViewById(R.id.btn_shere);
        refresh = findViewById(R.id.clickme);
        likeUnlike = findViewById(R.id.btn_like);


        new FetchQuoteTask().execute();


        likeUnlike.setOnClickListener(new View.OnClickListener() {
            boolean isLiked = false;

            @Override
            public void onClick(View view) {
                if (!isLiked) {
                    likeUnlike.setImageResource(R.drawable.ic_baseline_favorite_24);

                    String textToPass = quoteTextView.getText().toString();
                    Intent intent = new Intent(MainActivity.this, MyFavorite.class);
                    intent.putExtra("TEXT_KEY", textToPass);


                    isLiked = true;
                } else {
                    likeUnlike.setImageResource(R.drawable.ic_baseline_favoritenot_24);
                    Toast.makeText(MainActivity.this, "Remove from Favorite", Toast.LENGTH_SHORT).show();

                    isLiked = false;
                }

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareData(quoteTextView.getText().toString());
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FetchQuoteTask().execute();
            }
        });


    }

    // menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.exit) {
            Toast.makeText(this, "Exit", Toast.LENGTH_SHORT).show();
            finish();
        } else if (itemId == R.id.favorite_quote) {
            Toast.makeText(this, "My favroite", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, MyFavorite.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);

    }

    public void shareData(String quote) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Quotes of the day!");
        intent.putExtra(Intent.EXTRA_TEXT, quote);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private class FetchQuoteTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://api.quotable.io/random")
                    .get()
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("API Response", responseData);
                    JSONObject jsonObject = new JSONObject(responseData);
                    return jsonObject.getString("content");
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                Log.e("API Error", "Error fetching data from API", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                quoteTextView.setText(s);
//                saveToDatabase(s);
            }
        }
    }

}





