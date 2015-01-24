/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.gmailapptest.contentprovidertest;



import com.example.gmailapptest.R;
import com.example.gmailapptest.contentprovider.GmailContract;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;


public class LabelDetailsActivity extends FragmentActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, LabelListFragment.ItemClickedListener {
    public static final String LABEL_URI_EXTRA = "labelUri";

    static final String[] COLUMNS_TO_SHOW = new String[] {
            GmailContract.Labels.NAME,
            GmailContract.Labels.CANONICAL_NAME,
            GmailContract.Labels.NUM_CONVERSATIONS,
            GmailContract.Labels.NUM_UNREAD_CONVERSATIONS,
            GmailContract.Labels.URI,
            GmailContract.Labels.BACKGROUND_COLOR,
            GmailContract.Labels.TEXT_COLOR };

    static final int[] LAYOUT_ITEMS = new int[] {
            R.id.name_entry,
            R.id.canonical_name_entry,
            R.id.number_entry,
            R.id.unread_count_number_entry,
            R.id.uri_entry,
            R.id.background_color_entry,
            R.id.text_color_entry};

    private Uri mLabelUri;
    private LabelListFragment mFragment;
    private SimpleCursorAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.activity_main);

        final Intent intent = getIntent();

        mLabelUri = (Uri)intent.getParcelableExtra(LABEL_URI_EXTRA);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        // There is only one fragment
        mFragment = (LabelListFragment)fragment;
        mFragment.setItemClickedListener(this);

        mAdapter = new SimpleCursorAdapter(this, R.layout.label_details_item, null,
                COLUMNS_TO_SHOW, LAYOUT_ITEMS);
        mFragment.setListAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        final Bundle args = new Bundle();
        args.putParcelable("labelUri", mLabelUri);
        getSupportLoaderManager().restartLoader(0, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Uri labelUri = (Uri)args.getParcelable("labelUri");
        return new CursorLoader(this, labelUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClicked(int position) {
        // Get the cursor from the adapter
        final Cursor cursor = mAdapter.getCursor();

        cursor.moveToPosition(position);

        // get the uri
        final Uri labelUri = Uri.parse(
                cursor.getString(cursor.getColumnIndex(GmailContract.Labels.URI)));

        final Intent intent = new Intent(Intent.ACTION_VIEW, labelUri);
        startActivity(intent);
    }
}