package net.auva.tv;

import static net.auva.tv.Config.API_URL;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.auva.tv.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Favourite extends AppCompatActivity {

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Channel> movieList=new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView mList;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        mThis = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mList = findViewById(R.id.main_fav_list);

        movieList = new ArrayList<>();
        adapter = new ChannelAdapter(this, movieList);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mList.setHasFixedSize(true);
        mList.setLayoutManager(linearLayoutManager);
        RecyclerViewMargin decoration = new RecyclerViewMargin(24, 1);
        mList.addItemDecoration(decoration);
        getData();

    }

    /*  private void getData() {
          mList.setAdapter(adapter);
          List<FavChannel> channels = db.allChannel();
          for (int i=0;i<channels.size();i++){
              mList.setAdapter(adapter);
              Channel channel = new Channel();
              channel.setCName(channels.get(i).getCName());
              channel.setImage(channels.get(i).getImage());
              movieList.add(channel);
          }
          adapter.notifyDataSetChanged();
      }
      */
//  private String url = "http://192.168.200.1:3006/api/category";
    private void getData() {

        mList.setAdapter(adapter);

        StringRequest stringRequest = new StringRequest(API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.get("success").toString()=="true"){
                        JSONArray jsonArray=jsonObject.getJSONObject("details").getJSONArray("docs");
//                        String name=jsonObject.names().get(i).toString();
//                        JSONArray jsonArray = jsonObject.getJSONArray(name);
                        for (int j = 0; j < jsonArray.length(); j++) {
//                            Channel channel = new Channel();
                            JSONObject jo = jsonArray.getJSONObject(j);
                            JSONArray channels2 =jo.getJSONArray("Channels");

                            for (int jj = 0; jj < channels2.length(); jj++) {
                                Channel channel = new Channel();
                                JSONObject joo = channels2.getJSONObject(jj);
                                channel.setId(joo.getString("id"));
                                channel.setCName(joo.getString("channelName"));
                                channel.setImage(joo.getString("image"));
                                channel.setStreamName(joo.getString("streamName"));
//                                channel.setCat(joo.getString("cat"));
                                channel.setStreamName360(joo.getString("streamName360"));
                                channel.setStreamName480(joo.getString("streamName480"));
                                channel.setStreamName720(joo.getString("streamName720"));
//                                Log.println(Log.INFO,this,channel.getCName().toString())
//                                   System.out.println("===================================");
//                                   System.out.println(channel.getCName().toString());



                                SharedPreferences prefs = getSharedPreferences("FavChannels", MODE_PRIVATE);
                                Boolean restoredId = prefs.getBoolean(channel.getId(), false);
                                if (restoredId ==true) {
                                    movieList.add(channel);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Anything you want
                Log.e("error","error :" .concat(error.getMessage()));
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    class RecyclerViewMargin extends RecyclerView.ItemDecoration {
        private final int columns;
        private int margin;

        /**
         * constructor
         *
         * @param margin  desirable margin size in px between the views in the recyclerView
         * @param columns number of columns of the RecyclerView
         */
        RecyclerViewMargin(@IntRange(from = 0) int margin, @IntRange(from = 0) int columns) {
            this.margin = margin;
            this.columns = columns;

        }

        /**
         * Set different margins for the items inside the recyclerView: no top margin for the first row
         * and no left margin for the first column.
         */
        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {

            int position = parent.getChildLayoutPosition(view);
            //set right margin to all
            outRect.right = margin;
            //set bottom margin to all
            outRect.bottom = margin;
            //we only add top margin to the first row
            if (position < columns) {
                outRect.top = margin;
            }
            //add left margin only to the first column
            if (position % columns == 0) {
                outRect.left = margin;
            }
        }
    }

    private static Favourite mThis = null;
    public static Favourite getThis()
    {
        return mThis;
    }
}
