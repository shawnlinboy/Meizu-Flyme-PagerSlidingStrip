package me.mobilelin.meizu.lib;

import java.util.Locale;

import me.mobilelin.meizu.demo.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This class was initially created by astuetz(andreas.stuetz@gmail.com), and
 * was modified by 单线程的Shawn(mobilelin@mobilelin.me) for styling like Meizu
 * Flyme 3.0.
 * 
 * Please do me the honor of expressing my heartfelt respect for the original
 * author!
 * 
 * @author mobilelin
 * @date 2014-03-05
 * 
 */
public class PagerSlidingStrip extends HorizontalScrollView {

	// @formatter:off
	private static final int[] mArrayTextSystemAttr = new int[] {
			android.R.attr.textSize, android.R.attr.textColor };
	// @formatter:on

	private LinearLayout.LayoutParams defaultTabLayoutParams;
	private LinearLayout.LayoutParams expandedTabLayoutParams;

	private final PageListener pageListener = new PageListener();
	public OnPageChangeListener delegatePageListener;

	private LinearLayout mTabContainer;
	private ViewPager mViewPager;

	private int mTabCount;

	private int mPagerPosition = 0;
	private float mPagerPositionOffset = 0f;

	private Paint mPaintRect;
	private Paint mPaintDivider;

	private int mIndicatorColor = 0xFF666666;
	private int mUnderlineColor = 0x1A000000;
	private int mDividerColor = 0x1A000000;

	private boolean mShouldExpand = false;
	private boolean mShouldTextCaps = false;

	private int mScrollOffset = 52;
	private int mIndicatorHeight = 3;
	private int mUnderlineHeight = 0;
	private int mDividerPadding = 13;
	private int mTabPadding = 16;
	private int mDividerWidth = 1;

	private int mTabTextSize = 15;
	private int mTabTextColor = Color.BLACK;
	private Typeface mTypefaceTab = null;
	private int mTabStyle = Typeface.NORMAL;

	private int lastScrollX = 0;

	private int mTabBackground = R.drawable.background_tab;

	private Locale mLocale;

	public PagerSlidingStrip(Context context) {
		this(context, null);
	}

