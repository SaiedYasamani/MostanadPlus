package com.mostanad.plus.fragment;

import android.content.Context;
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

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import com.mostanad.plus.R;
import com.mostanad.plus.adapter.NewVideosAdapter;
import com.mostanad.plus.helper.EndlessRecyclerViewScrollListener;
import com.mostanad.plus.helper.RestCallBack;
import com.mostanad.plus.helper.RestHelper;
import com.mostanad.plus.pojo.SingleVideoModel;
import com.mostanad.plus.pojo.VideosModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewestVideosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewestVideosFragment extends Fragment {

    @BindView(R.id.txt_empty)
    TextView txtEmpty;
    @BindView(R.id.rvNewest)
    RecyclerView rvNewest;
    @BindView(R.id.pgbar_load_more)
    SmoothProgressBar progressbar;
    Unbinder unbinder;
    @BindView(R.id.swipeContainerNewest)
    SwipeRefreshLayout swipeContainerNewest;
    @BindView(R.id.pgbar)
    ProgressBar prbar;
    @BindView(R.id.txt_wait)
    TextView txtwait;
    @BindView(R.id.ll_wait)
    LinearLayout llwait;
    private OnFragmentInteractionListener mListener;

    private RestHelper restHelper;
    public NewVideosAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private GridLayoutManager layoutManager;

    private LinkedList<SingleVideoModel> videoList = new LinkedList<>();

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    public NewestVideosFragment() {
        // Required empty public constructor
    }


    public static NewestVideosFragment newInstance(String param1) {
        NewestVideosFragment fragment = new NewestVideosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_newest_videos, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViews();
        restHelper = new RestHelper();
        loadData();
        swipeContainerNewest.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                videoList.clear();
                adapter.notifyDataSetChanged();
                llwait.setVisibility(View.VISIBLE);
                txtEmpty.setVisibility(View.GONE);
                swipeContainerNewest.setRefreshing(false);
                loadData();

            }
        });
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                progressbar.setVisibility(View.VISIBLE);
                restHelper.NewestVideo(mParam1, page + 1, 20, new RestCallBack.ResponseSearchFinishListener() {
                    @Override
                    public void onFinish(VideosModel singleClips) {
                        if (singleClips != null) {
                            videoList.addAll(singleClips.getData());
                            adapter.notifyDataSetChanged();
                        }
                        progressbar.setVisibility(View.GONE);
                    }
                }, new RestCallBack.ResponseErrorListener() {
                    @Override
                    public void onError(String error) {
                        if (error != null)
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        progressbar.setVisibility(View.GONE);
                    }
                });
            }
        };
        rvNewest.addOnScrollListener(scrollListener);
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
        rvNewest.setHasFixedSize(true);
        rvNewest.setItemViewCacheSize(20);
        rvNewest.setDrawingCacheEnabled(true);
        rvNewest.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        layoutManager = new GridLayoutManager(getContext(), 2);
        rvNewest.setLayoutManager(layoutManager);
    }

    public void loadData() {
        restHelper.NewestVideo(mParam1, 1, 20, new RestCallBack.ResponseSearchFinishListener() {
                    @Override
                    public void onFinish(VideosModel singleClips) {
                        initializeRecyclerView(singleClips);
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

    private void initializeRecyclerView(final VideosModel singleClips) {
        videoList.addAll(singleClips.getData());
        adapter = new NewVideosAdapter(videoList, getContext());
        rvNewest.setAdapter(adapter);


    }
}
