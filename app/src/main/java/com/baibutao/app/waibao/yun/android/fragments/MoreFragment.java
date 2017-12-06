package com.baibutao.app.waibao.yun.android.fragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.AlarmSetupActivity;
import com.baibutao.app.waibao.yun.android.activites.MoreAboutActivity;
import com.baibutao.app.waibao.yun.android.activites.SetupActivity;
import com.baibutao.app.waibao.yun.android.activites.SetupNewProductActivity;
import com.baibutao.app.waibao.yun.android.activites.UserManageActivity;
import com.baibutao.app.waibao.yun.android.activites.common.BaseFragment;
import com.baibutao.app.waibao.yun.android.activites.common.CheckUpdateForFragment;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadHelper;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;

import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoreFragment extends BaseFragment {

    private TextView moneyTv;
    private Future<Response> responseFuture;

    public MoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoreFragment newInstance() {
        MoreFragment fragment = new MoreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =
                inflater.inflate(R.layout.fragment_more, container, false);

        TextView usernameTv = (TextView) root.findViewById(R.id.setup_username);
        moneyTv = (TextView) root.findViewById(R.id.setup_money);
        usernameTv.setText("账户："+eewebApplication.getUserDO().getUsername());

        root.findViewById(R.id.account_setup).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                handleUpdateSetup(v);
            }
        });
        root.findViewById(R.id.alarm_setup).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                handleAlarmSetup(v);
            }
        });
        root.findViewById(R.id.about_us).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                handleAbout(v);
            }
        });

//        root.findViewById(R.id.more_product).setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                handleNewProducts(v);
//            }
//        });

        root.findViewById(R.id.check_update).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                handleCheckUpdate(v);
            }
        });

        init();
        return root;
    }

    private void init() {
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "getAlarmAccountInfo");
        ProgressDialog progressDialog = showProgressDialog(R.string.app_read_data);
        progressDialog.setOnDismissListener(new MoreFragment.LoadData());
        responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
    }




    public void handleAbout(View v) {
        Intent intent = new Intent(getActivity(), MoreAboutActivity.class);
        startActivity(intent);
    }

    /**
     * 账户管理
     * @param v
     */
    public void handleUpdateSetup(View v) {
        Intent intent = new Intent(getActivity(), UserManageActivity.class);
        startActivity(intent);
    }

    /**
     * 报警管理
     * @param v
     */
    public void handleAlarmSetup(View v) {
        Intent intent = new Intent(getActivity(), AlarmSetupActivity.class);
        startActivity(intent);
    }


//    public void handleNewProducts(View v) {
//        Intent intent = new Intent(getActivity(), SetupNewProductActivity.class);
//        startActivity(intent);
//    }

    public void handleCheckUpdate(View v) {

        final RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        final Request request = remoteManager.createQueryRequest(Config.Values.YUN_CHECK_VERSION_URL);
        request.addParameter("user", eewebApplication.getUserDO().getUsername());
        ProgressDialog progressDialog = showProgressDialog(R.string.app_read_data);
        CheckUpdateForFragment checkUpdate = new CheckUpdateForFragment(getActivity());
        progressDialog.setOnDismissListener(checkUpdate);
        checkUpdate.setResponseFuture(eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request)));
    }




    private class LoadData implements DialogInterface.OnDismissListener {

        @Override
        public void onDismiss(DialogInterface dialog) {
            if (responseFuture == null) {
                return;
            }

            try {

                Response response = responseFuture.get();
//                if (!response.isDataSuccess()) {
//                    return;
//                }

                /**
                 * ｛"code":0,"msg_remain":"剩余短信条数","msg_count":"已发短信条数","credit_money":"账户余额"｝
                 */

                JSONObject jsonObject = JsonUtil.getJsonObject(response.getModel());
                final double money = JsonUtil.getDouble(jsonObject, "credit_money", 0.d);


                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        moneyTv.setText("余额：" + String.valueOf(money) + "元");
                    }
                });



            } catch (Exception e) {
                MoreFragment.this.logError(e.getMessage(), e);
            }

        }

    }

}
