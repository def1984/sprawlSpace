package com.example.ai.babel.ui.fragment;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.example.ai.babel.AVService;
import com.example.ai.babel.R;
import com.example.ai.babel.ui.InitActivity;


public class RegisterFragment extends Fragment {

    Button registerButton;
    EditText userEmail, userPassword , writeNameED;
    private ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerButton = (Button) getActivity().findViewById(R.id.register_button);
        userEmail = (EditText) getActivity().findViewById(R.id.email_edit);
        userPassword = (EditText) getActivity().findViewById(R.id.password_edit);
        writeNameED = (EditText) getActivity().findViewById(R.id.edit_nick_name);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userEmail.getText().toString().isEmpty()) {
                    if (!userPassword.getText().toString().isEmpty()) {
                        progressDialogShow();
                        register();
                    } else {
                        Toast.makeText(getActivity().getBaseContext(), "密码为空", Toast.LENGTH_SHORT).show();
                    }
                }else {
                        Toast.makeText(getActivity().getBaseContext(), "电子邮件为空", Toast.LENGTH_SHORT).show();
                    }
                }
        });

    }

    public void register() {
        SignUpCallback signUpCallback = new SignUpCallback() {
            public void done(AVException e) {
                progressDialogDismiss();
                if (e == null) {
                    showRegisterSuccess();
                    AVUser currentUser = AVUser.getCurrentUser();
                    AVObject newBook = new AVObject("Book");
                    AVObject newPage = new AVObject("Page");
                    newBook.put("title", "欢迎来到巴别");
                    newBook.put("description", "在此你可以点击进去书写一些你需要写的任何事物");
                    newBook.put("userObjectId", currentUser);
                    currentUser.put("bookIndex", 0);
                    newPage.put("bookObjectId", newBook);
                    newPage.put("title", "");
                    newPage.put("content", "");
                    newPage.saveInBackground();
                    Intent mainIntent = new Intent(getActivity(), InitActivity.class);
                    startActivity(mainIntent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "错误："+e.getCode(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        String password = userPassword.getText().toString();
        String email = userEmail.getText().toString();
        String writeName = writeNameED.getText().toString();
        AVService.signUp(password, email, writeName, signUpCallback);
    }

    private void progressDialogDismiss() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void progressDialogShow() {
        progressDialog = ProgressDialog
                .show(getActivity(), getActivity().getResources().getText(
                                R.string.dialog_message_title),
                        getActivity().getResources().getText(
                                R.string.dialog_text_wait), true, false);
    }

    private void showRegisterSuccess() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.dialog_message_title))
                .setMessage(getActivity().getResources().getString( R.string.success_register_success))
                .setNegativeButton(android.R.string.ok,new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

}
