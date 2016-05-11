package com.dzj.app.basiclibrary.BasicActivity.HeaderActivity.Views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dzj.app.basiclibrary.BasicActivity.HeaderActivity.Listeners.OnTitleBarClickListener;
import com.dzj.app.basiclibrary.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 页面标题
 */
public class HeaderView extends FrameLayout implements OnClickListener {
	private Context context;
	private TextView tvTitle;
	private ImageView imgTitle;
	private ImageButton leftImgBtn,rightImgBtn;
	private TextView tvLeft,tvRight;
	
	private View headView;
	private String leftCallback;
	private String rightCallback;
	private OnTitleBarClickListener onTitleBarClickListener;

	public HeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView(context);
	}

	private void initView(Context context) {
		headView = LayoutInflater.from(context).inflate(R.layout.view_header, this);
		tvLeft = (TextView) headView.findViewById(R.id.left_header_textview);
		leftImgBtn = (ImageButton) headView.findViewById(R.id.back_btn);
		tvTitle = (TextView) headView.findViewById(R.id.center_title_loan_textview);
		imgTitle = (ImageView)headView.findViewById(R.id.center_title_loan_imageview);
		tvRight = (TextView) headView.findViewById(R.id.right_header_textview);
		rightImgBtn = (ImageButton) headView.findViewById(R.id.right_header_btn);
		
		tvLeft.setOnClickListener(this);
		leftImgBtn.setOnClickListener(this);
		tvTitle.setOnClickListener(this);
		tvRight.setOnClickListener(this);
		rightImgBtn.setOnClickListener(this);
	}
	
	
	public void onHeader(String title, String isBack,String backCallback,String rightText, String rightIcon, String rightCallback , String data){
		onHeader(title, isBack, null, backCallback, rightText, rightIcon, rightCallback, data);
	}
	
	public void onHeader(String title, String isBack, Drawable leftDrawalbe,String backCallback,String rightText, String rightIcon, String rightCallback , String data){
		if(!TextUtils.isEmpty(data)){
			try {
				JSONObject dataJson = new JSONObject(data);
				if("true".equals(dataJson.optString("isFullScreen"))){
				      headView.setVisibility(View.GONE);
				      return;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}	
		}
		this.leftCallback = backCallback;
		this.rightCallback = rightCallback;
		setTitle(title);
		if(!TextUtils.isEmpty(rightIcon)){
			if(rightIcon.equalsIgnoreCase("add")){
				setRightIcon(R.drawable.add);
			}
		}
		if(!TextUtils.isEmpty(rightIcon)){
			if(rightIcon.equalsIgnoreCase("iloan_lmpp")){
				setRightIcon(R.drawable.iloan_lmpp);
			}
		}
		if("true".equals(isBack)){
			if(leftDrawalbe != null){
				leftImgBtn.setBackgroundDrawable(leftDrawalbe);
			}
			leftImgBtn.setVisibility(View.VISIBLE);
		}else{
			leftImgBtn.setVisibility(View.GONE);
		}
		if(!TextUtils.isEmpty(rightText)){
			tvRight.setText(rightText);
			tvRight.setVisibility(View.VISIBLE);
		}else if(!TextUtils.isEmpty(rightText)){
			
		}else{
			tvRight.setVisibility(View.GONE);
		}
	}

	public String getLeftCallback(){
		return this.leftCallback;
	}
	
	/**修改title*/
	public void setTitle(String title){
		imgTitle.setVisibility(View.GONE);
		tvTitle.setVisibility(View.VISIBLE);
		if(!TextUtils.isEmpty(title)){
			tvTitle.setText(title);
		}else{
			tvTitle.setText(R.string.app_name);
		}
	}
	public void setTitleImg(int resImgId){
		imgTitle.setVisibility(View.VISIBLE);
		tvTitle.setVisibility(View.GONE);
		imgTitle.setImageResource(resImgId);
	}
	
	public void setLeftIcon(int iconResId){
		Drawable leftDrawalbe = context.getResources().getDrawable(iconResId);
		leftImgBtn.setBackgroundDrawable(leftDrawalbe );
		leftImgBtn.setVisibility(View.VISIBLE);
		tvLeft.setVisibility(View.GONE);
	}
	
	public void setLeftText(String leftText){
		tvLeft.setText(leftText);
		tvLeft.setVisibility(View.VISIBLE);
		leftImgBtn.setVisibility(View.GONE);
	}
	
	public void setLeftInvisiable(){
		tvLeft.setVisibility(View.GONE);
		leftImgBtn.setVisibility(View.GONE);
	}
	
	public void setRightIcon(int iconResId){
		Drawable leftDrawalbe = context.getResources().getDrawable(iconResId);
		rightImgBtn.setBackgroundDrawable(leftDrawalbe);
		rightImgBtn.setVisibility(View.VISIBLE);
	}
	
	public void setRightText(String rightText){
		tvRight.setText(rightText);
		tvRight.setVisibility(View.VISIBLE);
		rightImgBtn.setVisibility(View.GONE);
	}
	
	public void setRightInvisiable(){
		tvRight.setVisibility(View.GONE);
		rightImgBtn.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.back_btn) {
			if (onTitleBarClickListener != null) {
				onTitleBarClickListener.onClickLeftButton(leftCallback);
			}

		} else if (id == R.id.left_header_textview) {
			if (onTitleBarClickListener != null) {
				onTitleBarClickListener.onClickLeftButton(leftCallback);
			}

		} else if (id == R.id.center_title_loan_textview) {
			if (onTitleBarClickListener != null) {
				onTitleBarClickListener.onClickTitleText();
			}

		} else if (id == R.id.right_header_btn) {
			if (onTitleBarClickListener != null) {
				onTitleBarClickListener.onClickRightButton(rightCallback);
			}

		} else if (id == R.id.right_header_textview) {
			if (onTitleBarClickListener != null) {
				onTitleBarClickListener.onClickRightButton(rightCallback);
			}

		} else {
		}
	}

	public void setOnTitleBarClickListener(OnTitleBarClickListener onTitleBarClickListener) {
		this.onTitleBarClickListener = onTitleBarClickListener;
	}

	public OnTitleBarClickListener getOnTitleBarClickListener() {
		return onTitleBarClickListener;
	}

	public ImageView getImgTitle() {
		return imgTitle;
	}

	public void setTitleInvisiable(){
		imgTitle.setVisibility(View.GONE);
		tvTitle.setVisibility(View.GONE);
	}

	/**将标题栏设置为默认状态,但不包括标题*/
	public void setToDefaultStateExcludeTitle(){
		leftCallback = "";
		rightCallback = "";
		setLeftIcon(R.drawable.global_back);
		setRightInvisiable();
	}

//	public void setHeader(WebHeaderRecord record) {
//		if(record!=null){
//			onHeader(record.title,
//					record.isBack,
//					record.backCallback,
//					record.rightText,
//					record.rightIcon,
//					record.rightCallback,
//					record.data);
//		}
//	}
}
