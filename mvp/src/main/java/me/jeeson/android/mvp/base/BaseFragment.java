package me.jeeson.android.mvp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.jeeson.android.mvp.base.delegate.IFragment;

import me.jeeson.android.mvp.mvp.IPresenter;
import com.trello.rxlifecycle2.components.support.RxFragment;

import javax.inject.Inject;

/**
 * Created by jeeson on 2015/12/8.
 */
public abstract class BaseFragment<P extends IPresenter> extends RxFragment implements IFragment{
    protected final String TAG = this.getClass().getSimpleName();
    @Inject
    protected P mPresenter;


    public BaseFragment() {
        //必须确保在Fragment实例化时setArguments()
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //绑定到butterknife
        return initView(inflater,container);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) mPresenter.onDestroy();//释放资源
        this.mPresenter = null;
    }


    /**
     * 是否使用eventBus,默认为使用(true)，
     *
     * @return
     */
    @Override
    public boolean useEventBus() {
        return true;
    }

}