	public PagerSlidingStrip(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PagerSlidingStrip(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setFillViewport(true);
		setWillNotDraw(false);

		mTabContainer = new LinearLayout(context);
		mTabContainer.setOrientation(LinearLayout.HORIZONTAL);
		mTabContainer.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(mTabContainer);

		DisplayMetrics dm = getResources().getDisplayMetrics();

		mScrollOffset = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mScrollOffset, dm);
		mIndicatorHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mIndicatorHeight, dm);
		mUnderlineHeight = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mUnderlineHeight, dm);
		mDividerPadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mDividerPadding, dm);
		mTabPadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mTabPadding, dm);
		mDividerWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mDividerWidth, dm);
		mTabTextSize = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, mTabTextSize, dm);

		// get system attrs (android:textSize and android:textColor)

		TypedArray a = context.obtainStyledAttributes(attrs,
				mArrayTextSystemAttr);

		mTabTextSize = a.getDimensionPixelSize(0, mTabTextSize);
		mTabTextColor = a.getColor(1, mTabTextColor);

		a.recycle();

		// get custom attrs

		a = context
				.obtainStyledAttributes(attrs, R.styleable.PagerSlidingStrip);

		mIndicatorColor = a.getColor(
				R.styleable.PagerSlidingStrip_indicator_color, mIndicatorColor);
		mUnderlineColor = a.getColor(
				R.styleable.PagerSlidingStrip_underline_color, mUnderlineColor);
		mDividerColor = a.getColor(R.styleable.PagerSlidingStrip_divider_color,
				mDividerColor);
		mIndicatorHeight = a.getDimensionPixelSize(
				R.styleable.PagerSlidingStrip_indicator_height,
				mIndicatorHeight);
		mUnderlineHeight = a.getDimensionPixelSize(
				R.styleable.PagerSlidingStrip_underline_height,
				mUnderlineHeight);
		mDividerPadding = a.getDimensionPixelSize(
				R.styleable.PagerSlidingStrip_divider_padding, mDividerPadding);
		mTabPadding = a.getDimensionPixelSize(
				R.styleable.PagerSlidingStrip_tabPaddingLeftRight, mTabPadding);
		mTabBackground = a.getResourceId(
				R.styleable.PagerSlidingStrip_tab_background, mTabBackground);
		mShouldExpand = a.getBoolean(R.styleable.PagerSlidingStrip_expandable,
				mShouldExpand);
		mScrollOffset = a.getDimensionPixelSize(
				R.styleable.PagerSlidingStrip_scrollOffset, mScrollOffset);
		mShouldTextCaps = a.getBoolean(
				R.styleable.PagerSlidingStrip_textAllCaps, mShouldTextCaps);

		a.recycle();

		mPaintRect = new Paint();
		mPaintRect.setAntiAlias(true);
		mPaintRect.setStyle(Style.FILL);

		mPaintDivider = new Paint();
		mPaintDivider.setAntiAlias(true);
		mPaintDivider.setStrokeWidth(mDividerWidth);

		defaultTabLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		expandedTabLayoutParams = new LinearLayout.LayoutParams(0,
				LayoutParams.MATCH_PARENT, 1.0f);

		if (mLocale == null) {
			mLocale = getResources().getConfiguration().locale;
		}
	}

	public void setViewPager(ViewPager pager) {
		this.mViewPager = pager;

		if (pager.getAdapter() == null) {
			throw new IllegalStateException(
					"ViewPager does not have adapter instance.");
		}

		pager.setOnPageChangeListener(pageListener);

		notifyDataSetChanged();
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		this.delegatePageListener = listener;
	}

	public void notifyDataSetChanged() {

		mTabContainer.removeAllViews();

		mTabCount = mViewPager.getAdapter().getCount();

		for (int i = 0; i < mTabCount; i++) {

			addTextTab(i, mViewPager.getAdapter().getPageTitle(i).toString());

		}

		updateTabStyles();

		getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressWarnings("deprecation")
					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {

						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
							getViewTreeObserver().removeGlobalOnLayoutListener(
									this);
						} else {
							getViewTreeObserver().removeOnGlobalLayoutListener(
									this);
						}

						mPagerPosition = mViewPager.getCurrentItem();
						scrollToChild(mPagerPosition, 0);
					}
				});

	}

	private void addTextTab(final int position, String title) {

		TextView tab = new TextView(getContext());
		tab.setText(title);
		tab.setGravity(Gravity.CENTER);
		tab.setSingleLine();

		addTab(position, tab);
	}

	private void addTab(final int position, View tab) {
		tab.setFocusable(true);
		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mViewPager.setCurrentItem(position);
			}
		});

		tab.setPadding(mTabPadding, 0, mTabPadding, 0);
		mTabContainer.addView(tab, position,
				mShouldExpand ? expandedTabLayoutParams
						: defaultTabLayoutParams);
	}

	private void updateTabStyles() {

		for (int i = 0; i < mTabCount; i++) {

			View v = mTabContainer.getChildAt(i);

			v.setBackgroundResource(mTabBackground);

			if (v instanceof TextView) {

				TextView tab = (TextView) v;
				tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
				tab.setTypeface(mTypefaceTab, mTabStyle);
				tab.setTextColor(mTabTextColor);

				// setAllCaps() is only available from API 14, so the upper case
				// is made manually if we are on a
				// pre-ICS-build
				if (mShouldTextCaps) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
						tab.setAllCaps(true);
					} else {
						tab.setText(tab.getText().toString()
								.toUpperCase(mLocale));
					}
				}
			}
		}

	}

	private void scrollToChild(int position, int offset) {

		if (mTabCount == 0) {
			return;
		}

		int newScrollX = mTabContainer.getChildAt(position).getLeft() + offset;

		if (position > 0 || offset > 0) {
			newScrollX -= mScrollOffset;
		}

		if (newScrollX != lastScrollX) {
			lastScrollX = newScrollX;
			scrollTo(newScrollX, 0);
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (isInEditMode() || mTabCount == 0) {
			return;
		}

		final int height = getHeight();

		// draw indicator line

		mPaintRect.setColor(mIndicatorColor);

		// default: line below current tab
		View currentTab = mTabContainer.getChildAt(mPagerPosition);
		float lineLeft = currentTab.getLeft();
		float lineRight = currentTab.getRight();

		// if there is an offset, start interpolating left and right coordinates
		// between current and next tab
		if (mPagerPositionOffset > 0f && mPagerPosition < mTabCount - 1) {

			View nextTab = mTabContainer.getChildAt(mPagerPosition + 1);
			final float nextTabLeft = nextTab.getLeft();
			final float nextTabRight = nextTab.getRight();

			lineLeft = (mPagerPositionOffset * nextTabLeft + (1f - mPagerPositionOffset)
					* lineLeft);
			lineRight = (mPagerPositionOffset * nextTabRight + (1f - mPagerPositionOffset)
					* lineRight);
		}

		canvas.drawRect(lineLeft, height - mIndicatorHeight, lineRight, height,
				mPaintRect);

		// draw underline

		mPaintRect.setColor(mUnderlineColor);
		canvas.drawRect(0, height - mUnderlineHeight, mTabContainer.getWidth(),
				height, mPaintRect);

		// draw divider

		mPaintDivider.setColor(mDividerColor);
		for (int i = 0; i < mTabCount - 1; i++) {
			View tab = mTabContainer.getChildAt(i);
			canvas.drawLine(tab.getRight(), mDividerPadding, tab.getRight(),
					height - mDividerPadding, mPaintDivider);
		}
	}

	private class PageListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {

			mPagerPosition = position;
			mPagerPositionOffset = positionOffset;

			scrollToChild(position, (int) (positionOffset * mTabContainer
					.getChildAt(position).getWidth()));

			invalidate();

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrolled(position, positionOffset,
						positionOffsetPixels);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				scrollToChild(mViewPager.getCurrentItem(), 0);
			}

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrollStateChanged(state);
			}
		}

		@Override
		public void onPageSelected(int position) {
			if (delegatePageListener != null) {
				delegatePageListener.onPageSelected(position);
			}
		}

	}

	public void setIndicatorColor(int indicatorColor) {
		this.mIndicatorColor = indicatorColor;
		invalidate();
	}

	public void setIndicatorColorResource(int resId) {
		this.mIndicatorColor = getResources().getColor(resId);
		invalidate();
	}

	public int getIndicatorColor() {
		return this.mIndicatorColor;
	}

	public void setIndicatorHeight(int indicatorLineHeightPx) {
		this.mIndicatorHeight = indicatorLineHeightPx;
		invalidate();
	}

	public int getIndicatorHeight() {
		return mIndicatorHeight;
	}

	public void setUnderlineColor(int underlineColor) {
		this.mUnderlineColor = underlineColor;
		invalidate();
	}

	public void setUnderlineColorResource(int resId) {
		this.mUnderlineColor = getResources().getColor(resId);
		invalidate();
	}

	public int getUnderlineColor() {
		return mUnderlineColor;
	}

	public void setDividerColor(int dividerColor) {
		this.mDividerColor = dividerColor;
		invalidate();
	}

	public void setDividerColorResource(int resId) {
		this.mDividerColor = getResources().getColor(resId);
		invalidate();
	}

	public int getDividerColor() {
		return mDividerColor;
	}

	public void setUnderlineHeight(int underlineHeightPx) {
		this.mUnderlineHeight = underlineHeightPx;
		invalidate();
	}

	public int getUnderlineHeight() {
		return mUnderlineHeight;
	}

	public void setDividerPadding(int dividerPaddingPx) {
		this.mDividerPadding = dividerPaddingPx;
		invalidate();
	}

	public int getDividerPadding() {
		return mDividerPadding;
	}

	public void setScrollOffset(int scrollOffsetPx) {
		this.mScrollOffset = scrollOffsetPx;
		invalidate();
	}

	public int getScrollOffset() {
		return mScrollOffset;
	}

	public void setShouldExpand(boolean shouldExpand) {
		this.mShouldExpand = shouldExpand;
		requestLayout();
	}

	public boolean getShouldExpand() {
		return mShouldExpand;
	}

	public boolean isTextAllCaps() {
		return mShouldTextCaps;
	}

	public void setAllCaps(boolean textAllCaps) {
		this.mShouldTextCaps = textAllCaps;
	}

	public void setTextSize(int textSizePx) {
		this.mTabTextSize = textSizePx;
		updateTabStyles();
	}

	public int getTextSize() {
		return mTabTextSize;
	}

	public void setTextColor(int textColor) {
		this.mTabTextColor = textColor;
		updateTabStyles();
	}

	public void setTextColorResource(int resId) {
		this.mTabTextColor = getResources().getColor(resId);
		updateTabStyles();
	}

	public int getTextColor() {
		return mTabTextColor;
	}

	public void setTypeface(Typeface typeface, int style) {
		this.mTypefaceTab = typeface;
		this.mTabStyle = style;
		updateTabStyles();
	}

	public void setTabBackground(int resId) {
		this.mTabBackground = resId;
	}

	public int getTabBackground() {
		return mTabBackground;
	}

	public void setTabPaddingLeftRight(int paddingPx) {
		this.mTabPadding = paddingPx;
		updateTabStyles();
	}

	public int getTabPaddingLeftRight() {
		return mTabPadding;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		mPagerPosition = savedState.currentPosition;
		requestLayout();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currentPosition = mPagerPosition;
		return savedState;
	}

	static class SavedState extends BaseSavedState {
		int currentPosition;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentPosition = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(currentPosition);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}
