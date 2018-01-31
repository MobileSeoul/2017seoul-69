package grocket.com.smart119citizen;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import grocket.com.smart119citizen.data.ContactItemData;
import grocket.com.smart119citizen.data.ContactItemDataListAdapter;
import grocket.com.smart119citizen.global.ApiUrl;
import grocket.com.smart119citizen.global.Preferences;
import grocket.com.smart119citizen.utils.ItemClickSupport;
import grocket.com.smart119citizen.utils.MyToolbar;
import grocket.com.smart119citizen.utils.PhoneUtil;
import grocket.com.smart119citizen.utils.SimpleLineDividerItemDecoration;
import weisure.com.keipacklib.dialog.SimpleSelectDialog;
import weisure.com.keipacklib.http.OvHttpRequestParameters;
import weisure.com.keipacklib.view.SimpleToast;

public class ContactListActivity extends CommonCompatActivity implements View.OnClickListener{

    private final int LIST_TOTAL_NO = 10;
    private final int REQ_COMPLETE_INSERT_CONTACT = 100;
    private final int REQ_COMPLETE_UPDATE_CONTACT = 101;
    private final int REQ_GET_CONTACT_LIST = 1000;
    private final int REQ_REMOVE_CONTACT_ITEM = 1001;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.btnAddTelephone)
    ImageButton mBtnAddTelephone;

    private int mCurrentPageNo = 1;
    private int mTotalPageNo = 1;
    private int mTotalItemCnt = 0;
    private boolean lastitemVisibleFlag = false;        //화면에 리스트의 마지막 아이템이 보여지는지 체크

    private MyToolbar mMyToolbar;
    private ArrayList<ContactItemData> mContactDataList;
    private ContactItemDataListAdapter mDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        ButterKnife.inject(this);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mTotalItemCnt == 0) {
            mCurrentPageNo = 1;
            mContactDataList.clear();
            getContactList(mCurrentPageNo, LIST_TOTAL_NO);
        }
    }

    @Override
    protected void init() {
        super.init();

        mMyToolbar = new MyToolbar(this, mToolbar);
        mMyToolbar.setToolbarWithBackKey(getString(R.string.title_contact_list));

        mContactDataList = new ArrayList<>();

        mDataAdapter = new ContactItemDataListAdapter(this, mContactDataList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(
                new SimpleLineDividerItemDecoration(this,
                        SimpleLineDividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.setAdapter(mDataAdapter);
        mRecyclerView.addOnScrollListener(onScrollListener);
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                ContactItemData item = mContactDataList.get(position);
                final String telephone = item.getTelephone();
                PhoneUtil.showPhoneDial(ContactListActivity.this, telephone);
            }
        });

        ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener(onItemLongClickListener);

        mBtnAddTelephone.setOnClickListener(this);
    }

    ItemClickSupport.OnItemLongClickListener onItemLongClickListener = new ItemClickSupport.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
            ContactItemData item = mContactDataList.get(position);
            final String pk = item.getPrimaryKey();
            final String name = item.getName();
            final String pos = item.getPosition();
            final String tel = item.getTelephone();
            final String[] items = new String[] { "삭제", "수정" };
            SimpleSelectDialog.show(ContactListActivity.this, "메뉴선택", items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which==0) {
                        removeContactItem(pk);
                    }
                    else {
                        Intent i = new Intent(ContactListActivity.this, UpdateContactActivity.class);
                        i.putExtra("Primarykey", pk);
                        i.putExtra("Name", name);
                        i.putExtra("Position", pos);
                        i.putExtra("Telephone", tel);
                        startActivityForResult(i, REQ_COMPLETE_UPDATE_CONTACT);
                    }
                }
            });
            return false;
        }
    };

    private void getContactList(int pageNo, int articleCnt) {
        Preferences pref = Preferences.getInstance(this);
        final String myID = pref.getID();

        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.GET_CONTACT_LIST);
        params.setRequestCode(REQ_GET_CONTACT_LIST);
        params.addParameter("ID", myID);
        params.addParameter("PageNo", String.valueOf(pageNo));
        params.addParameter("Cnt", String.valueOf(articleCnt));
        requestHttpConnect(params);
    }

    private void removeContactItem(String pk)
    {
        Preferences pref = Preferences.getInstance(this);
        final String myID = pref.getID();

        OvHttpRequestParameters params = new OvHttpRequestParameters();
        params.setUrl(ApiUrl.REMOVE_CONTACT_ITEM);
        params.setRequestCode(REQ_REMOVE_CONTACT_ITEM);
        params.addParameter("ID", myID);
        params.addParameter("PrimaryKey", pk);
        requestHttpConnect(params);
    }

    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태입니다.
            //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag) {
                debugMessage("list size : " + mContactDataList.size() + ", total cnt : " + mTotalItemCnt);
                if (mContactDataList.size() < mTotalItemCnt) {
                    mCurrentPageNo++;
                    if (mCurrentPageNo >= mTotalPageNo) mCurrentPageNo = mTotalPageNo;
                    getContactList(mCurrentPageNo, LIST_TOTAL_NO);
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

        if (requestCode == REQ_GET_CONTACT_LIST) {
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
                ContactItemData item = new ContactItemData();
                item.setPrimaryKey(jo.getString("PrimaryKey"));
                item.setName(jo.getString("Name"));
                item.setPosition(jo.getString("Position"));
                item.setTelephone(jo.getString("Telephone"));
                mContactDataList.add(item);
            }
            mDataAdapter.notifyDataSetChanged();
        }
        else if(requestCode == REQ_REMOVE_CONTACT_ITEM) {
            mCurrentPageNo = 1;
            mContactDataList.clear();
            getContactList(mCurrentPageNo, LIST_TOTAL_NO);
            SimpleToast.show(this, "선택하신 연락처가 삭제되었습니다.");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) return;

        if(requestCode == REQ_COMPLETE_INSERT_CONTACT ||
                requestCode == REQ_COMPLETE_UPDATE_CONTACT) {
            mCurrentPageNo = 1;
            mContactDataList.clear();
            getContactList(mCurrentPageNo, LIST_TOTAL_NO);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == mBtnAddTelephone) {
            Intent i = new Intent(this, InsertContactActivity.class);
            startActivityForResult(i, REQ_COMPLETE_INSERT_CONTACT);
        }
    }
}
