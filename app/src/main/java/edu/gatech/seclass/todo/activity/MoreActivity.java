package edu.gatech.seclass.todo.activity;


import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.seclass.todo.R;
import edu.gatech.seclass.todo.model.InfoModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 */
public class MoreActivity extends AppCompatActivity {

    private EditText edtNum;
    private TextView tvAdress;
    private Button btnQuery;
    private RecyclerView rv_data;
    private PhoneModel resultModel;
    private List<InfoModel> goodsModels = new ArrayList<>();
    MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_num);

        init();


        rv_data = findViewById(R.id.rv_data);
        mAdapter = new MyAdapter(goodsModels);
        rv_data.setLayoutManager(new LinearLayoutManager(this));
        rv_data.setAdapter(mAdapter);
    }

    private void init() {
        edtNum = findViewById(R.id.edt_num);
        btnQuery = findViewById(R.id.btn_query);
        tvAdress = findViewById(R.id.tv_adress);
        rv_data = findViewById(R.id.rv_data);


        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = edtNum.getText().toString();
                if (!TextUtils.isEmpty(num)) {
                    getData(num);
                } else {
                    Toast.makeText(MoreActivity.this, "请输入号码", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getData(final String num) {
        String url = "http://apis.juhe.cn/mobile/get?phone="+num +"&key=2b83ed20642560618b61847b0cd526e5";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                resultModel = new Gson().fromJson(str, PhoneModel.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String info = num + "  "+  resultModel.getResult().getProvince() + " " + resultModel.getResult().getCity();
                        tvAdress.setText(info);
                        goodsModels.add(new InfoModel(num,resultModel.getResult().getProvince(),resultModel.getResult().getCity()));
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }
}
