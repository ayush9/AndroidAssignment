package com.coding.android.androidassignment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.apache.http.HttpStatus;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainAdapter extends BaseAdapter {
    ArrayList<HashMap<String, String>> result;
    Context context;
    ImageView imdb;
    ArrayList<Bitmap> imageId= new ArrayList<Bitmap>();

    private static LayoutInflater inflater=null;

    public MainAdapter(MainActivity mainActivity, ArrayList<HashMap<String, String>> prgmNameList) {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=mainActivity;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i=0;i<300;i++)
        {
            imageId.add(null);
        }
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv,tv1,tv2;
        ImageView img;
        Button like, dislike;
    }
    Holder holder;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.program_list, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.tv1=(TextView) rowView.findViewById(R.id.textView2);
        holder.img=(ImageView) rowView.findViewById(R.id.imageView1);
//        holder.like = (Button)rowView.findViewById(R.id.like);
//        holder.dislike = (Button)rowView.findViewById(R.id.dislike);
//        holder.like.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.dislike.setVisibility(View.VISIBLE);
//                holder.like.setVisibility(View.GONE);
//            }
//        });
//        holder.dislike.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.dislike.setVisibility(View.GONE);
//                holder.like.setVisibility(View.VISIBLE);
//            }
//        });
        holder.tv.setText(result.get(position).get("title"));

        if((result.get(position).get("text")) != null)
        {
            holder.tv1.setText(result.get(position).get("text"));
        }else{
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(150, 150);
            holder.img.setLayoutParams(params1);
        }

        if (holder.img != null) {
            if(result.get(position).get("imageUrl") != null) {
                new ImageDownloaderTask(holder.img).execute(result.get(position).get("imageUrl"), Integer.toString(position));
            }
            else if(result.get(position).get("imageUrl").equals("")){
                holder.img.setVisibility(View.GONE);
            }
        }


        if(imageId.get(position)!=null)
        {
            holder.img.setImageBitmap(imageId.get(position));
        }

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent in = new Intent(context,
                        DetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ((Activity) context).overridePendingTransition(R.anim.right_left, R.anim.left_right);
                in.putExtra("hashMap", result.get(position));
                context.startActivity(in);
            }
        });
        rowView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub

                return false;
            }
        });
        return rowView;
    }

    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {

        int posi;
        private final WeakReference<ImageView> imageViewReference;

        public ImageDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            posi= Integer.parseInt(params[1]);
            return downloadBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        imageId.set(posi, bitmap);
                    } else {
                        Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.ic_perm_identity_grey_600_18dp);
                        imageView.setImageDrawable(placeholder);
                    }
                }
            }
        }
        private Bitmap downloadBitmap(String url) {
            HttpURLConnection urlConnection = null;
            try {
                URL uri = new URL(url);
                urlConnection = (HttpURLConnection) uri.openConnection();
                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpStatus.SC_OK) {
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (Exception e) {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                Log.w("ImageDownloader", "Error downloading image from " + url);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }
}
