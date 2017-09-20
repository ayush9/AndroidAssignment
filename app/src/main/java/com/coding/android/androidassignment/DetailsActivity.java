package com.coding.android.androidassignment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

public class DetailsActivity extends Activity {
    HashMap<String, String> hashmap;
    TextView description, text, date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        hashmap = (HashMap<String, String>) intent.getSerializableExtra("hashMap");
        description = (TextView)findViewById(R.id.description);
        description.setText(hashmap.get("description"));
        text = (TextView)findViewById(R.id.textView);
        text.setText(hashmap.get("text"));
        date = (TextView)findViewById(R.id.datetext);
        date.setText(hashmap.get("time"));

        ImageView image = (ImageView) findViewById(R.id.imageView1);
        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_perm_identity_grey_600_18dp));
        new DownloadImageTask(image).execute(hashmap.get("imageUrl"));
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }

    }
}