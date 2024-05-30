//package com.ethan.player.activity;
//
//
//import static com.ethan.player.util.VlcUtils.millisToString;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Handler.Callback;
//import android.os.Message;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.ImageButton;
//import android.widget.RelativeLayout;
//import android.widget.SeekBar;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import com.ethan.player.listener.VlcListener;
//import com.ethan.player.model.PlayerPara;
//import com.ethan.player.util.VlcUtils;
//
//import org.videolan.R;
//import org.videolan.libvlc.Media;
//import org.videolan.libvlc.media.VideoView;
//
//public class VideoPlayerActivity extends Activity implements VlcListener.OnChangeListener, OnClickListener, OnSeekBarChangeListener, Callback {
//
//	private static final int SHOW_PROGRESS = 0;
//	private static final int ON_LOADED = 1;
//	private static final int HIDE_OVERLAY = 2;
//
//	//自定义播放界面
//	private VideoView mPlayerView;
//	//滚动条
//	private SeekBar sbVideo;
//	//缓冲等待
//	private View rlLoading;
//	//底部工具栏和顶部标题栏
//	private View llOverlay, rlOverlayTitle;
//	//快进 暂停/播放
//	private ImageButton ibForward, ibPlay;
//	//标题 缓冲进度  当前播放时间 总长度 清晰度
//	private TextView tvTitle, tvBuffer, tvTime, tvLength, mArticulation;
//	//返回
//	private RelativeLayout mBack;
//	//错误显示
//	private RelativeLayout mErrorRl;
//	//错误信息
//	private TextView mErrorTv, mErrorBtn;
//
//	//是否返回
//	private boolean isBack = false;
//	private boolean isBuff = false;
//
//	private Handler mHandler;
//
//    private PlayerPara mPlayerPara;
//
//	private boolean isFirstCome = true;
//
//	private boolean isPlaying = false;
//
//	private boolean mIsNetWorkError = false;
//
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//		setContentView(R.layout.activity_player);
//		initView();
//		regEvent(true);
//		initData();
//	}
//
//	private void initView() {
//		tvTitle = (TextView) findViewById(R.id.tv_title);
//		tvTime = (TextView) findViewById(R.id.tv_time);
//		tvLength = (TextView) findViewById(R.id.tv_length);
//		sbVideo = (SeekBar) findViewById(R.id.sb_video);
//		mBack = (RelativeLayout) findViewById(R.id.rl_back_left);
//		mArticulation = (TextView) findViewById(R.id.tv_articulation);
//        mArticulation.setVisibility(View.GONE);
//		ibPlay = (ImageButton) findViewById(R.id.ib_play);
//		ibForward = (ImageButton) findViewById(R.id.ib_forward);
//        ibForward.setVisibility(View.GONE);
//		llOverlay = findViewById(R.id.ll_overlay);
//		rlOverlayTitle = findViewById(R.id.rl_title);
//		rlLoading = findViewById(R.id.rl_loading);
//		tvBuffer = (TextView) findViewById(R.id.tv_buffer);
//
//		mErrorRl = (RelativeLayout)findViewById(R.id.error_rl);
//		mErrorTv = (TextView) findViewById(R.id.error_tv);
//		mErrorBtn = (TextView)findViewById(R.id.error_btn);
//
//        mPlayerView = (VideoView) findViewById(R.id.pv_video);
//	}
//
//	private void regEvent(boolean b) {
//
//		if(sbVideo != null) {
//			sbVideo.setOnSeekBarChangeListener(b ? this : null);
//		}
//
//		if(mBack != null) {
//			mBack.setOnClickListener(b ? this : null);
//		}
//		if(mArticulation != null) {
//			mArticulation.setOnClickListener(b ? this : null);
//		}
//
//		if(ibPlay != null) {
//			//暂停
//			ibPlay.setOnClickListener(b ? this : null);
//		}
//
//		if(mErrorBtn != null) {
//			mErrorBtn.setOnClickListener(b ? this : null);
//		}
//	}
//
//	private void initData() {
//
//		try {
//            mPlayerPara = (PlayerPara) getIntent().getSerializableExtra("para");
//            if(mPlayerPara == null) {
//                return ;
//            }
//
//            mHandler = new Handler(this);
//
//            //设置标题
//            if (!VlcUtils.isEmpty(mPlayerPara.getTitle())) {
//                tvTitle.setText(mPlayerPara.getTitle());
//            }
//
//            //设置是否能拖动seekBar
//            if(!mPlayerPara.isDragSeekBar()) {
//                sbVideo.setVisibility(View.GONE);
//                sbVideo = (SeekBar) findViewById(R.id.sb_custom_video);
//                sbVideo.setVisibility(View.VISIBLE);
//            }
//
//			//设置播放路径
//            if(VlcUtils.isLocalPath(mPlayerPara.getPath())) {
//                mPlayerView.setVideoPath(mPlayerPara.getPath());
//            } else {
//                mPlayerView.setVideoURI(Uri.parse(mPlayerPara.getPath()));
//            }
//
//			mPlayerView.setOnChangeListener(this);
//			mPlayerView.start();
//			//显示加载进度条
//			showLoading();
//			//显示工具栏
//			showOverlay();
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void gotoVideoPlayerActivity(Context mContext, PlayerPara para) {
//
//		if(para == null || VlcUtils.isEmpty(para.getPath())) {
//			Toast.makeText(mContext, "视频播放地址不能为空", Toast.LENGTH_LONG).show();
//			return ;
//		}
//
//		if(para.getHideOverLayLength() <= 0 ) {
//			para.setHideOverLayLength(8 * 1000);
//		}
//
//		Intent intent = new Intent(mContext, VideoPlayerActivity.class);
//		intent.putExtra("para", para);
//		mContext.startActivity(intent);
//	}
//
//
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if(mIsNetWorkError) {
//			return false;
//		}
//
//		if (event.getAction() == MotionEvent.ACTION_UP) {
//			if (llOverlay.getVisibility() != View.VISIBLE) {
//				showOverlay();
//			} else {
//				hideOverlay();
//			}
//		}
//		return false;
//	}
//
//	@Override
//	public void onPause() {
//		hideOverlay();
//		if(isBack) {
//			//返回按键
//			mPlayerView.stop();
//		} else {
//			//home 不在主界面情况  但是不销毁
//			isPlaying = mPlayerView.isPlaying();
//			if (mPlayerView != null && mPlayerView.isPlaying()) {
//				mPlayerView.pause();
//			}
//		}
//		super.onPause();
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		if(isFirstCome) {
//			isFirstCome = false;
//		} else {
//			if(isPlaying)  {
//				mPlayerView.resume();
//			}
//			showOverlay();
//		}
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//	}
//
//	@Override
//	protected void onDestroy() {
//		if(mHandler != null) {
//			mHandler.removeMessages(HIDE_OVERLAY);
//			mHandler.removeMessages(ON_LOADED);
//			mHandler.removeMessages(SHOW_PROGRESS);
//		}
//
//		if(mPlayerView != null) {
//			mPlayerView.release();
//		}
//		super.onDestroy();
//	}
//
//	@Override
//	public void onBufferChanged(int buffer)
//	{
//		if (buffer >= 100 || buffer == 0)
//		{
//			hideLoading();
//		}
//		else
//		{
//			showLoading();
//		}
//		tvBuffer.setText("正在缓冲中..." + buffer + "%");
//	}
//
//	private void showLoading() {
//		rlLoading.setVisibility(View.VISIBLE);
//		isBuff = true;
//	}
//
//	private void hideLoading() {
//		rlLoading.setVisibility(View.GONE);
//		isBuff = false;
//	}
//
//	@Override
//	public void onLoadComplete() {
//		mHandler.sendEmptyMessage(ON_LOADED);
//	}
//
//	@Override
//	public void onError() {
//		Toast.makeText(getApplicationContext(), "无法播放该视频！", Toast.LENGTH_SHORT).show();
//		finish();
//	}
//
//	@Override
//	public void onEnd() {
//		//判断当前网络
//		if(!VlcUtils.isNetConnected(this)) {
//			mIsNetWorkError = true;
//			hideOverlay();
//			if(mErrorRl != null) {
//				mPlayerView.setVisibility(View.GONE);
//				mErrorRl.setVisibility(View.VISIBLE);
//			}
//			return ;
//		}
//		onBackPressed();
//	}
//
//	@Override
//	public void onCurrentTimeUpdate(int time) {
//
//	}
//
//	private void showOverlay() {
//		rlOverlayTitle.setVisibility(View.VISIBLE);
//		llOverlay.setVisibility(View.VISIBLE);
//		mHandler.sendEmptyMessage(SHOW_PROGRESS);
//		mHandler.removeMessages(HIDE_OVERLAY);
//		mHandler.sendEmptyMessageDelayed(HIDE_OVERLAY, mPlayerPara.getHideOverLayLength());
//	}
//
//	private void hideOverlay() {
//		rlOverlayTitle.setVisibility(mIsNetWorkError ? View.VISIBLE : View.GONE);
//		llOverlay.setVisibility(View.GONE);
//		mHandler.removeMessages(SHOW_PROGRESS);
//	}
//
//	/**
//	 * 设置同步UI界面
//	 * @return
//	 */
//	private int setOverlayProgress() {
//		if (mPlayerView == null) {
//			return 0;
//		}
//		int time = mPlayerView.getCurrentPosition();
//		int length =  mPlayerView.getDuration();
//		boolean playState = mPlayerView.isPlaying();
//
//		if(playState) {
//			ibPlay.setBackgroundResource(R.drawable.ic_pause);
//		} else {
//			ibPlay.setBackgroundResource(R.drawable.ic_play);
//		}
//
//		if (time >= 0) {
//			tvTime.setText(millisToString(time, false));
//			sbVideo.setProgress(time);
//		}
//		if (length >= 0) {
//			tvLength.setText(millisToString(length, false));
//			sbVideo.setMax(length);
//		}
//		return time;
//	}
//
//	@Override
//	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//		if (fromUser && mPlayerView.canSeekForward()) {
//			mPlayerView.seekTo(progress);
//			setOverlayProgress();
//		}
//	}
//
//	@Override
//	public void onStartTrackingTouch(SeekBar seekBar) {
//
//	}
//
//	@Override
//	public void onStopTrackingTouch(SeekBar seekBar) {
//
//	}
//
//	@Override
//	public boolean handleMessage(Message msg) {
//		switch (msg.what) {
//		case SHOW_PROGRESS:
//			setOverlayProgress();
//			mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 100);
//			break;
//		case ON_LOADED:
//			showOverlay();
//			hideLoading();
//			break;
//		case HIDE_OVERLAY:
//			hideOverlay();
//			break;
//		default:
//			break;
//		}
//		return false;
//	}
//
//
//	@Override
//	public void onBackPressed() {
//		//监听返回案件
//		isBack = true;
//		this.finish();
//	}
//
//
//	@Override
//	public void onClick(View view) {
//		int id = view.getId();
//		if(id == R.id.rl_back_left) {
//			isBack = true;
//			this.finish();
//		} else if(id == R.id.tv_articulation) {
//
//		} else if(id == R.id.ib_forward) {
//			//快进
//			if(!isBuff && mPlayerView.getCurrentPosition() <= mPlayerView.getDuration() -10000) {
//				mPlayerView.seekTo(10000);
//			}
//		} else if(id == R.id.ib_play) {
//			//暂停或者播放
//			if (mPlayerView.isPlaying()) {
//				mPlayerView.pause();
//				ibPlay.setBackgroundResource(R.drawable.ic_play);
//			} else {
//				mPlayerView.resume();
//				ibPlay.setBackgroundResource(R.drawable.ic_pause);
//			}
//		} else if(id == R.id.error_btn) {
//			mIsNetWorkError = false;
//			//点击重试
//			if(mErrorRl != null) {
//				mErrorRl.setVisibility(View.GONE);
//			}
//
//			if(mPlayerView != null) {
//				mPlayerView.setVisibility(View.VISIBLE);
//				initData();
//			}
//		}
//
//	}
//
//	@Override
//	public void onPrepared() {
//
//	}
//}
