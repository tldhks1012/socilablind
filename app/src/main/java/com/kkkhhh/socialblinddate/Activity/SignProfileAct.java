package com.kkkhhh.socialblinddate.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.Model.UserProfile;
import com.kkkhhh.socialblinddate.R;

import java.util.Arrays;

//////////// 회원가입 유저 프로필 입력 Act //////////////////

public class SignProfileAct extends AppCompatActivity implements View.OnClickListener {
    private Button dialogLocalBtn, dialogGenderBtn,profileNextBtn;
    private String[] itemsLocal = {"서울", "부산", "대구", "대전","울산","광주","인천","세종","경기","경남","경북","전남","전북","강원","제주","충북","충남"};
    private String[] itemsGender = {"남자","여자"};
    private FirebaseDatabase mFirebaseDatabase=FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference=mFirebaseDatabase.getReference().getRoot();
    private DatabaseReference signProfileRef;
    private EditText nicknameEdit,ageEdit;
    private String nicknameStr, ageStr,genderStr,localStr;
    private FirebaseAuth mFireAuth= FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_profile);
        init();
    }

    ///초기 내용 설정
    private void init(){
        nicknameEdit = (EditText)findViewById(R.id.sign_nickname);
        ageEdit = (EditText)findViewById(R.id.sign_age);

        dialogLocalBtn=(Button)findViewById(R.id.sign_local);
        dialogGenderBtn=(Button)findViewById(R.id.sign_gender);
        profileNextBtn=(Button)findViewById(R.id.sign_profile_next);

        dialogGenderBtn.setOnClickListener(this);
        dialogLocalBtn.setOnClickListener(this);
        profileNextBtn.setOnClickListener(this);

        progressDialog=new ProgressDialog(this);

        userID=mFireAuth.getCurrentUser().getUid();

        signProfileRef=databaseReference.child("users").child(userID);



    }

    ///다음버튼 눌렸을 시 - 값을 받아서 체크 후 전송
    private void profileNext(){
        nicknameStr=nicknameEdit.getText().toString().trim();
        ageStr=ageEdit.getText().toString().trim();
        genderStr=dialogGenderBtn.getText().toString().trim();
        localStr=dialogLocalBtn.getText().toString().trim();

        if(TextUtils.isEmpty(nicknameStr)){
            Toast.makeText(getApplicationContext(),"닉네임을 입력해주세요.",Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(ageStr)){
            Toast.makeText(getApplicationContext(),"나이를 입력해주세요.",Toast.LENGTH_SHORT).show();
        }else  if(TextUtils.isEmpty(genderStr)){
            Toast.makeText(getApplicationContext(),"성별을 선택해주세요",Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(localStr)){
            Toast.makeText(getApplicationContext(),"지역을 선택해주세요.",Toast.LENGTH_SHORT).show();
        }else{
            ///전송
            writeUserSecond(nicknameStr,ageStr,localStr,genderStr);
        }


    }
    ///showDialog [성별, 지역]
    private void showDialog(final String[] item, String title, final Button btn){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignProfileAct.this);


        builder.setTitle(title);
        builder.setPositiveButton("닫기", null);

        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedText = Arrays.asList(item).get(which);

                btn.setText(selectedText);
                dialog.cancel();

            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();

    }
    ///Firebase User 모델 전송 메소드
    private void writeUserSecond(String nickname, String age, String local,String gender) {
        //progressDialog 보여준다
        progressDialog.setMessage("정보를 저장중입니다.");
        progressDialog.show();

        //Database userProfile 에 셋팅
        signProfileRef.child("check").setValue(2);
        signProfileRef.child("_uNickname").setValue(nickname);
        signProfileRef.child("_uAge").setValue(age);
        signProfileRef.child("_uLocal").setValue(local);
        signProfileRef.child("_uGender").setValue(gender, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    //전송 오류가 나면 뜨는 메세지
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                    Toast.makeText(SignProfileAct.this, "정보가 저장 되질 않습니다.", Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                } else {
                    //전송이 완성 했을 경우
                    Intent intent = new Intent(SignProfileAct.this, SignImageAct.class);
                    startActivity(intent);
                    progressDialog.cancel();
                    finish();
                }
            }
        });
    }
    ///버튼클릭 리스너
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.sign_local: {
                showDialog(itemsLocal, "지역 선택", dialogLocalBtn);
                break;
            }
            case R.id.sign_gender:{
                showDialog(itemsGender, "성별 선택", dialogGenderBtn);
                break;
            }
            case R.id.sign_profile_next:{
                profileNext();
                break;
            }
default:
        }
    }
}
