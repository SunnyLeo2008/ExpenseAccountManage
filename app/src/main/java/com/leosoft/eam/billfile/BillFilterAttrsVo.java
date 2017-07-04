package com.leosoft.eam.billfile;

import java.io.Serializable;

/**
 * Created by Leo on 2017-06-11.
 */

public class BillFilterAttrsVo implements Serializable {

    private String value;
    private boolean isChecked;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
