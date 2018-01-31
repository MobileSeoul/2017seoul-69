package grocket.com.smart119citizen;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import weisure.com.keipacklib.view.SimpleToast;

public class SampleVedioViewActivity extends CommonCompatActivity
        implements View.OnClickListener {

    public final static String VEDIO_URL_FIRE = "http://nicein.weisure.co.kr/movie/movie_fire.mp4";
    public final static String VEDIO_URL_EMERGENCY = "http://nicein.weisure.co.kr/movie/movie_emergency.mp4";
    public final static String VEDIO_URL_RESCUE = "http://nicein.weisure.co.kr/movie/movie_rescue.mp4";

    @InjectView(R.id.view)
    VideoView mView;
    @InjectView(R.id.txtAccidentContent)
    TextView mTxtAccidentContent;
    @InjectView(R.id.txtNotServiceHopistal1)
    TextView mTxtNotServiceHopistal1;
    @InjectView(R.id.txtNotServiceHopistal2)
    TextView mTxtNotServiceHopistal2;

    private int mCommandType;
    private String mAccidentNo;
    private String mAccidentAddr;
    private String mAccidentAddrDetail;
    private String mAccidentContent;
    private String mReporterTel;
    private String mRegDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_vedio_view);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        Bundle argsBundle = intent.getExtras();
        if (argsBundle != null) {
            mCommandType = argsBundle.getInt("CommandType");
            mAccidentNo = argsBundle.getString("AccidentNo");
            mAccidentAddr = argsBundle.getString("AccidentAddr");
            mAccidentAddrDetail = argsBundle.getString("AccidentAddrDetail");
            mAccidentContent = argsBundle.getString("AccidentContent");
            mReporterTel = argsBundle.getString("ReporterTel");
            mRegDate = argsBundle.getString("RegDate");

            debugMessage("재해번호 : " + mAccidentNo);
            debugMessage("주소 : " + mAccidentAddr);
            debugMessage("상세주소 : " + mAccidentAddrDetail);
            debugMessage("사고내용 : " + mAccidentContent);
            debugMessage("신고자 전화번호 : " + mReporterTel);

            String content = mAccidentAddr + "\n\n" +
                    mAccidentContent + "\n\n" +
                    "신고자 전화번호 : " + mReporterTel;
            mTxtAccidentContent.setText(content);
        }

        init();
    }

    @Override
    protected void init() {
        super.init();

        mTxtNotServiceHopistal1.setOnClickListener(this);
        mTxtNotServiceHopistal2.setOnClickListener(this);

        //미디어컨트롤러 추가하는 부분
        MediaController controller = new MediaController(this);
        mView.setMediaController(controller);

        //비디오뷰 포커스를 요청함
        mView.requestFocus();

        // 0-화재출동  1-구조출동  2-구급출동
        if (mCommandType == 0) {
            mView.setVideoURI(Uri.parse(VEDIO_URL_FIRE));
        } else if (mCommandType == 1) {
            mView.setVideoURI(Uri.parse(VEDIO_URL_RESCUE));
        } else if (mCommandType == 2) {
            mView.setVideoURI(Uri.parse(VEDIO_URL_EMERGENCY));
        }

//        //동영상 경로가 SDCARD일 경우
//        String path = Environment.getExternalStorageDirectory()
//                + "/TestVideo.mp4";
//        mView.setVideoPath(path);


        //동영상이 재생준비가 완료되었을 때를 알 수 있는 리스너 (실제 웹에서 영상을 다운받아 출력할 때 많이 사용됨)
        mView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playVideo();
//                Toast.makeText(
//                        SampleVedioViewActivity.this,
//                        "동영상이 준비되었습니다. \n'시작' 버튼을 누르세요", Toast.LENGTH_SHORT).show();
            }
        });

        //동영상 재생이 완료된 걸 알 수 있는 리스너
        mView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //동영상 재생이 완료된 후 호출되는 메소드
                playVideo();

//                Toast.makeText(
//                        SampleVedioViewActivity.this,
//                        "동영상 재생이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mTxtNotServiceHopistal1) {
            SimpleToast.show(this, "Full Bed", Gravity.CENTER, 1500);
        } else if (v == mTxtNotServiceHopistal2) {
            SimpleToast.show(this, "MRI 고장", Gravity.CENTER, 1500);
        }
    }

    //동영상 재생 Method
    private void playVideo() {
        //비디오를 처음부터 재생할 때 0으로 시작(파라메터 sec)
        mView.seekTo(0);
        mView.start();
    }

    //동영상 정지 Method
    private void stopVideo() {
        //비디오 재생 잠시 멈춤
        mView.pause();

        //비디오 재생 완전 멈춤
//        videoView.stopPlayback();
        //videoView를 null로 반환 시 동영상의 반복 재생이 불가능
//        videoView = null;
    }
}
