package com.mostanad.plus.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.mostanad.plus.R;
import com.mostanad.plus.adapter.MainFragmentAdapter;
import com.mostanad.plus.helper.RestCallBack;
import com.mostanad.plus.helper.RestHelper;
import com.mostanad.plus.pojo.HomePageModel;
import com.mostanad.plus.pojo.SingleCategoryModel;
import com.mostanad.plus.pojo.SingleVideoModel;
import com.mostanad.plus.utils.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    @BindView(R.id.pgbar)
    ProgressBar prbar;
    @BindView(R.id.txt_wait)
    TextView txtwait;
    @BindView(R.id.ll_wait)
    LinearLayout llwait;
    @BindView(R.id.rvHome)
    RecyclerView rvHome;
    Unbinder unbinder;
    @BindView(R.id.txt_empty)
    TextView txtEmpty;
    @BindView(R.id.swipeContainerNewest)
    SwipeRefreshLayout swipeContainerNewest;
    private OnFragmentInteractionListener mListener;

    private LinearLayoutManager layoutManager;
    private RestHelper restHelper;
    private MainFragmentAdapter adapter;
    private SharedPreferences personInfo;

    private ArrayList<SingleCategoryModel> categories = new ArrayList<>();
    private ArrayMap<String, LinkedList<SingleVideoModel>> homeCategoryData = new ArrayMap<>();
    private ArrayMap<String, LinkedList<SingleVideoModel>> homeData=new ArrayMap<>();

    public MainFragment() {
        // Required empty public constructor
    }


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        personInfo = getContext().getSharedPreferences(Constants.PERSON_INFO, MODE_PRIVATE);
        initViews();
        restHelper = new RestHelper();
        loadCategories();
        swipeContainerNewest.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                homeCategoryData.clear();
                homeData.clear();
                adapter.notifyDataSetChanged();
                llwait.setVisibility(View.VISIBLE);
                rvHome.setVisibility(View.GONE);
                txtEmpty.setVisibility(View.GONE);
                swipeContainerNewest.setRefreshing(false);
                loadData();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void initViews() {
        rvHome.setHasFixedSize(true);
        rvHome.setItemViewCacheSize(20);
        rvHome.setDrawingCacheEnabled(true);
        rvHome.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        layoutManager = new LinearLayoutManager(getContext());
        rvHome.setLayoutManager(layoutManager);
    }

    public void loadCategories() {
        restHelper.Categories(personInfo.getString(Constants.USER_ID, ""), personInfo.getString(Constants.PHONE_NUMBER, ""), new RestCallBack.ResponseCategoryFinishListener() {
                    @Override
                    public void onFinish(ArrayList<SingleCategoryModel> category) {
                        categories.addAll(category);
                        loadData();
                    }
                }
                , new RestCallBack.ResponseErrorListener() {
                    @Override
                    public void onError(String error) {
                        if (error != null)
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        llwait.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void loadData() {
        restHelper.Home(personInfo.getString(Constants.USER_ID, ""), personInfo.getString(Constants.PHONE_NUMBER, ""), new RestCallBack.ResponseHomeFinishListener() {
                    @Override
                    public void onFinish(HomePageModel home) {
                        initializeRecyclerView(home);
                        rvHome.setVisibility(View.VISIBLE);
                        llwait.setVisibility(View.GONE);
                    }
                }
                , new RestCallBack.ResponseErrorListener() {
                    @Override
                    public void onError(String error) {
                        if (error != null)
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        llwait.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void initializeRecyclerView(final HomePageModel home) {
        homeData = getData(home);

        for (String k : homeData.keySet()) {

            if (k.equals("featured") || k.equals("new"))
                homeCategoryData.put(k, homeData.get(k));
            String newName = k;
            newName = newName.replace("cat_", "");
            for (int i = 0; i < categories.size(); i++) {
                if (newName.equals(categories.get(i).getId())) {
                    homeCategoryData.put(categories.get(i).getName_fa(), homeData.get(k));
                }
            }
        }

        homeData.clear();

        adapter = new MainFragmentAdapter(getContext(), homeCategoryData);
        rvHome.setAdapter(adapter);
    }

    public ArrayMap<String, LinkedList<SingleVideoModel>> getData(HomePageModel home) {
        ArrayMap<String, LinkedList<SingleVideoModel>> dataObject = new ArrayMap<>();
        JsonObject data = home.getData();

        for (Iterator<Map.Entry<String, JsonElement>> entries = data.entrySet().iterator(); entries.hasNext(); ) {
            Map.Entry<String, JsonElement> entry = entries.next();
            String key = entry.getKey();
            LinkedList<SingleVideoModel> fields;
            fields = new Gson().fromJson(entry.getValue(), new TypeToken<LinkedList<SingleVideoModel>>() {
            }.getType());
            dataObject.put(key, fields);
        }
        return dataObject;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void goToPage(int i);
    }
}
