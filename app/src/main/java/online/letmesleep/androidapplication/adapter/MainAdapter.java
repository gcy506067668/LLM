package online.letmesleep.androidapplication.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import online.letmesleep.androidapplication.R;

public class MainAdapter extends BaseQuickAdapter<String,BaseViewHolder> {


    public MainAdapter(@Nullable List<String> data) {
        super(R.layout.adapter_main,data);
    }


    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.adapter_main_text,item);
        if(item.contains("---")){
            ((TextView)helper.getView(R.id.adapter_main_text)).setTextColor(Color.RED);
        }else{
            ((TextView)helper.getView(R.id.adapter_main_text)).setTextColor(Color.parseColor("#45adfc"));
        }
    }
}
