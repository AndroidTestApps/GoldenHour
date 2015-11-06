package com.hebertwilliams.goldenhour;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.hebertwilliams.goldenhour.api.FlickrApiUtility;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by kylehebert on 10/25/15. Used to start a background thread
 * for downloading Flickr thumbnails.
 */
public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    private Handler mRequestHandler;
    //ConcurrentMap is a thread-safe version of HashMap
    private ConcurrentMap<T, String> mRequestMap  = new ConcurrentHashMap<>();

    private Handler mResponseHandler; //used to send messages back to the main thread
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    /*
    called when an image has been fully downloaded and is ready to
    be added to the UI
     */
    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
        public void handleMessage(Message message) {
                if (message.what == MESSAGE_DOWNLOAD) {
                    T target = (T) message.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL: " + url);

        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target,url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD,target)
                    .sendToTarget();
        }
    }

    /*
    cleans all of the requests out of the Handler, gets called in a GalleryFragment's
    onDestroyView()
     */
    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }


    /*
    This is where the image downloading occurs. First checks for a url, then uses
    FlickrApiUtility.getUrlBytes. The bytes are used by BitmapFactory to construct
    the bitmap.
     */
    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);

            if (url == null) {
                return;
            }

            byte[] bitmapBytes = new FlickrApiUtility().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes,0,bitmapBytes.length);
            Log.i(TAG, "Bitmap created");

            //run this on the main thread directly
            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    /*
                    make sure that by the time the download finishes the PhotoHolder
                    gets the correct image, even if the RecyclerView has recycled it and
                    made a new request
                     */
                    if (mRequestMap.get(target) != url) {
                        return;
                    }

                    mRequestMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target,bitmap);
                }
            });

        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }




}
