package com.zerocodeteam.network.request;

import android.graphics.Bitmap;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyLog;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by ZeroCodeTeam on 23.7.2015.
 */
public class PhotoUploadRequest<T> extends GsonRequest<T> {
    private static final String FILE_PART_NAME = "file";

    private MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();
    private final Bitmap mImage;
    private final String mImageName;

    public PhotoUploadRequest(Object cookie, String url, Class<T> clazz, Map<String, String> headers, Bitmap image, String imageName, ResponseListener<T> listener) {
        super(cookie, Method.POST, url, clazz, headers, null, listener);
        mImage = image;
        mImageName = imageName;
        buildMultipartEntity();
    }

    private void buildMultipartEntity() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        mBuilder.addBinaryBody(FILE_PART_NAME, byteArray, ContentType.create("image/jpeg"), mImageName);
        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
    }

    @Override
    public String getBodyContentType() {
        String contentTypeHeader = mBuilder.build().getContentType().getValue();
        return contentTypeHeader;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mBuilder.build().writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.");
        }
        return bos.toByteArray();
    }
}
