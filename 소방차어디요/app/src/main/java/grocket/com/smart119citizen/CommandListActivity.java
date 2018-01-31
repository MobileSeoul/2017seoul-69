package grocket.com.smart119citizen;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.AbsListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import grocket.com.smart119citizen.data.CommandItemData;
import grocket.com.smart119citizen.data.CommandItemDataListAdapter;
import grocket.com.smart119citizen.global.ApiUrl;
import grocket.com.smart119citizen.global.Preferences;
import grocket.com.smart119citizen.utils.MyToolbar;
import grocket.com.smart119citizen.utils.SimpleLineDividerItemDecoration;
import weisure.com.keipacklib.http.OvHttpRequestParameters;
import weisure.com.keipacklib.view.SimpleToast;

public class CommandListActivity extends CommonCompatActivity {

    private final int LIST_TOTAL_NO = 10;
    private final int REQ_GET_COMMAND_LIST = 1000;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private int mCurrentPageNo = 1;
    private int mTotalPageNo = 1;
    private int mTotalItemCnt = 0;
    private boolean lastitemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크

    private MyToolbar mMyToolbar;
    private ArrayList<CommandItemData> mCommandDataList;
    private CommandItemDataListAdapter mDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_list);
        ButterKnife.inject(this);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mTotalItemCnt == 0) {
            mCurrentPageNo = 1;
            getCommandList(mCurrentPageNo, LIST_TOTAL_NO);
        }
    }

    @Override
    protected void init() {
        super.init();

        mMyToolbar = new MyToolbar(this, mToolbar);
        mMyToolbar.setToolbarWithBackKey(getString(R.string.title_command_list));

        mCommandDataList = new ArrayList<>();

        mDataAdapter = new CommandItemDataListAdapter(this, mCommandDataList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(
                new SimpleLineDividerItemDecoration(this,
                        SimpleLineDividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.setAdapter(mDataAdapter);
        mRecyclerView.addOnScrollListener(onScrollListener);
    }

    private void getCommandList(int pageNo, int articleCnt) {
        Preferences pref = Preferences.getInstance(this);
        final String myID = pref.getID();

        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.GET_COMMAND_LIST);
        params.setRequestCode(REQ_GET_COMMAND_LIST);
        params.addParameter("ID", myID);
        params.addParameter("PageNo", String.valueOf(pageNo));
        params.addParameter("Cnt", String.valueOf(articleCnt));
        requestHttpConnect(params);
    }

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
            //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag) {
                debugMessage("list size : " + mCommandDataList.size() + ", total cnt : " + mTotalItemCnt);
                if (mCommandDataList.size() < mTotalItemCnt) {
                    mCurrentPageNo++;
                    if (mCurrentPageNo >= mTotalPageNo) mCurrentPageNo = mTotalPageNo;
                    getCommandList(mCurrentPageNo, LIST_TOTAL_NO);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = recyclerView.getLayoutManager().getItemCount();
            int firstVisibleItem = ((LinearLayoutManager)
                    recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

            //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
            lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
        }
    };

    @Override
    public void onHttpSuccess(int requestCode, JSONObject json_result) throws JSONException {
        super.onHttpSuccess(requestCode, json_result);

        if(requestCode == REQ_GET_COMMAND_LIST) {
            mCurrentPageNo = json_result.getInt("CurrentPageNo");
            mTotalPageNo = json_result.getInt("TotalPageCnt");
            mTotalItemCnt = json_result.getInt("TotalItemCnt");

            String dataResult = json_result.getString("data");
            JSONArray ja = new JSONArray(dataResult);

//            if(ja.length() == 0) {
//                mImgEmpty.setVisibility(View.VISIBLE);
//                mRecyclerView.setVisibility(View.GONE);
//            }else{
//                mImgEmpty.setVisibility(View.GONE);
//                mRecyclerView.setVisibility(View.VISIBLE);
//            }

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                CommandItemData item = new CommandItemData();
                item.setPrimarykey(jo.getString("Primarykey"));
                item.setCommandType(jo.getInt("CommandType"));
                item.setAccidentNo(jo.getString("AccidentNo"));
                item.setAccidentAddress(jo.getString("AccidentAddress"));
                item.setDetailAddress(jo.getString("DetailAddress"));
                item.setAccidentContent(jo.getString("AccidentContent"));
                item.setReporterTelephone(jo.getString("ReporterTelephone"));
                item.setRegDate(jo.getString("RegDate"));
                mCommandDataList.add(item);

                debugMessage("신고자 전화번호 : " + item.getReporterTelephone());
            }
            mDataAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onHttpFailure(int requestCode, JSONObject json_result) throws JSONException {
        super.onHttpFailure(requestCode, json_result);
        SimpleToast.show(this, json_result.getString("fail_cause"));
    }

    @Override
    public void onHttpError(String error_message) {
        super.onHttpError(error_message);
        SimpleToast.show(this, error_message);
    }
}
