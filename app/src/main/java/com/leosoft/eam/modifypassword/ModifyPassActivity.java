package com.leosoft.eam.modifypassword;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.leosoft.eam.R;

public class ModifyPassActivity extends AppCompatActivity {

    private ImageButton imgBtnModifyPassBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pass);

        imgBtnModifyPassBack = (ImageButton) findViewById(R.id.imageButtonModifyPassBack);

        getFragmentManager().beginTransaction()
                .add(R.id.container_modify_pass, new ModifyPassFragment())
                .commit();

        imgBtnModifyPassBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
