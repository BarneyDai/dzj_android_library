/**
  * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * 		Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.dzj.app.xlistview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;

import java.text.SimpleDateFormat;
import java.util.Date;

public class XListView extends ListView implements OnScrollListener {

	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	// the interface to trigger refresh and load more.
	private IXListViewListener mListViewListener;

	// -- header view
	private XListViewHeader mHeaderView;
	private boolean mEnablePullRefresh = false; // default no
	private boolean mPullRefreshing = false; // is refreashing.

	// -- footer view
	private XListViewFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;

	// total list items, used to detect is at the bottom of listview.
	private int mTotalItemCount;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400; // scroll back duration
	private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
														// at bottom, trigger
														// load more.
	private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
													// feature.

	private float xDistance, yDistance, lastX, lastY; // for
														// onInterceptTouchEvent
														// for child view scroll
	private float mSlop;

	private View mEmptyView;
	private Drawable mOrginBack;
	//标志是否是上拉刷新或者下拉加载更多
	public  boolean refrashState;
	
	public boolean isEnableRefresh;

	// // -- header load or refresh
	// private boolean mHeaderPullRefresh = false;

	
	
	/**
	 * @param context
	 */
	public XListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public boolean isEnableRefresh() {
		return isEnableRefresh;
	}

	public void setEnableRefresh(boolean isEnableRefresh) {
		this.isEnableRefresh = isEnableRefresh;
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		// XListView need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		super.setOnScrollListener(this);

		// init header view
		mHeaderView = new XListViewHeader(context);
		addHeaderView(mHeaderView);

		// init footer view
		mFooterView = new XListViewFooter(context);

		mSlop = ViewConfigurationCompat
				.getScaledPagingTouchSlop(ViewConfiguration.get(context));
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// make sure XListViewFooter is the last footer view, and only add once.
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
			// 默认不显示
			mFooterView.hide();
		}
		super.setAdapter(adapter);
	}

	/**
	 * enable or disable pull down refresh feature.
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderView.setVisibility(View.INVISIBLE);
		} else {
			mHeaderView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * enable or disable pull up load more feature.
	 * 
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			 mFooterView.hide();
			mFooterView.setOnClickListener(null);
		} else {
			mPullLoading = false;
			// mFooterView.show();
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	// /**
	// * set header pull is refresh or loadmore
	// *
	// * @param set
	// */
	// public void setHeaderPullRefresh(boolean set) {
	// mHeaderPullRefresh = set;
	// }
	
	
	@Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (IndexOutOfBoundsException e) {
            // samsung error IndexOutOfBoundException in HeaderViewListAdapter
        }
    }

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			setRefreshTime(new SimpleDateFormat().format(new Date()));
			mPullRefreshing = false;
			resetHeaderHeight();
		}
	}

	/**
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
		}
	}

	/**
	 * 有时需要手动显示下拉刷新 调用此方法
	 */
	public void showHeadViewForRefresh() {
		post(new Runnable() {
			
			@Override
			public void run() {
				if (mEnablePullRefresh && !mPullRefreshing) {
					mPullRefreshing = true;
					mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
					int height = mHeaderView.getVisiableHeight();
					mScrollBack = SCROLLBACK_HEADER;
					mScroller.startScroll(0, height, 0, mHeaderView.getOriginHeight()
							- height, SCROLL_DURATION);
					// trigger computeScroll
					invalidate();
					if (mListViewListener != null) {
						mListViewListener.onRefresh();
					}
					

				}
			}
		});
		
	}
	
	
	
	public void noHeadViewForRefresh(){
		if (mEnablePullRefresh && !mPullRefreshing) {
			mPullRefreshing = true;
//			CourseClassfyAdapter.isEnableRefresh = false;
			invalidate();
			if (mListViewListener != null) {
				mListViewListener.onRefresh();
			}
		}
	}
	/**
	 * 无头刷新
	 */
	public void hideHeadViewForRefresh() {
		if (mEnablePullRefresh && !mPullRefreshing) {
			
			if (mListViewListener != null) {
				mListViewListener.onRefresh();
			}

		}
	}
	/**
	 * set last refresh time
	 * 
	 * @param time
	 */
	public void setRefreshTime(String time) {
		mHeaderView.setRefreshTime(time);
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta
				+ mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderView.getOriginHeight()) {
				mHeaderView.setState(XListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(XListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0); // scroll to top each time
	}

	/**
	 * reset header view's height.
	 */
	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		// height = 0 still need reset
		// if (height == 0) // not visible.
		// return;
		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && height <= mHeaderView.getOriginHeight()) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && height > mHeaderView.getOriginHeight()) {
			finalHeight = mHeaderView.getOriginHeight();
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		// trigger computeScroll
		invalidate();
	}

	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
													// more.
				mFooterView.setState(XListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(XListViewFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);

		// setSelection(mTotalItemCount - 1); // scroll to bottom
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	private void startLoadMore() {
		mPullLoading = true;

		if (mListViewListener != null && mListViewListener.hasMore()) {
			mFooterView.setState(XListViewFooter.STATE_LOADING);
			mListViewListener.onLoadMore();

		} else {
			mFooterView.hide();
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
		}
		
	}

	public void serFootHide(){
		mFooterView.hide();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (mEnablePullRefresh && getFirstVisiblePosition() == 0
					&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				// the first item is showing, header has shown or pull down.
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				invokeOnScrolling();
			} else if (mEnablePullLoad
					&& getLastVisiblePosition() == mTotalItemCount - 2) {
				if (mListViewListener != null && mListViewListener.hasMore()) {
					mFooterView.show();
				} else {
					mFooterView.hide();
				}
			} else if (mEnablePullLoad
					&& getLastVisiblePosition() == mTotalItemCount - 1
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				// last item, already pulled up or want to pull up.
				if (mListViewListener != null && mListViewListener.hasMore()) {
					updateFooterHeight(-deltaY / OFFSET_RADIO);
				} else {
					mFooterView.hide();
				}
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			refrashState=true;
			mLastY = -1; // reset
			if (getFirstVisiblePosition() == 0) {
				// invoke refresh
				if (mEnablePullRefresh
						&& mHeaderView.getVisiableHeight() > mHeaderView
								.getOriginHeight()) {
					mPullRefreshing = true;
					mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
					if (mListViewListener != null) {
						mListViewListener.onRefresh();
					}
				}
				resetHeaderHeight();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				// invoke load more.
				if (mEnablePullLoad
						&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA
						&& !mPullLoading) {
					startLoadMore();
				}
				resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
//			postInvalidate();
			invalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
		
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// send to user's listener
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	/**
	 * as super.setEmptyView no support pull must call before setAdapter
	 */
	@SuppressLint("NewApi")
	@Override
	public void setEmptyView(View emptyView) {
		mEmptyView = emptyView;

		// If not explicitly specified this view is important for accessibility.
		if (Build.VERSION.SDK_INT > 15) {
			if (emptyView != null
					&& emptyView.getImportantForAccessibility() == IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
				emptyView
						.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
			}
		}

		// addFooterView(mEmptyView);
		mOrginBack = getBackground();
		final ListAdapter adapter = getAdapter();
		final boolean empty = ((adapter == null) || adapter.isEmpty())
				&& !mPullLoading && !mPullRefreshing;
		updateEmptyStatus(empty);
	}

	@Override
	public void requestLayout() {
		final ListAdapter adapter = getAdapter();
		final boolean empty = ((adapter == null) || adapter.isEmpty())
				&& !mPullLoading && !mPullRefreshing;
		updateEmptyStatus(empty);
		super.requestLayout();
	}

	/**
	 * Update the status of the list based on the empty parameter. If empty is
	 * true and we have an empty view, display it. In all the other cases, make
	 * sure that the listview is VISIBLE and that the empty view is GONE (if
	 * it's not null).
	 */
	private void updateEmptyStatus(boolean empty) {
		if (isInFilterMode()) {
			empty = false;
		}

		if (empty) {
			if (mEmptyView != null) {
				mEmptyView.setVisibility(View.VISIBLE);
				setBackgroundResource(android.R.color.transparent);
			} else {
			}
		} else {
			if (mEmptyView != null) {
				mEmptyView.setVisibility(View.GONE);
				setBackgroundResource(android.R.color.transparent);
			}
		}
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	/**
	 * you can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * implements this interface to get refresh/load more event.
	 */
	public interface IXListViewListener {
		public void onRefresh();

		public void onLoadMore();

		// is or not has more data
		public boolean hasMore();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			lastX = ev.getX();
			lastY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();
			xDistance = Math.abs(curX - lastX);
			yDistance = Math.abs(curY - lastY);

			if (mSlop > xDistance && mSlop > yDistance) {
				break;
			}

			lastX = curX;
			lastY = curY;

			// 对于横向scroll，不Intercept
			if (xDistance > yDistance)
				return false;
		}
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * 
	 * @function 显示listview foot
	 * @author liping
	 * @Date 2015-1-13
	 */
	public void showListViewFooter(){
		mFooterView.show();
	}
	
	public void lock() {

	}

}
