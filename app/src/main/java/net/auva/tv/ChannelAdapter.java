package net.auva.tv;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.player.exoplayer.PlayerActivity;
import com.squareup.picasso.Picasso;

import net.auva.tv.R;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by ankit on 27/10/17.
 */

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {

    private Context context;
    private List<Channel> list;

    public ChannelAdapter(Context context, List<Channel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Channel channel = list.get(position);

        holder.ChannelName.setText(channel.getCName());

        String logo =channel.getImage();

        Picasso.get()
                .load(logo)
                .resize(480, 360)
                .centerInside()
                .into(holder.Image);


        SharedPreferences prefs = context.getSharedPreferences("FavChannels", MODE_PRIVATE);
        Boolean restoredId = prefs.getBoolean(channel.getId(), false);
        if (restoredId) {
            holder.fav.setImageResource(R.drawable.ic_baseline_favorite_24px);
        }else {
            holder.fav.setImageResource(R.drawable.ic_baseline_favorite_border_24px);
        }
        holder.itemView.findViewById(R.id.channel_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String StreamName;
                String url;
//                System.out.println(channel.getStreamName());

//                if (!channel.getServer().equals("") && !channel.getHlsport().equals("") && !channel.getApp().equals("") && !channel.getStreamName().equals("")) {
//                    if (channel.getStreamName().endsWith("_sd")) {
//                        StreamName = channel.getStreamName().replace("_sd", "");
//                        url = "http://" + channel.getServer() + ":" + channel.getHlsport() + "/" + channel.getApp() + "/" + StreamName + ".m3u8";
//                    } else {
//                        url = "http://" + channel.getServer() + ":" + channel.getHlsport() + "/" + channel.getApp() + "/" + channel.getStreamName() + ".m3u8";
//                    }

//                    if (channel.getStreamName() != null) {
//                        Intent intent = new Intent(context, PlayerActivity.class);
//                        intent.putExtra("channel_url", channel.getStreamName());
//                        intent.putExtra("channel_name", channel.getCName());
//                        context.startActivity(intent);
//                    }
                if (channel.getStreamName() != null) {
                    Intent intent = new Intent(context, PlayerActivity.class);
                    intent.putExtra("channel_url", channel.getStreamName());
                    intent.putExtra("channel_url360", channel.getStreamName360());
                    intent.putExtra("channel_url480", channel.getStreamName480());
                    intent.putExtra("channel_url720", channel.getStreamName720());
                    intent.putExtra("channel_name", channel.getCName());
                    context.startActivity(intent);
//                    System.out.println(channel.getStreamName360());
//                    System.out.println("==========");
                }
//                }
//                else {
//                    Log.e("error :", "empty value");
//                }
            }
        });

        holder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = context.getSharedPreferences("FavChannels", MODE_PRIVATE);
                SharedPreferences.Editor editor = context.getSharedPreferences("FavChannels", MODE_PRIVATE).edit();
                Boolean restoredId = prefs.getBoolean(channel.getId(), false);
                if (restoredId) {
                    editor.putBoolean(channel.getId(), false);
                    editor.apply();
                    editor.commit();
                    holder.fav.setImageResource(R.drawable.ic_baseline_favorite_border_24px);
                    MainActivity.getThis().decrementFavoriteChannels();
                    if (context instanceof Favourite){
                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                        if (list.size()==0){
                            ((Favourite) context).finish();
                        }
                    }
//                        Toast.makeText(context, "Channel removed from favorite list", Toast.LENGTH_SHORT).show();
                }else{
                    editor.putBoolean(channel.getId(), true);
                    editor.apply();
                    editor.commit();
                    holder.fav.setImageResource(R.drawable.ic_baseline_favorite_24px);
                    MainActivity.getThis().incrementFavoriteChannels(true);
//                        Toast.makeText(context, "Channel added to favorite list", Toast.LENGTH_SHORT).show();
                }
                //db = new SQLiteDatabaseHandler(context);
                //boolean isAlredyFav =db.CheckIsDataAlreadyInDBorNot("21");
                //FavChannel channel1 = new FavChannel(channel.getId(), channel.getCName(), channel.getImage(), channel.getStreamName(), channel.getCat(), channel.getApp(), channel.getHlsport(), channel.getServer());
                //db.addChannel(channel1);
            }
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ChannelName;
        public ImageView Image;
        public ImageView fav;

        public ViewHolder(View itemView) {
            super(itemView);

            ChannelName = itemView.findViewById(R.id.channel_name);
            Image = itemView.findViewById(R.id.image_url);
            fav = itemView.findViewById(R.id.add_favourite);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }



}