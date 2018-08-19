package online.letmesleep.androidapplication.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


import java.util.List;

import online.letmesleep.androidapplication.R;
import online.letmesleep.androidapplication.bean.Contacts;

public class PhoneContactAdapter extends BaseQuickAdapter<Contacts,BaseViewHolder> {
    public PhoneContactAdapter(@Nullable List<Contacts> data) {
        super( R.layout.adapter_recyclerview_contacts,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Contacts item) {
        helper.setText(R.id.adapter_contacts_name,item.getName());
        if(item.getType()==1)
            helper.setImageResource(R.id.adapter_contacts_image,R.drawable.ic_home_colorful_24dp);
        else
            helper.setImageResource(R.id.adapter_contacts_image,R.drawable.ic_account_circle_colorful_24dp);
    }
}
