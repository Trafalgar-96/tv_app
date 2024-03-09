package net.auva.tv;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import net.auva.tv.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2/28/2017.
 */

public class Tab1Fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";
    Category cat;
    TabLayout tabLayout;
    String name;


    private RecyclerView mList;
    private DividerItemDecoration dividerItemDecoration;
    private List<Channel> movieList;
    private RecyclerView.Adapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(null == savedInstanceState) {
        movieList = new ArrayList<>();
        }

        adapter = new ChannelAdapter(getContext(), movieList);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);

        mList = view.findViewById(R.id.frag_main_list);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerViewMargin decoration = new RecyclerViewMargin(24, 1);
        mList.addItemDecoration(decoration);
        if(null == savedInstanceState) {
        getData();
        }
        return view;
    }

    public void setListOfChannels(Category category,String name) {
        this.name=name;
        this.cat=category;
//        Log.e("TAG :",name+"");
    }

    private void getData() {
        movieList.clear();
        mList.setAdapter(adapter);

        if (cat !=null){
            for (int i=0;i<cat.getChannels().size();i++){
//                Log.i("TAG :","movieList.toString()============");
//                Log.i("TAG :",cat.getChannels().get(i)+"");
                String CatName = cat.getChannels().get(i).getCat();
                if (CatName.equals(name)){
                    movieList.add(cat.getChannels().get(i));
                }
            }
            movieList.sort((o1, o2) -> o1.getImage().compareTo(o2.getImage()));
        }else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
        //Log.e("TAG :",movieList.get(0).toString());

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_favorite:
//                Intent intent1 = new Intent(getActivity(),Favourite.class);
//                startActivity(intent1);
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
}
