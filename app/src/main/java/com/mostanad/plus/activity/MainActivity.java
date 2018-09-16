package com.mostanad.plus.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.mostanad.plus.R;
import com.mostanad.plus.adapter.MainHomeViewPagerAdapter;
import com.mostanad.plus.fragment.CategoriesFragment;
import com.mostanad.plus.fragment.MainFragment;
import com.mostanad.plus.fragment.NewestVideosFragment;
import com.mostanad.plus.helper.CustomTypefaceSpan;
import com.mostanad.plus.helper.RestCallBack;
import com.mostanad.plus.helper.RestHelper;
import com.mostanad.plus.pojo.UserModel;
import com.mostanad.plus.utils.Constants;
import com.mostanad.plus.view.CustomViewPager;
import ir.oveissi.materialsearchview.MaterialSearchView;

import static com.mostanad.plus.utils.Constants.APP_NAME_FA;
import static com.mostanad.plus.utils.Constants.USER_NAME;

public class MainActivity extends AppCompatActivity implements
        MainFragment.OnFragmentInteractionListener
        , CategoriesFragment.OnFragmentInteractionListener
        , NewestVideosFragment.OnFragmentInteractionListener {

    @BindView(R.id.txt_category_title)
    TextView txtCategoryTitle;
    @BindView(R.id.img_app_icon)
    ImageView imgAppIcon;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.toolbar_container)
    FrameLayout toolbarContainer;
    @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    @BindView(R.id.pager)
    CustomViewPager pager;
    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private Typeface typeface;
    private TextView userName, phoneNumber;

    private MainHomeViewPagerAdapter adapter;

    private SharedPreferences personInfo;
    private SharedPreferences.Editor editor;
    private String mobileNumber;

    private RestHelper restHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        restHelper = new RestHelper();
        personInfo = getApplicationContext().getSharedPreferences(Constants.PERSON_INFO, MODE_PRIVATE);
        editor = personInfo.edit();

        initViews();
        initializeViewPager();

        searchView = findViewById(R.id.search_view);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        mobileNumber = personInfo.getString(Constants.PHONE_NUMBER, "");

        if (!personInfo.getBoolean(Constants.IS_SIGN_IN, false))
            signIn();

        initNavigationDrawer();

        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);
        searchView.setHintTextColor(R.color.material_drawer_hint_text);
        searchView.setHint("جستجو ...");
        searchView.bringToFront();

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
                searchView.closeSearch();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                searchView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchViewClosed() {

            }
        });

        pager.setCurrentItem(adapter.getCount() - 1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bottomBar.onSaveInstanceState();
    }

    public void initViews() {
        typeface = Typeface.createFromAsset(getAssets(), "fonts/shabnam.ttf");
        pager = findViewById(R.id.pager);
        bottomBar = findViewById(R.id.bottomBar);
        txtCategoryTitle = findViewById(R.id.txt_category_title);
        txtCategoryTitle.setText("خانه");
    }

    private void initializeViewPager() {
        adapter = new MainHomeViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(4);
        pager.setPagingEnabled(false);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home:
                        pager.setCurrentItem(3);
                        txtCategoryTitle.setText(getString(R.string.home));
                        break;
                    case R.id.tab_categories:
                        pager.setCurrentItem(2);
                        txtCategoryTitle.setText(getString(R.string.categories));
                        break;
                    case R.id.tab_newest:
                        pager.setCurrentItem(1);
                        txtCategoryTitle.setText(getString(R.string.newest));
                        break;
                    case R.id.tab_mostview:
                        pager.setCurrentItem(0);
                        txtCategoryTitle.setText(getString(R.string.mostview));
                        break;
                }
            }
        });
    }

    private void signIn() {

        restHelper.SignIn(mobileNumber, new RestCallBack.ResponseUserFinishListener() {
            @Override
            public void onFinish(UserModel userResponse) {
                switch (userResponse.getCode()) {
                    case "200": {
                        editor.putString(Constants.USER_ID, userResponse.getData().getId());
                        editor.putString(USER_NAME, userResponse.getData().getFullname());
                        editor.putString(Constants.USER_EMAIL, userResponse.getData().getEmail());
                        editor.putString(Constants.PASSWORD, userResponse.getData().getActivation_code());
                        editor.putBoolean(Constants.IS_SIGN_IN, true);
                        editor.apply();
                        String user_name = personInfo.getString(USER_NAME, "");
                        if (user_name.equals("")) {
                            editor.putString(USER_NAME, "کاربر ناشناس");
                            editor.apply();
                        }
                        break;
                    }
                }
            }
        }, new RestCallBack.ResponseErrorListener() {
            @Override
            public void onError(String error) {
                switch (error) {
                    case "404":
                        editor.putString(Constants.USER_ID, "").apply();
                        break;
                    case "400":
                        editor.putString(Constants.USER_ID, "").apply();
                        break;
                    case "406":
                        editor.putString(Constants.USER_ID, "").apply();
                        break;
                }
            }
        });
    }

    private void applyFontToMenuItem(MenuItem mi) {

        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", typeface), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public void initNavigationDrawer() {

        NavigationView navigationView = findViewById(R.id.navigation_view);

        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            applyFontToMenuItem(mi);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.profile: {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                    }
                    break;
                    case R.id.download: {
                        Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case R.id.share:
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = getString(R.string.shareapp);
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, APP_NAME_FA);
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "ارسال توسط"));
                        break;
                    case R.id.about:
                        Intent iii = new Intent(MainActivity.this, InfoActivity.class);
                        iii.putExtra("type", "about");
                        startActivity(iii);
                        break;
                    case R.id.rules:
                        Intent ii = new Intent(MainActivity.this, InfoActivity.class);
                        ii.putExtra("type", "terms");
                        startActivity(ii);
                        break;
                    case R.id.contact_us:
                        Intent iiii = new Intent(MainActivity.this, InfoActivity.class);
                        iiii.putExtra("type", "contact");
                        startActivity(iiii);
                        break;
                }
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
        userName = header.findViewById(R.id.txt_navigation_name);
        phoneNumber = header.findViewById(R.id.txt_navigation_number);

        mobileNumber = personInfo.getString(Constants.PHONE_NUMBER, "");
        String user_name = personInfo.getString(USER_NAME, "");
        if (user_name.equals(""))
            user_name = "کاربر ناشناس";

        userName.setText(user_name);
        phoneNumber.setText(mobileNumber);


        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
            }
        });

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {


        if (drawerLayout.isDrawerOpen(Gravity.RIGHT) || searchView.isSearchOpen()) {
            drawerLayout.closeDrawer(Gravity.RIGHT);
            searchView.closeSearch();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "برای خروج از برنامه دوباره کلیک کنید", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_download:
                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void goToPage(int i) {
        bottomBar.selectTabAtPosition(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String uname = personInfo.getString(USER_NAME, "");
        if (uname.equals(""))
            uname = "کاربر ناشناس";
        userName.setText(uname);
    }
}
