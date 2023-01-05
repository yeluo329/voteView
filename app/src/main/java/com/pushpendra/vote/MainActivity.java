package com.pushpendra.vote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView voteRv;
    private VoteAdapter voteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        voteRv = findViewById(R.id.vote_rv);
        voteAdapter = new VoteAdapter();
        voteRv.setLayoutManager(new LinearLayoutManager(this));
        voteRv.setAdapter(voteAdapter);

        // 自己编写的数据,可以根据需求从服务器拿去
        List<VoteInfo.optionInfo> voteInfoList = new ArrayList<>();
        voteInfoList.add(new VoteInfo.optionInfo("一直在一起", 4000, 1));
        voteInfoList.add(new VoteInfo.optionInfo("从未在一起", 1000, 2));
        voteInfoList.add(new VoteInfo.optionInfo("在一起然后分手", 5000, 3));
        VoteInfo voteInfo = new VoteInfo(0, voteInfoList);
        voteAdapter.setMyOption(voteInfo.getMyOption());
        voteAdapter.setCallBacK((position, voteInfo1) -> {
            for (int i = 0; i < voteInfo1.getOptionInfoList().size(); i++) {
                if (i != position)
                    voteAdapter.refreshPartItem(i, voteInfo1.getOptionInfoList());
            }
        });
        voteAdapter.update(voteInfo.getOptionInfoList());
    }


}