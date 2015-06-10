package com.example.ai.babel.ui.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.SaveCallback;
import com.example.ai.babel.R;

import org.xwalk.core.XWalkView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.richeditor.RichEditor;


public class PageObjectFragment extends android.support.v4.app.Fragment {

    private AVObject pageObj;
    private EditText pageTitle;
    private ArrayList<View> pgViewList = new ArrayList<View>();
    private LinearLayout pageBox ;
    private XWalkView mXWalkView;

    private RichEditor mEditor;
    private TextView mPreview;
    @Override
    public void onResume() {
        super.onResume();
        new UpDataPostList().execute();
        if (mXWalkView != null) {
            mXWalkView.resumeTimers();
            mXWalkView.onShow();
        }
    }

    public PageObjectFragment(AVObject pageObj) {
        this.pageObj = pageObj;
    }

    public PageObjectFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mXWalkView != null) {
            mXWalkView.pauseTimers();
            mXWalkView.onHide();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pageObj.put("title", pageTitle.getText());
        pageObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                } else {
                    Log.e("LeanCloud", "Save failed.");
                }
            }
        });
        if (mXWalkView != null) {
            mXWalkView.onDestroy();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_collection_page, container, false);
        pageTitle = (EditText) rootView.findViewById(R.id.page_title);
        pageTitle.setText(pageObj.get("title").toString());
//        mXWalkView = (XWalkView) rootView.findViewById(R.id.edit_main);
//        mXWalkView.load("file:///android_asset/index.html", null);
        mEditor = (RichEditor) rootView.findViewById(R.id.editor);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(16);
        mEditor.setPlaceholder("请输入内容...");
        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mXWalkView != null) {
            mXWalkView.onActivityResult(requestCode, resultCode, data);
        }
    }



    class UpDataPostList extends AsyncTask<Void, Integer, Boolean> {
        ArrayList<HashMap<String, Object>> listItemMain = new ArrayList<HashMap<String, Object>>();
        ArrayList<String> postObjIDList = new ArrayList<String>();
        String errMs = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            AVQuery<AVObject> query = AVQuery.getQuery("Post");
            query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.whereEqualTo("pgObjectId", pageObj);
            query.orderByDescending("createdAt");
            List<AVObject> commentList = null;
            try {
                commentList = query.find();
                for (int i = 0; i < commentList.size(); i++) {

                }
            } catch (AVException e) {
                e.printStackTrace();
                errMs=e.getMessage();
            }
            return true;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate();
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }
    }

    private void DeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确定删除？");
        builder.setTitle("提示");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                   new UpDataPostList().execute();
                    }
                }, 10);
            }
        });
        builder.create().show();
    }

}
