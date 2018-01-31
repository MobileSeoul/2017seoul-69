package weisure.com.keipacklib.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by chokyounglae on 15. 2. 9..
 */
public class ExtendedScrollView extends ScrollView {
    public static final int CHECK_SCROLL_BOTTOM = 99999;
    private final String TAG = "ExtendedScrollView";
    // 스크롤이 맨 아래까지 되면 처리할 이벤트 전달용 핸들러
    Handler m_Handler = null;

    // 스크롤뷰 영역 체크하려고 쓰는 Rect 변수
    Rect m_rect;

    public ExtendedScrollView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ExtendedScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public ExtendedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        checkIsLocatedAtFooter();
    }

    // 현재 스크롤이 최하단까지 내려왔는지 검사하는 메소드
    private void checkIsLocatedAtFooter() {
        if(m_rect == null)
        {
            m_rect = new Rect();		// int left, int top, int right, int bottom
            getLocalVisibleRect(m_rect);
            return;
        }

        int oldBottom = m_rect.bottom;
        getLocalVisibleRect(m_rect);			// 현재 스크롤뷰의 영역을 구함
        // 이때 스크롤을 이동시켰으면 top과 bottom이 이동한 만큼 변경된다.
        int height = getMeasuredHeight();	// 스크롤 뷰의 높이를 구함
        View v = getChildAt(0);	// 스크롤 뷰안에 들어있는 자식뷰의 높이 구하기위해 사용

        // 이전 bottom값과 스크롤 뷰의 높이가 없을 경우 처리안함
        if(oldBottom <= 0 || height <= 0) return;

        // bottom값의 변화가 없을 경우 처리안함
        // 그리고 현재 bottom의 값이 자식뷰의 맨 아래까지 왔을 경우 맨 아래까지 스크롤 한거임.
        if (oldBottom != m_rect.bottom
                && m_rect.bottom == (v.getMeasuredHeight() + getPaddingTop() + getPaddingBottom())) {
            Log.d(TAG, "스크롤 최하단임..!!");
            if (m_Handler != null)
                m_Handler.sendEmptyMessage(CHECK_SCROLL_BOTTOM);
        }
    }

    public void setHandler(Handler handler) {
        m_Handler = handler;
    }
}
