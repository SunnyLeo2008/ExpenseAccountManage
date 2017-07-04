package com.leosoft.eam.login;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.leosoft.eam.R;
import com.leosoft.eam.utils.ShapedImageView;

import java.io.File;

public class LoginActivity extends Activity {

    private ShapedImageView imageLoginUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imageLoginUser = (ShapedImageView) findViewById(R.id.imageLogin);

        File file = new File(getApplicationContext().getFilesDir() + "/images","user_photo.png");
        if (!file.exists()) {
            //设置显示的客户图片，默认显示User
            imageLoginUser.setImageResource(R.drawable.user);
        } else {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(getApplicationContext().getFilesDir().getAbsoluteFile() + "/images/user_photo.png");
                if (bitmap != null) {
                    imageLoginUser.setImageBitmap(bitmap);
                } else {
                    imageLoginUser.setImageResource(R.drawable.user);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        getFragmentManager().beginTransaction()
                .add(R.id.container_login, new LoginFragment())
                .commit();
    }

}
