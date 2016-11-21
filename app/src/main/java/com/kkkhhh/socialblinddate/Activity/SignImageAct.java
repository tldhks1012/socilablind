package com.kkkhhh.socialblinddate.Activity;

import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.kkkhhh.socialblinddate.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SignImageAct extends AppCompatActivity {
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

    private  ArrayList<Boolean> signImgCheckArray = new ArrayList();

    private ArrayList<ImageView> signImgArray = new ArrayList();

    private ArrayList<String> signImgStrArray = new ArrayList();

    private String[] fileArray=new String[6];

    private Button nextBtn;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;


    private FirebaseDatabase mFireDB=FirebaseDatabase.getInstance();
    private DatabaseReference dbRef=mFireDB.getReference().getRoot();

    private FirebaseAuth fireAuth=FirebaseAuth.getInstance();

    private String getUid=fireAuth.getCurrentUser().getUid().toString();


    private int intentCheck;

    private ProgressDialog progressDialog;


    private static final int PICK_FROM_GALLERY = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_image);
        init();
    }
//////초기 내용 설정
    private void init() {
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

        progressDialog=new ProgressDialog(SignImageAct.this);

        storageRef = storage.getReferenceFromUrl("gs://socialblinddate.appspot.com");
        storageRef = storageRef.getRoot();

        nextBtn=(Button)findViewById(R.id.sign_img_next_btn);

        imgInit();
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signImgCheckArray.get(0)==false) {
                    Toast.makeText(SignImageAct.this,"대표사진을 등록해주세요",Toast.LENGTH_SHORT).show();
                }else{
                    alertDialog();
                }
            }
        });

    }
    //////이미지 버튼 클릭
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

    //////앨범을 가기 위한 Intent 값
    private void doTakeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_GALLERY);
    }
    //////앨범 사진 받아 온 후 Result 값

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_FROM_GALLERY) {
            if (resultCode == RESULT_OK) {
                String getByteString;
                // 1. Uri 값을 얻은 후에 Uri 실제 경로값을 찾는다
                Uri getUri = data.getData();
                String uriPath = getRealPathFromURI(getUri);
                Bitmap orgImage;
                try {
                    // 2. 사진이 회전되는걸 막기위한 소스
                    ExifInterface exif = new ExifInterface(uriPath);
                    int exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    // 3. 사진의 용량을 줄이는 소스
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;
                    orgImage= BitmapFactory.decodeFile(uriPath, options);
                    orgImage=rotate(orgImage,exifDegree);
                    // 4. Bitmap값을 배열로 변화
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    orgImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] dataByte = baos.toByteArray();
                    // 5. Glide 라이브러리를 이용하여 이미지뷰에 삽입
                    Glide.with(this).
                            load(dataByte)
                            .centerCrop()
                            .into(signImgArray.get(intentCheck));
                    // 6. 지정한 체크값에 intentCheck 사진이 들어갈 경우 true 반환
                    signImgCheckArray.set(intentCheck, true);
                    // 6. 배열값을 그대로 스트링 값으로 삽입
                    getByteString = Base64.encodeToString(dataByte, 0);
                    fileArray[intentCheck] = getByteString;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }
    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if
        (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
    // 실제 이미지 파일 경로
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        CursorLoader cursorLoader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
if(signImgStrArray.size()>0){
    signImgStrArray.clear();
}
    //fileArray 데이터 값을 이미지 ArrayList로 추출
    for (int index = 0; index < 6; index++) {
        if (fileArray[index] != null) {
            signImgStrArray.add(fileArray[index]);
        }else{
            signImgStrArray.add(null);
        }
        int size =signImgStrArray.size();
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
        userImgRef.child("check").setValue(3);
        userImgRef.child("_uCoin").setValue(500);
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
                    progressDialog.cancel();
                    Intent intent = new Intent(SignImageAct.this,MainAct.class);
                    startActivity(intent);
                    Toast.makeText(SignImageAct.this,"회원 가입이 완료 되었습니다",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

  /*      UserImg userImg = new UserImg(sign_img1_str,sign_img2_str,sign_img3_str,sign_img4_str,sign_img5_str,sign_img6_str);
        dbRef.child("users").child(getUid).child("profileImg").setValue(userImg, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError !=null){
                    Log.d("dataError",databaseError.toString());
                }else{
                    progressDialog.cancel();
                    Intent intent = new Intent(SignImageAct.this,MainAct.class);
                    startActivity(intent);
                    Toast.makeText(SignImageAct.this,"회원 가입이 완료 되었습니다",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });*/

    }

    private void alertDialog(){
        LayoutInflater inflater=getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_sign_img,null);
        Button signPerfectBtn=(Button)dialogView.findViewById(R.id.sign_perfect_btn);
        signPerfectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    progressDialog.setMessage("회원정보를 저장하고 있습니다.");
                    progressDialog.show();
                    storageUploadEvent();
                }
        });


        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog=builder.create();
        dialog.show();
    }


}

