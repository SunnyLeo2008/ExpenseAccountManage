package com.leosoft.eam.userphoto;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

/**
 * Created by Mickael on 10/10/2016.
 */

public class CameraPickerManager extends com.leosoft.eam.userphoto.PickerManager {

    public CameraPickerManager(Activity activity) {
        super(activity);
    }

    protected void sendToExternalApp() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        mProcessingPhotoUri = getImageFile(false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getImageFile(true));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        activity.startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }
}
