package grocket.com.smart119citizen;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import java.util.EmptyStackException;
import java.util.Stack;

import weisure.com.keipacklib.view.BaseAppCompatActivity;

/**
 * Created by chokyounglae on 16. 9. 2..
 */
public class CommonCompatActivity extends BaseAppCompatActivity {
    // Back Stack Activity
    private static Stack<Activity> mBackStackActivity = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//		enableExternalFont(FONT_NAME);
        disableExternalFont();

        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);

//        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        // 카메라 & 갤러리 이미지 선택 설정
        //setImageSizeBoundary(400); // optional. default is 500.
//        setCropOption(1, 1);  // optional. default is no crop.
        //setCustomButtons(btnGallery, btnCamera, btnCancel); // you can set these buttons.
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    /**
     * 현재 Activity를 back stack에 저장.
     * 현재 Activity에서 pushThisActivityIntoBackStack()를 호출을 했다면,
     * 그 Activity가 종료될 때 반드시 popActivityFromBackStack()를 호출해서 back stack에서 지워야한다.
     */
    protected void pushThisActivityIntoBackStack()
    {
        mBackStackActivity.push(this);
    }

    protected Activity popActivityFromBackStack() {
        if(mBackStackActivity.size() == 0) return null;
        return mBackStackActivity.pop();
    }

    protected int getBackStackSize() {
        return mBackStackActivity.size();
    }

    protected void clearAllActivityInBackStack() {
        debugMessage("back stack size : " + mBackStackActivity.size());
        mBackStackActivity.clear();
    }

    /**
     * Back Stack에 깔려있는 모든 Activity 종료.
     * Top에 위치한 Activity는 현재 화면에 보이는 Activity이기 때문에 종료하지 않음.
     */
    protected void finishAllActivityInBackStack() {
        debugMessage("back stack size : " + mBackStackActivity.size());
        for(int i=0;i<mBackStackActivity.size();i++) {
            Activity act = mBackStackActivity.get(i);
            if(act == null) continue;
            if(act == this) continue; // 현재 activity는 종료하지 않는다.
            mBackStackActivity.get(i).finish();
        }

        try {
            Activity topAct = mBackStackActivity.pop();
            mBackStackActivity.clear();
            mBackStackActivity.push(topAct);
        }
        catch (EmptyStackException e) {
            e.printStackTrace();
        }
    }
}
