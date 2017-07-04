package com.leosoft.eam.login;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leosoft.eam.MainActivity;
import com.leosoft.eam.R;
import com.leosoft.eam.utils.DatabaseAccess;
import com.leosoft.eam.utils.EncryptUtils;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Leo on 2017-05-11.
 */

public class LoginFragment extends Fragment {

    private String PASSCODE = "";
    private PassCodeView passCodeView;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_login, container, false);
        passCodeView = (PassCodeView) mRootView.findViewById(R.id.pass_code_view);
        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Font-Bold.ttf");
        passCodeView.setTypeFace(typeFace);
        passCodeView.setKeyTextColor(R.color.colorBlackShade);
        passCodeView.setEmptyDrawable(R.drawable.empty_dot);
        passCodeView.setFilledDrawable(R.drawable.filled_dot);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();
        Cursor cursor = databaseAccess.querySQL("SELECT PASSWORD FROM T_SYSINFO");
        if(cursor!=null && cursor.moveToFirst()){
            PASSCODE = cursor.getString(0);
        }
        cursor.close();
        databaseAccess.close();

        bindEvents();
        return mRootView;
    }

    private void bindEvents() {
        passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override public void onTextChanged(String text) {
                String encryptMD5String = "";
                if (text.length() == 4) {
                    encryptMD5String = EncryptUtils.encryptMD5ToString(text);
                    if (encryptMD5String.equals(PASSCODE)) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        passCodeView.setError(true);
                    }
                }
            }
        });
    }
}
