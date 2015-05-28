package com.example.ai.babel.ui.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.ai.babel.R;
import com.example.ai.babel.ui.DetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DemoObjectFragment extends android.support.v4.app.Fragment {


    public static final String ARG_OBJECT = "object";
    private ListView postList;

    private AVUser currentUser = AVUser.getCurrentUser();
    @Override
    public void onResume() {
        super.onResume();
        new UpDataPostList().execute();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
        Bundle args = getArguments();

        ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                Integer.toString(args.getInt(ARG_OBJECT)));
        return rootView;
    }



    class UpDataPostList extends AsyncTask<Void, Integer, Boolean> {

        ArrayList<HashMap<String, Object>> listItemMain = new ArrayList<HashMap<String, Object>>();
        ArrayList<String> postObjIDList = new ArrayList<String>();

        ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            AVQuery<AVObject> query = AVQuery.getQuery("Post");
            query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
            postList = (ListView) getActivity().findViewById(R.id.post_list);

            query.whereEqualTo("userObjectId", currentUser);

            query.orderByDescending("createdAt");
            List<AVObject> commentList = null;
            try {
                commentList = query.find();
            } catch (AVException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < commentList.size(); i++) {
                HashMap<String, Object> allDrawNavTag = new HashMap<String, Object>();
                allDrawNavTag.put("postImage", R.drawable.ic_tick);//加入图片
                allDrawNavTag.put("postTitle", commentList.get(i).getString("title"));
                allDrawNavTag.put("postContent", commentList.get(i).getString("content"));
                listItemMain.add(allDrawNavTag);
                postObjIDList.add(commentList.get(i).getObjectId());
            }
            return true;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate();
            dialog.setMessage("当前下载进度：" + values[0] + "%");
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            SimpleAdapter mSimpleAdapter = new SimpleAdapter(getActivity(), listItemMain,//需要绑定的数据
                    R.layout.content_item,
                    new String[]{"postImage", "postTitle", "postContent"},
                    new int[]{R.id.contentImage, R.id.postTitle, R.id.postContent}
            );


            postList.setAdapter(mSimpleAdapter);//为ListView绑定适配器


            postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.putExtra("objectId", postObjIDList.get(position));
                    intent.setClass(getActivity(), DetailActivity.class);
                    startActivity(intent);
                }
            });

            postList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                @Override
                public boolean onItemLongClick(AdapterView parent, View view, final int position,
                                               long id) {
                    DeleteDialog();
                    AVQuery<AVObject> query = AVQuery.getQuery("Post");

                    query.whereEqualTo("objectId", postObjIDList.get(position));
                    query.findInBackground(new FindCallback<AVObject>() {
                        public void done(List<AVObject> avObjects, AVException e) {
                            if (e == null) {
                                avObjects.get(0).deleteInBackground();
                            } else {
                            }
                        }
                    });
                    return true;
                }
            });
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
                }, 50);
            }
        });
        builder.create().show();
    }

}