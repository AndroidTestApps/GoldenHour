package com.hebertwilliams.goldenhour;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by kylehebert on 11/8/15. Fragment for displaying the details
 * of an indvidual photo.
 */
public class PhotoPageFragment extends Fragment {

    private static final String ARG_URI = "photo_page_url";

    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static PhotoPageFragment newInstance(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);

        PhotoPageFragment fragment = new PhotoPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUri = getArguments().getParcelable(ARG_URI);
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_page, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.fragment_photo_page_progress_bar);
        mProgressBar.setMax(100);

        mWebView = (WebView) view.findViewById(R.id.fragment_photo_page_web_view);
        //we need JavaScript to render the Flickr photo page
        mWebView.getSettings().setJavaScriptEnabled(true);

        //once progress is 100 the page is done loading so hide the progress bar
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView webView, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            public void onReceivedTitle(WebView webView, String title) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
                appCompatActivity.getSupportActionBar().setSubtitle(title);
            }
        });
        //override URLLoading to prevent being re-directed to Flickr's mobile view
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                return false;
            }
        });
        mWebView.loadUrl(mUri.toString());

        return view;
    }
}
