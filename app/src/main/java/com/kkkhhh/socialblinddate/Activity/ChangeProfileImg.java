package com.kkkhhh.socialblinddate.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.R;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ChangeProfileImg extends AppCompatActivity {
    private ImageView
            sign_img1 = null,
            sign_img2 = null,
            sign_img3 = null,
            sign_img4 = null,
            sign_img5 = null,
            sign_img6 = null;
    private boolean
            sign_img1_check=false,
            sign_img2_check=false,
            sign_img3_check=false,
            sign_img4_check=false,
            sign_img5_check=false,
            sign_img6_check=false;
    private String
            sign_img1_str,
            sign_img2_str,
            sign_img3_str,
            sign_img4_str,
            sign_img5_str,
            sign_img6_str;

    private ArrayList<Boolean> signImgCheckArray = new ArrayList();

    private ArrayList<ImageView> signImgArray = new ArrayList();

    private ArrayList<String> signImgStrArray = new ArrayList();

    private String[] fileArray=new String[6];

    private Button nextBtn;

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    private FirebaseDatabase mFireDB=FirebaseDatabase.getInstance();
    private DatabaseReference dbRef=mFireDB.getReference().getRoot();

    private FirebaseAuth fireAuth=FirebaseAuth.getInstance();

    private String getUid=fireAuth.getCurrentUser().getUid().toString();

    private RequestManager mGlideRequestManager;

    private int intentCheck;

    private ProgressDialog progressDialog;

    private static long ONE_MEGABYTE = 1024 * 1024;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_img);
        init();
    }
    private void init(){
        sign_img1 = (ImageView) findViewById(R.id.sign_img1);
        signImgArray.add(sign_img1);
        sign_img2 = (ImageView) findViewById(R.id.sign_img2);
        signImgArray.add(sign_img2);
        sign_img3 = (ImageView) findViewById(R.id.sign_img3);
        signImgArray.add(sign_img3);
        sign_img4 = (ImageView) findViewById(R.id.sign_img4);
        signImgArray.add(sign_img4);
        sign_img5 = (ImageView) findViewById(R.id.sign_img5);
        signImgArray.add(sign_img5);
        sign_img6 = (ImageView) findViewById(R.id.sign_img6);
        signImgArray.add(sign_img6);

        signImgCheckArray.add(sign_img1_check);
        signImgCheckArray.add(sign_img2_check);
        signImgCheckArray.add(sign_img3_check);
        signImgCheckArray.add(sign_img4_check);
        signImgCheckArray.add(sign_img5_check);
        signImgCheckArray.add(sign_img6_check);
        mGlideRequestManager=Glide.with(this);

        nextBtn=(Button)findViewById(R.id.sign_img_next_btn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signImgCheckArray.get(0)==false) {
                    Toast.makeText(ChangeProfileImg.this,"대표사진을 등록해주세요",Toast.LENGTH_SHORT).show();
                }else{
                    alertDialog();
                }
            }
        });
        IntentInit();

    }

    private void IntentInit(){
        dbRef.child("users").child(getUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {
                    final UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    if (!userModel._uImage1.equals("@null")) {
                        sign_img1.post(new Runnable() {
                            @Override
                            public void run() {
                                storageRef.child(userModel._uImage1).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        mGlideRequestManager.load(bytes).centerCrop().
                                                crossFade(1000).into(sign_img1);
                                        signImgCheckArray.set(0,true);
                                        String getByteString = Base64.encodeToString(bytes, 0);
                                        fileArray[0] = getByteString;
                                    }
                                });

                            }
                        });

                    }
                    if (!userModel._uImage2.equals("@null")) {
                        sign_img2.post(new Runnable() {
                            @Override
                            public void run() {
                                storageRef.child(userModel._uImage2).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        mGlideRequestManager.load(bytes).centerCrop().
                                                crossFade(1000).into(sign_img2);
                                        signImgCheckArray.set(1,true);
                                        String getByteString = Base64.encodeToString(bytes, 0);
                                        fileArray[1] = getByteString;
                                    }
                                });
                            }
                        });

                    }
                    if (!userModel._uImage3.equals("@null")) {
                        sign_img3.post(new Runnable() {
                            @Override
                            public void run() {
                                storageRef.child(userModel._uImage3).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        mGlideRequestManager.load(bytes).centerCrop().
                                                crossFade(1000).into(sign_img3);
                                        signImgCheckArray.set(2,true);
                                        String getByteString = Base64.encodeToString(bytes, 0);
                                        fileArray[2] = getByteString;
                                    }
                                });
                            }
                        });

                    }
                    if (!userModel._uImage4.equals("@null")) {
                        sign_img4.post(new Runnable() {
                            @Override
                            public void run() {
                                storageRef.child(userModel._uImage4).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        mGlideRequestManager.load(bytes).centerCrop().
                                                crossFade(1000).into(sign_img4);
                                        signImgCheckArray.set(3,true);
                                        String getByteString = Base64.encodeToString(bytes, 0);
                                        fileArray[3] = getByteString;
                                    }
                                });
                            }
                        });

                    }
                    if (!userModel._uImage5.equals("@null")) {
                        sign_img5.post(new Runnable() {
                            @Override
                            public void run() {
                                storageRef.child(userModel._uImage5).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        mGlideRequestManager.load(bytes).centerCrop().
                                                crossFade(1000).into(sign_img5);
                                        signImgCheckArray.set(4,true);
                                        String getByteString = Base64.encodeToString(bytes, 0);
                                        fileArray[4] = getByteString;
                                    }
                                });
                            }
                        });

                    }
                    if (!userModel._uImage6.equals("@null")) {
                        sign_img6.post(new Runnable() {
                            @Override
                            public void run() {
                                storageRef.child(userModel._uImage6).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        mGlideRequestManager.load(bytes).centerCrop().
                                                crossFade(1000).into(sign_img6);
                                        signImgCheckArray.set(5,true);
                                        String getByteString = Base64.encodeToString(bytes, 0);
                                        fileArray[5] = getByteString;
                                    }
                                });
                            }
                        });
                    }
                    imgInit();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void imgInit() {
        for (int index = 0; index < signImgArray.size(); index++) {
            imgSelect(signImgArray.get(index), index);
        }
    }
    //////버튼 클릭 후 이벤트 [사진이 없을시에는 갤러리만 있을시에는 갤러리, 삭제]
    private void imgSelect(final ImageView imageView, final int position) {

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signImgCheckArray.get(position)==true) {
                    intentCheck = position;
                    selectImg(position);
                }else {
                    doTakeAlbumAction();
                    intentCheck = position;
                }
            }
        });
    }
    //앨범을 가기 위한 Intent 값
    private void doTakeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, Crop.REQUEST_PICK);

    }


    //앨범 사진 받아 온 후 Result 값
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }


    private void handleCrop(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data != null) {

                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap orgImage = null;
                    try {
                        orgImage = MediaStore.Images.Media.getBitmap(getContentResolver(), Crop.getOutput(data));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        orgImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] dataByte = baos.toByteArray();
                        // 5. Glide 라이브러리를 이용하여 이미지뷰에 삽입
                        Glide.with(this).
                                load(dataByte)
                                .centerCrop()
                                .into(signImgArray.get(intentCheck));
                        String getByteString = Base64.encodeToString(dataByte, 0);
                        signImgCheckArray.set(intentCheck, true);
                        fileArray[intentCheck] = getByteString;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == Crop.RESULT_ERROR) {
                    Toast.makeText(this, Crop.getError(data).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    // 사진이 있을시에 나오는 Dialog
    private void selectImg(final int position) {
        final CharSequence[] items = {"삭제"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

        builder.setItems(items, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
            public void onClick(DialogInterface dialog, int index) {
                switch (index) {
                    case 0: {
                        signImgArray.get(position).setImageBitmap(null);
                        signImgCheckArray.set(position, false);
                        fileArray[intentCheck]=null;
                        break;
                    }
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //다음화면 버튼
    private void storageUploadEvent() {
       /* if(signImgStrArray.size()>0){
            signImgStrArray.clear();
        }*/
        //fileArray 데이터 값을 이미지 ArrayList로 추출
        for (int index = 0; index < 6; index++) {
            if (fileArray[index] != null) {
                signImgStrArray.add(fileArray[index]);
            }else{
                signImgStrArray.add(null);
            }
        }
        ///
        signImgWriter(signImgStrArray);

    }

    //이미지 ArrayList에 내용이 있으면 입력 없으면 @null로 변환
    private void signImgWriter(List imgArray){
        if (imgArray.get(0) != null) {
            sign_img1_str = imgArray.get(0).toString();
        }else {
            sign_img1_str ="@null";
        }
        if (imgArray.get(1) != null) {
            sign_img2_str = imgArray.get(1).toString();
        }else {
            sign_img2_str ="@null";
        }
        if (imgArray.get(2) != null) {
            sign_img3_str = imgArray.get(2).toString();
        }else {
            sign_img3_str ="@null";
        }
        if (imgArray.get(3) != null) {
            sign_img4_str = imgArray.get(3).toString();
        }else {
            sign_img4_str ="@null";
        }
        if (imgArray.get(4) != null) {
            sign_img5_str = imgArray.get(4).toString();
        }else {
            sign_img5_str ="@null";
        }
        if (imgArray.get(5) != null) {
            sign_img6_str = imgArray.get(5).toString();
        }else {
            sign_img6_str ="@null";
        }


        uploadStorage();
    }
    //이미지 파일을 전송
    private void uploadStorage(){
        if(sign_img1_str=="@null") {
            storageRef.child("userProfile").child(getUid).child("userProfile").child("img1").delete();
        }else{
            byte[] file =Base64.decode(sign_img1_str,0);
            StorageReference img1_Ref=storageRef.child("userProfile").child(getUid).child("img1");
            img1_Ref.putBytes(file);
            sign_img1_str=img1_Ref.getPath();

        }
        if(sign_img2_str=="@null") {
            storageRef.child("userProfile").child(getUid).child("img2").delete();
        }else{
            byte[] file =Base64.decode(sign_img2_str,0);
            StorageReference img2_Ref=storageRef.child("userProfile").child(getUid).child("img2");
            img2_Ref.putBytes(file);
            sign_img2_str=img2_Ref.getPath();
        }
        if(sign_img3_str=="@null") {
            storageRef.child("userProfile").child(getUid).child("img3").delete();
        }else{
            byte[] file =Base64.decode(sign_img3_str,0);
            StorageReference img3_Ref=storageRef.child("userProfile").child(getUid).child("img3");
            img3_Ref.putBytes(file);
            sign_img3_str=img3_Ref.getPath();
        }
        if(sign_img4_str=="@null") {
            storageRef.child("userProfile").child(getUid).child("img4").delete();
        }else{
            byte[] file =Base64.decode(sign_img4_str,0);
            StorageReference img4_Ref=storageRef.child("userProfile").child(getUid).child("img4");
            img4_Ref.putBytes(file);
            sign_img4_str=img4_Ref.getPath();
        }
        if(sign_img5_str=="@null") {
            storageRef.child("userProfile").child(getUid).child("img5").delete();
        }else{
            byte[] file =Base64.decode(sign_img5_str,0);
            StorageReference img5_Ref=storageRef.child("userProfile").child(getUid).child("img5");
            img5_Ref.putBytes(file);
            sign_img5_str=img5_Ref.getPath();
        }
        if(sign_img6_str=="@null") {
            storageRef.child("userProfile").child(getUid).child("img6").delete();
        }else{
            byte[] file =Base64.decode(sign_img5_str,0);
            StorageReference img6_Ref=storageRef.child("userProfile").child(getUid).child("img6");
            img6_Ref.putBytes(file);
            sign_img6_str=img6_Ref.getPath();
        }

        //데이터베이스 레퍼런스 설정(유저 프로필 path)
        DatabaseReference userImgRef=dbRef.child("users").child(getUid);

        //check 값
        userImgRef.child("_uImage1").setValue(sign_img1_str);
        userImgRef.child("_uImage2").setValue(sign_img2_str);
        userImgRef.child("_uImage3").setValue(sign_img3_str);
        userImgRef.child("_uImage4").setValue(sign_img4_str);
        userImgRef.child("_uImage5").setValue(sign_img5_str);
        userImgRef.child("_uImage6").setValue(sign_img6_str, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError !=null){
                    Log.d("dataError",databaseError.toString());
                }else{

                    finish();
                }
            }
        });

    }

    private void alertDialog(){
        LayoutInflater inflater=getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_sign_img,null);
        Button signPerfectBtn=(Button)dialogView.findViewById(R.id.sign_perfect_btn);
        signPerfectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storageUploadEvent();
            }
        });


        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}


