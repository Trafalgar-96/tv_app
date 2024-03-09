package net.auva.tv;

import static net.auva.tv.Config.API_URL;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.IntRange;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private int noOfFavoriteChannels = 0;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    private Menu mMenu = null;

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;
    final static int CHILD_LOADING = 0;
    final static int CHILD_ERROR = 1;
    final static int CHILD_OK = 2;
    ViewFlipper viewFlipper;

    ArrayList CatList = new ArrayList();
    List<Channel> channels = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Channel> movieList;
    //    private RecyclerView.Adapter adapter; //ADAPTER IS IN FRAGMENT
    TabLayout tabLayout;

    @Override
    protected void onStart() {
        super.onStart();
//        CheckForUpdatesService.checkForUpdate(this, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GetPhoneNumberHintIntentRequest request = GetPhoneNumberHintIntentRequest.builder().build();
        Identity.getSignInClient(this).getPhoneNumberHintIntent(request).addOnSuccessListener(pendingIntent -> {

        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        }
        mThis = this;
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        movieList = new ArrayList<>();
        mViewPager = findViewById(R.id.container);
        viewFlipper = findViewById(R.id.frag_view_flipper);
        Button btnRetry = findViewById(R.id.frag_btn_retry);
        btnRetry.setOnClickListener(v -> getData());

        tabLayout = findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getData();
    }

    private void getFavoriteList(List<Channel> movieList) {
        SharedPreferences prefs = this.getSharedPreferences("FavChannels", MODE_PRIVATE);
        for (Channel channel : movieList) {
            if (prefs.getBoolean(channel.getId(), false)) incrementFavoriteChannels(false);
        }
        updateMenuFavoriteButton();

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        int IndexNumber = CatList.size();
        adapter.notifyDataSetChanged();
        Category category = new Category(CatList, channels);
        Tab1Fragment[] listOfFrag = new Tab1Fragment[IndexNumber];
        if (IndexNumber > 0 && category.getChannels().size() > 0) {
            for (int i = 0; i < IndexNumber; i++) {
                listOfFrag[i] = new Tab1Fragment();
                listOfFrag[i].setListOfChannels(category, CatList.get(i).toString());
                adapter.addFragment(listOfFrag[i], CatList.get(i).toString());
                viewPager.setAdapter(adapter);
            }
        }

    }

    public void getData() {

        viewFlipper.setDisplayedChild(CHILD_LOADING);

        StringRequest stringRequest = new StringRequest(API_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.get("success").toString() == "true") {
                        JSONArray jsonArray = jsonObject.getJSONObject("details").getJSONArray("docs");

                        for (int j = 0; j < jsonArray.length(); j++) {

                            JSONObject jo = jsonArray.getJSONObject(j);

                            CatList.add(jo.getString("categoryName"));
                            JSONArray channels2 = jo.getJSONArray("Channels");

                            for (int jj = 0; jj < channels2.length(); jj++) {
                                Channel channel = new Channel();
                                JSONObject joo = channels2.getJSONObject(jj);
                                channel.setId(joo.getString("id"));
                                channel.setCName(joo.getString("channelName"));
                                channel.setImage(joo.getString("image"));
                                channel.setStreamName(joo.getString("streamName"));
                                channel.setCat(jo.getString("categoryName"));
                                channel.setStreamName360(joo.getString("streamName360"));
                                channel.setStreamName480(joo.getString("streamName480"));
                                channel.setStreamName720(joo.getString("streamName720"));
                                movieList.add(channel);
                                channels.add(channel);

                            }
                        }
                        setupViewPager(mViewPager);
                        tabLayout.setupWithViewPager(mViewPager);
                        getFavoriteList(movieList);
                        viewFlipper.setDisplayedChild(CHILD_OK);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    viewFlipper.setDisplayedChild(CHILD_ERROR);
                }
            }
        }, error -> {
            // Anything you want
            Log.e("error", "error :".concat(error.getMessage()));
            viewFlipper.setDisplayedChild(CHILD_ERROR);
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        adapter.notifyDataSetChanged(); //ADAPTER IS IN FRAGMENT
        updateMenuFavoriteButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                Intent intent1 = new Intent(MainActivity.this, Favourite.class);
                startActivity(intent1);
                return true;

            case R.id.about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                View aboutDialog = inflater.inflate(R.layout.dialog_about, null);
                builder.setView(aboutDialog);
                final AlertDialog dialog = builder.create();
                dialog.show();
                TextView versionName = aboutDialog.findViewById(R.id.version_name);
                versionName.setText("Version " + BuildConfig.VERSION_NAME);
                RelativeLayout button = aboutDialog.findViewById(R.id.about_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.tv_url)));
                        startActivity(browserIntent);
                    }
                });
//                button.setEnabled(false);
                RelativeLayout cardView = aboutDialog.findViewById(R.id.about_card);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Window window = dialog.getWindow();
                window.setBackgroundDrawableResource(android.R.color.transparent);
                return true;
//            case R.id.action_cast:
//                screenCast();
//                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
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
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

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

    private static MainActivity mThis = null;

    public Menu getMenu() {
        return mMenu;
    }

    public static MainActivity getThis() {
        return mThis;
    }

    public void incrementFavoriteChannels(boolean doUpdate) {
        noOfFavoriteChannels++;
        if (doUpdate) updateMenuFavoriteButton();
    }

    public void decrementFavoriteChannels() {
        noOfFavoriteChannels--;
        updateMenuFavoriteButton();
    }

    private void updateMenuFavoriteButton() {
        Menu menu = this.getMenu();
        if (menu != null) menu.findItem(R.id.action_favorite).setVisible(noOfFavoriteChannels > 0);
    }

//    public void startServiceViaWorker() {
//        Log.d(TAG, "startServiceViaWorker called");
//        String UNIQUE_WORK_NAME = "StartMyServiceViaWorker";
//        WorkManager workManager = WorkManager.getInstance(this);
//        // As per Documentation: The minimum repeat interval that can be defined is 15 minutes
//        // (same as the JobScheduler API), but in practice 15 doesn't work. Using 16 here
//        PeriodicWorkRequest request =
//                new PeriodicWorkRequest.Builder(
//                        TvWorker.class,
//                        16,
//                        TimeUnit.MINUTES)
//                        .build();
//        // to schedule a unique work, no matter how many times app is opened i.e. startServiceViaWorker gets called
//        // do check for AutoStart permission
//        workManager.enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
//    }

    private void optimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
    }

    private void startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean granted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
            if (!granted)
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 202);
            else optimization();
        } else optimization();
    }

}

