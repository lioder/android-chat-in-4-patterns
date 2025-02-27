package nju.androidchat.client.hw2;

import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import lombok.extern.java.Log;
import nju.androidchat.client.BR;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.hw2.viewmodel.Hw2ViewModel;
import nju.androidchat.client.hw2.viewmodel.UiOperator;

@Log
public class Hw2TalkActivity extends AppCompatActivity implements TextView.OnEditorActionListener, UiOperator {
    private Hw2ViewModel viewModel;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new Hw2ViewModel(this);
        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main_hw2);
        binding.setVariable(BR.viewModel, viewModel);
        mScrollView = findViewById(R.id.content_scroll_view);
        mScrollView.setOnScrollChangeListener((view, x, y, oldX, oldY) -> {
            Rect scrollRect = new Rect();
            mScrollView.getHitRect(scrollRect);
            LinearLayout linearLayout = (LinearLayout)mScrollView.getChildAt(0);
            int totalMsgCnt = linearLayout.getChildCount();
            int msgDisplayedCnt = 0;
            for (int i = 0; i < totalMsgCnt; i++) {
                View messageView = linearLayout.getChildAt(i);
                if (messageView.getLocalVisibleRect(scrollRect)) {
                    msgDisplayedCnt++;
                }
            }
            viewModel.setMsgNotDisplayed(totalMsgCnt - msgDisplayedCnt);
        });
    }


    @Override
    public void onBackPressed() {
        AsyncTask.execute(() -> {
            viewModel.disconnect();
        });

        Utils.jumpToHome(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            log.info("not on focus");
            return hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    private boolean hideKeyboard() {
        return Utils.hideKeyboard(this);
    }


    private void sendText() {
        viewModel.sendMessage();
    }

    public void onBtnSendClicked(View v) {
        hideKeyboard();
        sendText();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (Utils.send(actionId, event)) {
            hideKeyboard();
            // 异步地让Controller处理事件
            sendText();
        }
        return false;
    }

    @Override
    public void scrollListToBottom() {
        Utils.scrollListToBottom(this);
    }
}
