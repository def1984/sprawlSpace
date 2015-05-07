package com.example.ai.babel.ui.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.example.ai.babel.R;
import com.example.ai.babel.ui.MainActivity;


public class LoginFragment extends Fragment {

    private Button loginButton;
    private EditText loginEmailInput,loginPasswordInput;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loginButton= (Button) getActivity().findViewById(R.id.login_button);
        loginEmailInput= (EditText) getActivity().findViewById(R.id.login_email_input);
        loginPasswordInput= (EditText) getActivity().findViewById(R.id.login_password_input);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = loginEmailInput.getText().toString();
                String password = loginPasswordInput.getText().toString();
                AVUser.logInInBackground(username, password, new LogInCallback() {
                    public void done(AVUser user, AVException e) {
                        if (user != null) {
                            Toast.makeText(getActivity().getBaseContext(),"登陆成功",Toast.LENGTH_SHORT);
                            Intent mainIntent = new Intent(getActivity().getBaseContext(), MainActivity.class);
                            startActivity(mainIntent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity().getBaseContext(),"登陆失败",Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });
    }
}