package com.ville.homeland.ui;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.ville.homeland.R;
import com.ville.homeland.view.VideoListFragment;

public class QueryProgramsResultsActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_frame);
		
		String key = getIntent().getStringExtra("query_key");
		
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("\""+ key + "\" 的搜索结果" );
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.content_frame, VideoListFragment.newInstance(VideoListFragment.MODE_QUERY, 0, "", key, null, 0))
		.commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		}
		return true;
	}
}
