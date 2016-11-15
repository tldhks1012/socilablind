package com.kkkhhh.socialblinddate.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kkkhhh.socialblinddate.R;

public class LoginAct extends AppCompatActivity implements View.OnClickListener{

    private EditText loginEmailEd,loginPasswordEd;
    private Button loginBtn;
    private String loginEmailStr ,loginPasswordStr;
    private FirebaseAuth mFireBaseAuth = FirebaseAuth.getInstance();
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

    }

    private void init(){

        loginEmailEd=(EditText)findViewById(R.id.login_email);
        loginPasswordEd=(EditText)findViewById(R.id.login_password);
        loginBtn=(Button)findViewById(R.id.login_btn);

        mProgressDialog=new ProgressDialog(LoginAct.this);

        loginBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        mProgressDialog.setMessage("로그인 중 입니다");
        mProgressDialog.show();

        loginEmailStr=loginEmailEd.getText().toString().trim();
        loginPasswordStr=loginPasswordEd.getText().toString().trim();

        if(TextUtils.isEmpty(loginEmailStr)){
            Toast.makeText(LoginAct.this,"이메일을 입력해주세요.",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(loginPasswordStr)){
            Toast.makeText(LoginAct.this,"패스워드를 입력해주세요.",Toast.LENGTH_SHORT).show();
        }else{
            mFireBaseAuth.signInWithEmailAndPassword(loginEmailStr,loginPasswordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d("로그인 성공", "signInWithEmail:onComplete:" + task.isSuccessful());
                        Intent intent= new Intent(LoginAct.this,MainAct.class);
                        startActivity(intent);
                        Toast.makeText(LoginAct.this,loginEmailStr+"님 로그인이 완료되었습니다",Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Log.w("로그인 실패", "signInWithEmail:failed", task.getException());
                        Toast.makeText(LoginAct.this,loginEmailStr+"아이디와 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }
}
