package com.example.yunshaonote;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.listener.DialogUIItemListener;
import com.dou361.dialogui.listener.DialogUIListener;
import com.example.yunshaonote.db.FragItem;
import com.example.yunshaonote.db.Note;
import com.example.yunshaonote.db.NoteItem;
import com.example.yunshaonote.service.DateRemindService;
import com.example.yunshaonote.service.RemindService;
import com.example.yunshaonote.util.FormatUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.codbking.widget.bean.DateType.TYPE_YMD;

public class EditNoteActivity extends AppCompatActivity {

    private ImageButton backButton;

    private TextView title;

    private ImageButton finishButton;

    private TextView dateTextView;

    private Button selectButton;

    private Note note;

    private EditText editText;

    public Uri imageUri;

    public static final int TAKE_PHOTO = 1;

    public static final int CHOOSE_PHOTO = 2;

    public ImageView picture;

    public String mImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        backButton = (ImageButton) findViewById(R.id.left_button);
        title = (TextView) findViewById(R.id.title_textview);
        finishButton = (ImageButton) findViewById(R.id.right_button);
        dateTextView = (TextView) findViewById(R.id.date_note_tv);
        selectButton = (Button) findViewById(R.id.select_button);
        editText = (EditText) findViewById(R.id.edit_content);
        picture = (ImageView) findViewById(R.id.edit_photo);
        backButton.setBackgroundResource(R.drawable.back_light);
        finishButton.setBackgroundResource(R.drawable.finish_prompt);
        String date = getIntent().getStringExtra("date");
        if (date.equals("add")) {
            addNote();
        } else {
            editNote(date);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void addNote(){
        note = new Note();
        selectButton.setBackgroundResource(R.drawable.time);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
        title.setText("增加日程");
        dateTextView.setText("");
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickDialog dialog = new DatePickDialog(EditNoteActivity.this);
                dialog.setYearLimt(2);
                dialog.setTitle("选择时间");
                dialog.setType(TYPE_YMD);
                dialog.setMessageFormat("yyyy-MM-dd");
                dialog.setOnSureLisener(new OnSureLisener() {
                    @Override
                    public void onSure(Date date) {
                        String s = FormatUtil.dateToString(date);
                        if (FormatUtil.isRepeat(s)){
                            Toast.makeText(EditNoteActivity.this, "该日程已存在！请重新选择", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            note.setDate(s);
                            note.setTimeId(date.getTime());
                            dateTextView.setText(FormatUtil.switchDate(note.getTimeId()));
                        }
                    }
                });
                dialog.show();
            }
        });
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (note.getDate() == null) {
                    note.setContent(editText.getText().toString());
                    note.setUpdateTime(FormatUtil.getEditTime());
                    note.setNoteType(FormatUtil.STICKY_TYPE);
                    note.setTimeId(System.currentTimeMillis());
                    note.save();
                    Toast.makeText(EditNoteActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    note.setContent(editText.getText().toString());
                    note.setUpdateTime(FormatUtil.getEditTime());
                    note.setNoteType(FormatUtil.DATE_TYPE);
                    note.save();
                    Toast.makeText(EditNoteActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(EditNoteActivity.this, DateRemindService.class);
                    startService(intent1);
                    finish();
                }
            }
        });
    }

    public void editNote(final String foreDate){
        //从数据库中查找数据
        List<Note> noteList = DataSupport.where("date = ?", foreDate).find(Note.class);
        if (noteList.size() == 0) {
            noteList = DataSupport.where("timeId = ?", foreDate).find(Note.class);
        }
        note = noteList.get(0);
        if (note.getImagePath() != null){
            if (note.getImagePath() != null){
                Bitmap bitmap = BitmapFactory.decodeFile(note.getImagePath());
                picture.setImageBitmap(bitmap);
            }
            picture.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DialogUIUtils.init(EditNoteActivity.this);
                    DialogUIUtils.showAlertHorizontal(EditNoteActivity.this, "提示", "是否删除此张照片？", new DialogUIListener() {
                        @Override
                        public void onPositive() {
                            note.setToDefault("imagePath");
                            note.save();
                            Bitmap bitmap1 = ((BitmapDrawable) picture.getDrawable()).getBitmap();
                            picture.setImageBitmap(null);
                            if (bitmap1 != null && !bitmap1.isRecycled()){
                                bitmap1.recycle();
                            }
                        }

                        @Override
                        public void onNegative() {

                        }
                    }).show();
                    return true;
                }
            });
        }
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title.setText("编辑日程");
        editText.setText(note.getContent());
        if (note.getNoteType() == FormatUtil.DATE_TYPE) {
            dateTextView.setText(FormatUtil.switchDate(note.getTimeId()));
        } else {
            dateTextView.setText("记事本");
        }
        selectButton.setBackgroundResource(R.drawable.camera);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> strings = new ArrayList<>();
                strings.add("用相机拍摄");
                strings.add("从相册中选择");
                DialogUIUtils.init(EditNoteActivity.this);
                DialogUIUtils.showBottomSheetAndCancel(EditNoteActivity.this, strings, "取消", new DialogUIItemListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        if (position == 0) {
                            takePhoto();
                        } else if (position == 1) {
                            choosePhoto();
                        }
                    }
                }).show();
            }
        });
        finishButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String newContent = editText.getText().toString();
                note.setContent(newContent);
                note.setUpdateTime(FormatUtil.getEditTime());
                note.save();
                String[] itemArr = newContent.split("\n");
                List<FragItem> fragItemList = DataSupport.where("date = ?", String.valueOf(note.getTimeId())).find(FragItem.class);
                for (FragItem fragItem : fragItemList){
                    boolean deleteFrag = true;
                    for (int i = 0; i < itemArr.length; i++){
                        if (fragItem.getItem().equals(itemArr[i])){
                            deleteFrag = false;
                            break;
                        }
                    }
                    if (deleteFrag){
                        DataSupport.deleteAll(FragItem.class, "date = ? and item = ?", String.valueOf(note.getTimeId()), fragItem.getItem());
                    }
                }
                Intent intent1 = new Intent(EditNoteActivity.this, DateRemindService.class);
                startService(intent1);
                if (note.getNoteType() == FormatUtil.DATE_TYPE){
                    List<NoteItem> noteItemList = DataSupport.where("date = ?", String.valueOf(note.getTimeId())).find(NoteItem.class);
                    if (noteItemList.size() != 0){
                        for (int i = 0; i < noteItemList.size(); i++){
                            boolean temp = true;
                            for (int j = 0; j < itemArr.length; j++){
                                if (noteItemList.get(i).getItem().equals(itemArr[j])){
                                    temp = false;
                                    break;
                                }
                            }
                            if (temp){
                                DataSupport.deleteAll(NoteItem.class, "date = ? and item = ?", String.valueOf(note.getTimeId()), noteItemList.get(i).getItem());
                            }
                        }
                    }
                    Intent intent = new Intent(EditNoteActivity.this, RemindService.class);
                    intent.putExtra("frag", 1);
                    startService(intent);
                }
                Toast.makeText(EditNoteActivity.this, "编辑成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void takePhoto() {
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this, "com.example.cameraalbumtest.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void choosePhoto() {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    mImagePath = imageUri.getPath();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        mImagePath = imagePath;
        note.setImagePath(mImagePath);
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

}
