/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ville.homeland.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.example.android.bitmapfun.util.ImageFetcher;
import com.example.android.bitmapfun.util.ImageWorker;
import com.polites.android.GestureImageView;
import com.ville.homeland.R;
import com.ville.homeland.ui.ImageDetailActivity;
import com.ville.homeland.util.GifLoader;
import com.ville.homeland.widget.GifMovieView;

/**
 * This fragment will populate the children of the ViewPager from {@link ImageDetailActivity}.
 */
public class GifImageDetailFragment extends Fragment {
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private String mImageUrl;
    private GifMovieView mGifView;
    private GifLoader mGifLoader;

    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param imageUrl The image url to load
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
    public static GifImageDetailFragment newInstance(String imageUrl) {
        final GifImageDetailFragment f = new GifImageDetailFragment();
        
        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageUrl);
        f.setArguments(args);
        
        return f;
    }

    public GifImageDetailFragment() {
    }

    /**
     * Populate image using a url from extras, use the convenience factory method
     * {@link GifImageDetailFragment#newInstance(String)} to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;
        mGifLoader = new GifLoader(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.gif_detail_fragment, container, false);
        mGifView = (GifMovieView) v.findViewById(R.id.imageView);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //This is Very very Important For findViewByTag in ImageDetailActivity
        //Here getView() ,we get NoSaveStateFrameLayout
        getView().setTag(mImageUrl);
        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (ImageDetailActivity.class.isInstance(getActivity())) {
        	mGifLoader.loadGifView(mImageUrl, mGifView);
        }

        // Pass clicks on the ImageView to the parent activity to handle
        if (OnClickListener.class.isInstance(getActivity())) {
            mGifView.setOnClickListener((OnClickListener) getActivity());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
