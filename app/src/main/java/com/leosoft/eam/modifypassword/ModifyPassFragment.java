package com.leosoft.eam.modifypassword;

import android.app.Fragment;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leosoft.eam.login.PassCodeView;
import com.leosoft.eam.R;
import com.leosoft.eam.utils.DatabaseAccess;
import com.leosoft.eam.utils.EncryptUtils;
import com.leosoft.eam.utils.InfoText;

import org.jetbrains.annotations.Nullable;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Leo on 2017-05-11.
 */

public class ModifyPassFragment extends Fragment {

    private String oldPASSCODE = "";
    private String newPASSCODE = "";
    private String encryptMD5String = "";

    private PassCodeView passCodeView;
    private TextView tvModifyPassHint;

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
            oldPASSCODE = cursor.getString(0);
        }
        cursor.close();
        databaseAccess.close();


        tvModifyPassHint = (TextView) getActivity().findViewById(R.id.modify_pass_hint);

        bindEvents(0);
        return mRootView;
    }



    /*flagOperator=0 校验原密码
    flagOperator=1 输入新密码
    flagOperator=2 校验新密码*/
    private void bindEvents(final int flagOperator) {
        passCodeView.setOnTextChangeListener(new PassCodeView.TextChangeListener() {
            @Override public void onTextChanged(String text) {

                if (text.length() == 4) {
                    switch(flagOperator){
                        case 0: {
                            encryptMD5String = EncryptUtils.encryptMD5ToString(text);
                            if (encryptMD5String.equals(oldPASSCODE)) {

                                tvModifyPassHint.setText(InfoText.MODIFY_PASSWORD_HINT_1);

                                passCodeView.reset();

                                bindEvents(1);
                            } else {
                                passCodeView.setError(true);
                            }

                            break;
                        }
                        case 1: {
                            newPASSCODE = text;
                            passCodeView.reset();

                            tvModifyPassHint.setText(InfoText.MODIFY_PASSWORD_HINT_2);

                            bindEvents(2);

                            break;
                        }
                        default: {
                            if (newPASSCODE.equals(text)) {
                                encryptMD5String = EncryptUtils.encryptMD5ToString(text);

                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
                                databaseAccess.open();

                                String SQLStr = "UPDATE T_SYSINFO SET PASSWORD='" + encryptMD5String + "'";

                                SQLStr = databaseAccess.ddlSQL(SQLStr, false);
                                if (!SQLStr.isEmpty()) {
                                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(InfoText.TEXT_ERROR_TITLE)
                                            .setContentText(SQLStr)
                                            .setConfirmText(InfoText.TEXT_OK)
                                            .show();
                                } else {
                                    getActivity().finish();
                                }
                            } else {
                                passCodeView.setError(true);

                                tvModifyPassHint.setText(InfoText.MODIFY_PASSWORD_HINT_1);

                                bindEvents(1);
                            }

                            break;
                        }
                    }
                }
            }
        });
    }
}
