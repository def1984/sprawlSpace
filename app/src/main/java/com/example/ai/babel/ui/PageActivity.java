package com.example.ai.babel.ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.SaveCallback;
import com.example.ai.babel.R;
import com.example.ai.babel.adapter.CollectionPageAdapter;
import com.example.ai.babel.ui.widget.MyFloatingActionButton;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class PageActivity extends BaseActivity {

    private Toolbar mToolbar;
    private MyFloatingActionButton fabBtn;
    private Boolean isCheck = false;
    private LinearLayout mLinearLayout;
    private CollectionPageAdapter mDemoCollectionPagerAdapter;
    private ViewPager mViewPager;
    private ArrayList<String> pgObIdList = new ArrayList<String>();
    AVQuery<AVObject> queryBook = AVQuery.getQuery("Book");
    AVObject BookObject;
    static public String bookObjectId;
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        new LoadPages().execute();
    }

    class LoadPages extends AsyncTask<Void, Integer, Boolean> {
        ProgressDialog progressDialog =new ProgressDialog(PageActivity.this);
        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        private List<AVObject> pageListAll;
        @Override
        protected Boolean doInBackground(Void... params) {
            AVQuery<AVObject> queryPage = AVQuery.getQuery("Page");
            queryPage.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);
            queryBook.setCachePolicy(AVQuery.CachePolicy.NETWORK_ONLY);

            try {
                queryPage.whereEqualTo("bookObjectId", queryBook.get(getIntent().getStringExtra("objectId")));
            } catch (AVException e) {
                e.printStackTrace();
            }

            queryPage.orderByAscending("createdAt");
            try {
                pageListAll=queryPage.find();
                for (int i = 0; i < pageListAll.size(); i++) {
                    pgObIdList.add(pageListAll.get(i).getObjectId());
                }
            } catch (AVException e) {
                e.printStackTrace();
                Toast.makeText(PageActivity.this, "连接超时:错误代码:"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            try {
                BookObject = queryBook.get(getIntent().getStringExtra("objectId"));
            } catch (AVException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setMessage("当前下载进度：" + values[0] + "%");
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            mDemoCollectionPagerAdapter = new CollectionPageAdapter(getSupportFragmentManager());
            mDemoCollectionPagerAdapter.setPageList(pageListAll);
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mDemoCollectionPagerAdapter);
            mViewPager.setCurrentItem(BookObject.getInt("pageIndex"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookObjectId=getIntent().getStringExtra("objectId");
        setContentView(R.layout.activity_page);
        intiView();
        fabBtnAm();
        addNewPost();
    }


    @Override
    protected void onPause() {
        super.onPause();
        BookObject.put("pageIndex", mViewPager.getCurrentItem());
        BookObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                return;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_search_into);
        searchItem.setIcon(android.support.v7.appcompat.R.drawable.abc_ic_search_api_mtrl_alpha);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_search_into:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.add_new_page:
                startActivity(new Intent(this, AddNewBook.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void intiView() {
        mToolbar = getActionBarToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showAllMinFab() {
        Animation minFabSet = AnimationUtils.loadAnimation(PageActivity.this, R.anim.min_fab_anim);
        mLinearLayout = (LinearLayout) findViewById(R.id.mini_fab_content);
        for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
            FloatingActionButton mini = (FloatingActionButton) mLinearLayout.getChildAt(i);
            mini.setVisibility(View.VISIBLE);
            mini.startAnimation(minFabSet);
        }
    }

    private void hideAllMinFab() {
        mLinearLayout = (LinearLayout) findViewById(R.id.mini_fab_content);
        Animation minFabSetRve = AnimationUtils.loadAnimation(PageActivity.this, R.anim.min_fab_anim_rev);
        for (int i = 0; i < mLinearLayout.getChildCount(); i++) {
            FloatingActionButton mini = (FloatingActionButton) mLinearLayout.getChildAt(i);
            minFabSetRve.setFillAfter(true);
            mini.startAnimation(minFabSetRve);
        }
    }


    private void fabBtnAm() {
        fabBtn = (MyFloatingActionButton) findViewById(R.id.fab);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animationSet = AnimationUtils.loadAnimation(PageActivity.this, R.anim.fab_anim);
                ImageView fabImageView = (ImageView) findViewById(R.id.img_fab);
                if (!isCheck) {
                    animationSet.setFillAfter(true);
                    showAllMinFab();
                    fabImageView.startAnimation(animationSet);
                    isCheck = true;
                } else {
                    animationSet.setInterpolator(new ReverseInterpolator());
                    fabImageView.startAnimation(animationSet);
                    hideAllMinFab();
                    isCheck = false;
                }
            }
        });
    }

    public class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float paramFloat) {
            return Math.abs(paramFloat - 1f);
        }
    }

    private void addNewPost() {
        FloatingActionButton addNewPost = (FloatingActionButton) findViewById(R.id.add_new_post);
        addNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("objectId", pgObIdList.get(mViewPager.getCurrentItem()));
                intent.setClass(PageActivity.this, AddNewPost.class);
                startActivity(intent);
                overridePendingTransition(android.support.v7.appcompat.R.anim.abc_fade_in, android.support.v7.appcompat.R.anim.abc_fade_out);
            }
        });
    }
}
