package com.mostanad.plus.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.mostanad.plus.R;
import com.mostanad.plus.adapter.CategoryAdapter;
import com.mostanad.plus.helper.RestCallBack;
import com.mostanad.plus.helper.RestHelper;
import com.mostanad.plus.pojo.SingleCategoryModel;

import static android.content.Context.MODE_PRIVATE;
import static com.mostanad.plus.utils.Constants.PERSON_INFO;
import static com.mostanad.plus.utils.Constants.PHONE_NUMBER;
import static com.mostanad.plus.utils.Constants.USER_ID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {

    @BindView(R.id.txt_empty)
    TextView txtEmpty;
    @BindView(R.id.rvCategory)
    RecyclerView rvCategory;
    Unbinder unbinder;
    @BindView(R.id.swipeContainerCategories)
    SwipeRefreshLayout swipeContainerCategories;
    @BindView(R.id.pgbar)
    ProgressBar prbar;
    @BindView(R.id.txt_wait)
    TextView txtwait;
    @BindView(R.id.ll_wait)
    LinearLayout llwait;
    private OnFragmentInteractionListener mListener;

    private RestHelper restHelper;
    public CategoryAdapter adapter;

    private ArrayList<SingleCategoryModel> categoryArrayList = new ArrayList<>();

    private SharedPreferences personInfo;

    public CategoriesFragment() {
        // Required empty public constructor
    }


    public static CategoriesFragment newInstance() {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        unbinder = ButterKnife.bind(this, view);
        personInfo = getContext().getSharedPreferences(PERSON_INFO, MODE_PRIVATE);
        initViews();
        restHelper = new RestHelper();
        loadData();
        swipeContainerCategories.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                categoryArrayList.clear();
                adapter.notifyDataSetChanged();
                llwait.setVisibility(View.VISIBLE);
                txtEmpty.setVisibility(View.GONE);
                swipeContainerCategories.setRefreshing(false);
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
        void onFragmentInteraction(Uri uri);
    }

    public void initViews() {
        rvCategory.setHasFixedSize(true);
        rvCategory.setItemViewCacheSize(20);
        rvCategory.setDrawingCacheEnabled(true);
        rvCategory.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        rvCategory.setLayoutManager(layoutManager);
    }

    public void loadData() {
        restHelper.Categories(personInfo.getString(USER_ID, ""), personInfo.getString(PHONE_NUMBER, ""), new RestCallBack.ResponseCategoryFinishListener() {
                    @Override
                    public void onFinish(ArrayList<SingleCategoryModel> categories) {
                        initializeRecyclerView(categories);
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

    private void initializeRecyclerView(final ArrayList<SingleCategoryModel> singleClips) {
        categoryArrayList.addAll(singleClips);
        adapter = new CategoryAdapter(categoryArrayList, getContext());
        rvCategory.setAdapter(adapter);
    }
}
