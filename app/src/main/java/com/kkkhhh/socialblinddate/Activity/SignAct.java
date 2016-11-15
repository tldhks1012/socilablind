package com.kkkhhh.socialblinddate.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SignAct extends AppCompatActivity implements View.OnClickListener {

    private EditText emailEdit,passwordEdit;
    private String emailStr,passwordStr;
    private Button signPostBtn,signPerfectBtn;
    private CheckBox signUserInfoCb,signUseCb;
    private TextView signUserInfoView,signUseView;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseRef= mFirebaseDatabase.getReference().getRoot();
    private ProgressDialog progressDialog;
    private String strCurDate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign);
        init();

    }
//초기 설정
    private void init(){
        emailEdit=(EditText)findViewById(R.id.sign_email);
        passwordEdit = (EditText)findViewById(R.id.sign_password);


        progressDialog=new ProgressDialog(this);

        signPostBtn=(Button)findViewById(R.id.sign_next_btn);


        signPostBtn.setOnClickListener(this);

        //현재 날짜 및 시간 구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        strCurDate = CurDateFormat.format(date);


    }


    //다음화면 누를경우
    @Override
    public void onClick(View view) {

        if(view==signPostBtn) {
            //EditText 값을 받음
            emailStr = emailEdit.getText().toString().trim();
            passwordStr = passwordEdit.getText().toString().trim();

            //에디트 텍스트 값 체크
            if (TextUtils.isEmpty(emailStr)) {

                Toast.makeText(SignAct.this, "이메일을 확인 해주세요.", Toast.LENGTH_SHORT).show();

                return;
            }
            else if (TextUtils.isEmpty(passwordStr)) {
                Toast.makeText(SignAct.this, "패스워드를 확인 해주세요.", Toast.LENGTH_SHORT).show();

                return;
            }else if (passwordStr.length() < 6) {

                Toast.makeText(SignAct.this, "패스워드는 6글자 이상 입니다.", Toast.LENGTH_SHORT).show();
            }
            else {
                //다이아로그 창 메소드
                alertDialog();
            }
        }
        }
    @Override
    protected void onStart(){
           super.onStart();
    }

//데이터 베이스 Push
    private void userSignPutData(String uID,String email){


//데이터 베이스 UserModel 값 입력
        UserModel mUserModel= new UserModel(uID,email,strCurDate);
        mDatabaseRef.child("users").child(uID).setValue(mUserModel);
        //데이터 베이스 SignCheck : 1일 경우 이메일 정보, 2일 경우 프로필정보 ,3일 경우 이미지정보
        mDatabaseRef.child("users").child(uID).child("check").setValue(1);

    }


    private void alertDialog(){
        //다이아로그 창 메소드
        final LayoutInflater inflater=getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_sign,null);
        signUserInfoCb=(CheckBox)dialogView.findViewById(R.id.sign_user_info_check);
        signUseCb=(CheckBox)dialogView.findViewById(R.id.sign_user_use_check);
        signUserInfoView=(TextView)dialogView.findViewById(R.id.sign_user_info_view);
        signUserInfoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignAct.this,SignUserInfoAct.class);
                startActivity(intent);
            }
        });
        signUseView=(TextView)dialogView.findViewById(R.id.sign_user_use_view);
        signPerfectBtn=(Button)dialogView.findViewById(R.id.sign_perfect_btn);
        //회원가입 완료를 눌렸을때 이벤트
        signPerfectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //개인정보 & 이용약관 체크
                if(signUserInfoCb.isChecked()==false){
                    Toast.makeText(SignAct.this,"개인정보 방침에 동의해주세요.",Toast.LENGTH_SHORT).show();
                }else if(signUseCb.isChecked()==false){
                        Toast.makeText(SignAct.this,"이용약관에 동의해주세요.",Toast.LENGTH_SHORT).show();
                }else{
                    //개인정보 & 이용약관 체크가 다 됐을 경우 progressDialog 메세지 뜸
                    progressDialog.setMessage("회원정보를 저장하고 있습니다.");
                    progressDialog.show();
                    //계정 생성 코드 메소드
                    createAuth();
                }
            }
        });

//dialog 세팅
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    //계정 생성
    private void createAuth(){
        mFirebaseAuth.createUserWithEmailAndPassword(emailStr,passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //계정 생성이 성공할 경우
                    String userID=mFirebaseAuth.getCurrentUser().getUid().toString();
                    //데이터베이스 사용자 프로필에 uID,email 값 입력
                    userSignPutData(userID,emailStr);
                    Intent intent= new Intent(SignAct.this,SignProfileAct.class);
                    startActivity(intent);
                    progressDialog.cancel();
                    finish();
                }else{
                    //실패 할 경우
                    Toast.makeText(SignAct.this,"메일을 확인해주세요.",Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                }
            }
        });

    }
}
