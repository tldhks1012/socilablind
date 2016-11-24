package com.kkkhhh.socialblinddate.Activity;

import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kkkhhh.socialblinddate.Etc.DataBaseFiltering;
import com.kkkhhh.socialblinddate.Etc.UserValue;
import com.kkkhhh.socialblinddate.Model.Post;
import com.kkkhhh.socialblinddate.Model.UserModel;
import com.kkkhhh.socialblinddate.R;
import com.rey.material.widget.ProgressView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostWriterAct extends AppCompatActivity {
    private ImageView
            writeIv = null;
    private boolean
            writeIvCheck = false;
    private String
            writeIvStr;

    private ArrayList<Boolean> writerImgCheckArray = new ArrayList();

    private ArrayList<ImageView> writerImgArray = new ArrayList();

    private ArrayList<String> writerImgStrArray = new ArrayList();

    private String[] fileArray = new String[1];

    private EditText writerTitle, writerBody;

    private String titleStr, bodyStr,userGender,userAge,userLocal,userProfileImg;

    private Button uploadButton;

    private StorageReference storageRef =FirebaseStorage.getInstance().getReference();

    private FirebaseDatabase mFireDB = FirebaseDatabase.getInstance();
    private DatabaseReference dataReference = mFireDB.getReference().getRoot();
    private DatabaseReference manReference;
    private DatabaseReference womanReference;

    private FirebaseAuth fireAuth = FirebaseAuth.getInstance();

    private String getUid = fireAuth.getCurrentUser().getUid().toString();

    private int intentCheck;

    private ProgressDialog progressDialog;

    private String updateLocal,updateGender,updatePostKey,updateImg1;

    String _getKey ;

    private static final int PICK_FROM_GALLERY = 0;

    private ProgressView progressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_writer);
        init();

    }
