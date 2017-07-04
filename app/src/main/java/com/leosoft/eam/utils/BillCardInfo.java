package com.leosoft.eam.utils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Leo on 2017-05-24.
 */

public class BillCardInfo implements Serializable {

    private String mCardViewJNL = "";
    private String mCardViewBillType = "";
    private String mCardViewBillChildType = "";
    private String mCardViewBillSum = "";
    private String mCardViewBillDate = "";
    private String mCardViewBillMemo = "";
    private String mCardViewBillCreateDate = "";
    private String mCardViewBillDealDate = "";
    private String mCardViewPhotoID = "default.png";

    public BillCardInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date curDate = new Date(System.currentTimeMillis());
        this.mCardViewJNL = formatter.format(curDate);
    }

    public BillCardInfo(String mCardViewJNL,String mCardViewBillType, String mCardViewBillChildType,String mCardViewBillSum, String mCardViewBillDate, String mCardViewBillCreateDate, String mCardViewBillDealDate,String mCardViewBillMemo, String mCardViewPhotoID) {
        this.mCardViewJNL = mCardViewJNL;
        this.mCardViewBillType = mCardViewBillType;
        this.mCardViewBillChildType = mCardViewBillChildType;
        this.mCardViewBillSum = mCardViewBillSum;
        this.mCardViewBillDate = mCardViewBillDate;
        this.mCardViewBillMemo = mCardViewBillMemo;
        this.mCardViewPhotoID = mCardViewPhotoID;
        this.mCardViewBillCreateDate = mCardViewBillCreateDate;
        this.mCardViewBillDealDate = mCardViewBillDealDate;
    }

    public void setmCardViewBillType(String mCardViewBillType) {
        this.mCardViewBillType = mCardViewBillType;
    }

    public void setmCardViewBillSum(String mCardViewBillSum) {
        this.mCardViewBillSum = mCardViewBillSum;
    }

    public void setmCardViewBillDate(String mCardViewBillDate) {
        this.mCardViewBillDate = mCardViewBillDate;
    }

    public void setmCardViewBillMemo(String mCardViewBillMemo) {
        this.mCardViewBillMemo = mCardViewBillMemo;
    }

    public void setmCardViewPhotoID(String mCardViewPhotoID) {
        this.mCardViewPhotoID = mCardViewPhotoID;
    }

    public void setmCardViewBillCreateDate(String mCardViewBillCreateDate) {
        this.mCardViewBillCreateDate = mCardViewBillCreateDate;
    }

    public void setmCardViewBillDealDate(String mCardViewBillDealDate) {
        this.mCardViewBillDealDate = mCardViewBillDealDate;
    }

    public void setmCardViewJNL(String mCardViewJNL) {
        if (mCardViewJNL.isEmpty()){
            //取当前系统日期
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            Date curDate = new Date(System.currentTimeMillis());
            this.mCardViewJNL = formatter.format(curDate);
        } else {
            this.mCardViewJNL = mCardViewJNL;
        }
    }

    public void setmCardViewBillChildType(String mCardViewBillChildType) {
        this.mCardViewBillChildType = mCardViewBillChildType;
    }

    public String getmCardViewBillType() {
        return mCardViewBillType;
    }

    public String getmCardViewBillSum() {
        return mCardViewBillSum;
    }

    public String getmCardViewBillDate() {
        return mCardViewBillDate;
    }

    public String getmCardViewBillMemo() {
        return mCardViewBillMemo;
    }

    public String getmCardViewPhotoID() {
        return mCardViewPhotoID;
    }

    public String getmCardViewBillCreateDate() {
        return mCardViewBillCreateDate;
    }

    public String getmCardViewBillDealDate() {
        return mCardViewBillDealDate;
    }

    public String getmCardViewJNL() {
        return mCardViewJNL;
    }

    public String getmCardViewBillChildType() {
        return mCardViewBillChildType;
    }
}
