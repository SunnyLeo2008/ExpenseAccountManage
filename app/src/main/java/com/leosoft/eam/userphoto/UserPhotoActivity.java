package com.leosoft.eam.userphoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.leosoft.eam.R;
import com.leosoft.eam.utils.InfoText;
import com.leosoft.eam.utils.ShapedImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Leo on 2017-06-04.
 */

public class UserPhotoActivity extends AppCompatActivity {

    private ShapedImageView mCropView;
    private ImageButton imageButtonUserPhotoBack;
    private Button btnUserPhotoSave;
    private Button btnUserPhotoCamera;
    private Button btnUserPhotoGallery;
    private Button btnUserPhotoDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_photo);

        mCropView = (ShapedImageView) findViewById(R.id.imageCropView);
        imageButtonUserPhotoBack = (ImageButton) findViewById(R.id.imageButtonUserPhotoBack);
        btnUserPhotoSave =  (Button) findViewById(R.id.user_photo_save);
        btnUserPhotoCamera =  (Button) findViewById(R.id.startCameraBtn);
        btnUserPhotoGallery =  (Button) findViewById(R.id.startGalleryBtn);
        btnUserPhotoDefault =  (Button) findViewById(R.id.goDefaultBtn);

        File file = new File(getApplicationContext().getFilesDir() + "/images","user_photo.png");
        if (!file.exists()) {
            //设置显示的客户图片，默认显示User
            mCropView.setImageResource(R.drawable.user);
        } else {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(getApplicationContext().getFilesDir().getAbsoluteFile() + "/images/user_photo.png");
                if (bitmap != null) {
                    mCropView.setImageBitmap(bitmap);
                } else {
                    mCropView.setImageResource(R.drawable.user);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        imageButtonUserPhotoBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                setResult(InfoText.RESULT_USER_PHOTO_BACK, intent);
                finish();
            }
        });

        btnUserPhotoSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCropView.setDrawingCacheEnabled(true);
                Bitmap tmpBitmap = Bitmap.createBitmap(mCropView.getDrawingCache());
                mCropView.setDrawingCacheEnabled(false);

                SavePicToFile(tmpBitmap);

                Intent intent=new Intent();
                setResult(InfoText.RESULT_USER_PHOTO_SAVE, intent);
                finish();
            }
        });

        btnUserPhotoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new PickerBuilder(UserPhotoActivity.this, PickerBuilder.SELECT_FROM_CAMERA)
                        .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                            @Override
                            public void onImageReceived(Uri imageUri) {
                                mCropView.setImageURI(imageUri);
                            }
                        })
                        .withTimeStamp(false)
                        .setCropScreenColor(Color.CYAN)
                        .start();
            }
        });

        btnUserPhotoGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PickerBuilder(UserPhotoActivity.this, PickerBuilder.SELECT_FROM_GALLERY)
                        .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                            @Override
                            public void onImageReceived(Uri imageUri) {
                                mCropView.setImageURI(imageUri);
                            }
                        })
                        .setCropScreenColor(Color.CYAN)
                        .setOnPermissionRefusedListener(new PickerBuilder.onPermissionRefusedListener() {
                            @Override
                            public void onPermissionRefused() {

                            }
                        })
                        .start();
            }
        });

        btnUserPhotoDefault.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new SweetAlertDialog(UserPhotoActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(InfoText.TEXT_ASK_TITLE)
                        .setContentText(InfoText.TEXT_ASK_DEFAULT)
                        .setCancelText(InfoText.TEXT_CANCEL)
                        .setConfirmText(InfoText.TEXT_CONFIRM)
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                File file = new File(getApplicationContext().getFilesDir() + "/images","user_photo.png");
                                if (file.exists()) {
                                    file.delete();
                                }

                                mCropView.setImageResource(R.drawable.user);

                                sDialog.dismissWithAnimation();

                                Intent intent=new Intent();
                                setResult(InfoText.RESULT_USER_PHOTO_SAVE, intent);
                                finish();
                            }
                        })
                        .show();
            }
        });
    }

    private void SavePicToFile(Bitmap mBitmap) {

        File file = new File(getApplicationContext().getFilesDir() + "/images","user_photo.png");

        if (mBitmap != null) {
            String fileName = getApplicationContext().getFilesDir().getAbsolutePath() + "/images/user_photo.png";
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(fileName);

                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            file.delete();
        }
    }


}