//UI 초기 설정
    private void init() {
        //제목 에디트 텍스트
        writerTitle = (EditText) findViewById(R.id.writer_title);
        //내용 에디트 텍스트
        writerBody = (EditText) findViewById(R.id.writer_body);
        //사진 이미지뷰
        writeIv = (ImageView) findViewById(R.id.writer_img1);
        //프로그래스뷰
        progressView=(ProgressView)findViewById(R.id.progressview);
       //업로드 버튼
        uploadButton = (Button) findViewById(R.id.writer_img_upload_btn);
        //이미지뷰 리스트 이미지 추가
        writerImgArray.add(writeIv);

        //체크리스트에 체크 추가
        writerImgCheckArray.add(writeIvCheck);

        //남자 DB레퍼런스
        manReference=dataReference.child("/posts/man-posts/");
        //여자 DB레퍼런스
        womanReference=dataReference.child("/posts/woman-posts/");

        //유저 값 들고오기
        getUserValue();

        //프로그래스 참조
        progressDialog = new ProgressDialog(PostWriterAct.this);

        //수정 받아 올때
        receiveIntent();

        //사진등록
        imgInit();

        //업로드 버튼
        _upload();


    }

    private void getUserValue() {
                   SharedPreferences preferences= getSharedPreferences(UserValue.SHARED_NAME,MODE_PRIVATE);
                    userLocal = preferences.getString(UserValue.USER_LOCAL,null);
                    userGender = preferences.getString(UserValue.USER_GENDER,null);
                    userAge = preferences.getString(UserValue.USER_AGE,null);
                    userProfileImg = preferences.getString(UserValue.USER_IMG1,null);
    }

    private void _upload(){
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleStr = writerTitle.getText().toString();
                bodyStr = writerBody.getText().toString();

                if (TextUtils.isEmpty(titleStr)) {
                    Toast.makeText(PostWriterAct.this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(bodyStr)) {
                    Toast.makeText(PostWriterAct.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    uploadButtonClick();
                }
            }
        });
    }

    //수정으로 들어올때
    private void receiveIntent() {

        Intent intent = getIntent();
        if (intent.getStringExtra("local") != null) {
            progressView.setVisibility(View.VISIBLE);
            updateLocal = intent.getStringExtra("local");
            updateGender = intent.getStringExtra("gender");
            updatePostKey = intent.getStringExtra("postKey");

            if (updateGender.equals("남자")) {
                receiveIntentReference(manReference);
            } else if (updateGender.equals("여자")) {
                receiveIntentReference(womanReference);
            }
        }
    }

    //수정으로 들어 온 후 데이터베이스 레퍼런스
    private void receiveIntentReference(DatabaseReference databaseReference){
        databaseReference.child(updatePostKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    Post post=dataSnapshot.getValue(Post.class);
                    writerTitle.setText(post.title);
                    writerBody.setText(post.body);
                    if(!post.img1.equals("@null")) {
                        Glide.with(PostWriterAct.this).using(new FirebaseImageLoader()).load(storageRef.child(post.img1)).centerCrop().listener(new RequestListener<StorageReference, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                                progressView.setVisibility(View.INVISIBLE);
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressView.setVisibility(View.INVISIBLE);
                                return false;
                            }
                        }).into(writeIv);
                        writerImgCheckArray.set(0, true);
                        updateImg1=post.img1;
                    }else{
                        writerImgCheckArray.set(0, false);
                        updateImg1=post.img1;
                        progressView.setVisibility(View.INVISIBLE);
                    }
                }
                _upload();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //이미지 버튼 클릭
    private void imgInit() {
        for (int index = 0; index < writerImgArray.size(); index++) {
            imgSelect(writerImgArray.get(index), index);
        }
    }

    //버튼 클릭 후 이벤트 [사진이 없을시에는 갤러리만 있을시에는 갤러리, 삭제]
    private void imgSelect(final ImageView imageView, final int position) {

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (writerImgCheckArray.get(position) == true) {
                    intentCheck = position;
                    selectImg(position);
                } else {
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
        startActivityForResult(intent, PICK_FROM_GALLERY);
    }


    //앨범 사진 받아 온 후 Result 값
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
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    // 3. 사진의 용량을 줄이는 소스
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    /*options.inSampleSize = 2;*/
                    orgImage = BitmapFactory.decodeFile(uriPath, options);
                    orgImage = rotate(orgImage, exifDegree);
                    // 4. Bitmap값을 배열로 변화
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    orgImage.compress(Bitmap.CompressFormat.JPEG, 65, baos);
                    byte[] dataByte = baos.toByteArray();
                    // 5. Glide 라이브러리를 이용하여 이미지뷰에 삽입
                    Glide.with(this).
                            load(dataByte)
                            .centerCrop()
                            .into(writerImgArray.get(intentCheck));
                    // 6. 지정한 체크값에 intentCheck 사진이 들어갈 경우 true 반환
                    writerImgCheckArray.set(intentCheck, true);
                    // 6. 배열값을 그대로 스트링 값으로 삽입
                    getByteString = Base64.encodeToString(dataByte, 0);
                    fileArray[intentCheck] = getByteString;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    //사진
    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
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

    //실제 이미지 파일 경로
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        CursorLoader cursorLoader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //사진이 있을시에 나오는 Dialog
    private void selectImg(final int position) {
        final CharSequence[] items = {"삭제"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

        builder.setItems(items, new DialogInterface.OnClickListener() {    // 목록 클릭시 설정
            public void onClick(DialogInterface dialog, int index) {
                switch (index) {
                    case 0: {
                        if(updateImg1!=null) {
                            if (!updateImg1.equals("@null")) {
                                updateImg1 = "@null";
                                writerImgArray.get(position).setImageBitmap(null);
                                writerImgCheckArray.set(position, false);
                                fileArray[intentCheck] = null;
                            }else if(updateImg1.equals("@null")){
                                updateImg1 = "@null";
                                writerImgArray.get(position).setImageBitmap(null);
                                writerImgCheckArray.set(position, false);
                                fileArray[intentCheck] = null;
                            }
                        }else {
                            writerImgArray.get(position).setImageBitmap(null);
                            writerImgCheckArray.set(position, false);
                            fileArray[intentCheck] = null;
                        }
                        break;
                    }
                }

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //업로드 버튼
    private void uploadButtonClick() {

        progressDialog.setMessage("포스트를 저장 중 입니다.");
        progressDialog.show();
        if (writerImgStrArray.size() > 0) {
            writerImgStrArray.clear();
        }
        //fileArray 데이터 값을 이미지 ArrayList로 추출
        for (int index = 0; index < 1; index++) {
            if (fileArray[index] != null) {
                writerImgStrArray.add(fileArray[index]);
            } else {
                writerImgStrArray.add(null);
            }
        }
        ///
        writerImgWriter(writerImgStrArray);

    }

    //이미지 ArrayList에 내용이 있으면 입력 없으면 @null로 변환
    private void writerImgWriter(List imgArray) {
        if (imgArray.get(0) != null) {
            writeIvStr = imgArray.get(0).toString();
        } else {
            writeIvStr = "@null";
        }
        uploadStorage();
    }

    //이미지 파일을 전송
    private void uploadStorage() {
        _getKey = dataReference.child("posts").push().getKey();
        if (writeIvStr == "@null") {
            storageRef.child(getUid).child("img1").delete();
        } else {
            byte[] file = Base64.decode(writeIvStr, 0);
            StorageReference img1_Ref = storageRef.child("post").child(_getKey).child(getUid).child("img1");
            img1_Ref.putBytes(file);
            writeIvStr = img1_Ref.getPath();
        }

        if(updateImg1!=null){
            if(!updateImg1.equals("@null")&&(writerImgCheckArray.get(0)==true)){
                writeNewPost(getUid, userProfileImg, titleStr, bodyStr, updateImg1, userLocal, userGender, userAge,updatePostKey);
            }else{
                writeNewPost(getUid, userProfileImg, titleStr, bodyStr, writeIvStr, userLocal, userGender, userAge,updatePostKey);
            }
        }else {
            writeNewPost(getUid, userProfileImg, titleStr, bodyStr, writeIvStr, userLocal, userGender, userAge,_getKey);
        }
    }



    private void writeNewPost(String userId,String userImg, String title, String body, String img1,String local,String gender,String age,String key) {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String stampTime = CurDateFormat.format(date);

        long yetNow = -1 * new Date().getTime();



        Post post = new Post(userId,userImg, title, body, img1,local,gender,age,stampTime,key,yetNow);
        Map<String, Object> postValues = post.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        DataBaseFiltering dataBaseFiltering =new DataBaseFiltering();
        String localChange=dataBaseFiltering.changeLocal(local);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
        if(gender.equals("여자")) {
            childUpdates.put("/posts/woman-posts/" + key, postValues);
            childUpdates.put("/posts/woman-posts-local/"+localChange+"/"+key,postValues);
        }else if(gender.equals("남자")){
            childUpdates.put("/posts/man-posts/" + key, postValues);
            childUpdates.put("/posts/man-posts-local/"+localChange+"/"+key,postValues);
        }

        dataReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d("dataError", databaseError.toString());
                } else {
                    progressDialog.cancel();
                    finish();
                }
            }
        });

/*    private void alertDialog(){
        LayoutInflater inflater=getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_writer_img,null);
        Button writerPerfectBtn=(Button)dialogView.findViewById(R.id.writer_perfect_btn);
        writerPerfectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("회원정보를 저장하고 있습니다.");
                progressDialog.show();

            }
        });


        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog=builder.create();
        dialog.show();
    }*/
    }
}
