package me.mobilelin.meizu.demo;

import me.mobilelin.meizu.lib.PagerSlidingStrip;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	private ViewPager mViewPager;
	private PagerSlidingStrip mPagerSlidingStrip;
	private LinearLayout mLinearLayout;
	private ImageView mImageView;
	private View mCustomView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mCustomView = LayoutInflater.from(this).inflate(
				R.layout.custom_tab_view, null);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mPagerSlidingStrip = (PagerSlidingStrip) mCustomView
				.findViewById(R.id.tabs);
		mImageView = (ImageView) mCustomView.findViewById(R.id.iv_actionbar_up);
		mLinearLayout = (LinearLayout) mCustomView.findViewById(R.id.ll_home);

		getActionBar().setCustomView(mCustomView);
		mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

		// Tell mPagerSlidingStrip which viewpager you are using
		mPagerSlidingStrip.setViewPager(mViewPager);
		setListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		int actionBarOptions = getActionBar().getDisplayOptions();
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM | actionBarOptions);
	}

	private void setListener() {
		mLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Toggle the drawer!",
						Toast.LENGTH_SHORT).show();
				mImageView.setVisibility(isDrawerOpened() ? View.VISIBLE
						: View.INVISIBLE);
			}

			private boolean isDrawerOpened() {
				// do something here!
				return true;
			}
		});
	}

	class PagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "Short", "Loooooooong" };

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return DummyFragment.newInstance(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

	}

}
