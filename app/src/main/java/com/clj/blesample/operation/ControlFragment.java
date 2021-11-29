package com.clj.blesample.operation;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.clj.blesample.MainActivity;
import com.clj.blesample.R;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ControlFragment extends Fragment {
    private LinearLayout layout_container;
    private final List<String> childList = new ArrayList<>();
    private  View v ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View v = inflater.inflate(R.layout.fragment_service_list, null);
         v = inflater.inflate(R.layout.read_write, null);
        Log.e("initView额就我v积极的k","电话外汇");
        initView(v);
        showData();
        return v;
    }

    private void initView(View v) {
        //导航栏菜单
        //按钮按钮
        Button btnbtn = (Button)v.findViewById(R.id.button1);
        btnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("跳转","changePage(1)");
                ((ControlActivity) getActivity()).changePage(1);
            }
        });
        //电磁阀按钮按钮
        Button btnelec = (Button)v.findViewById(R.id.button2);
        btnelec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("跳转","changePage(2)");
                ((ControlActivity) getActivity()).changePage(2);
            }
        });
        //表头
        layout_container = (LinearLayout) v.findViewById(R.id.layout_container);
        //仪表盘
        ClockView clock_view = (ClockView) v.findViewById(R.id.clock_view);
        clock_view.setCompleteDegree(10f);

    }

    public void showData() {
        Log.e("initView额就我v积极的k","0");
        final BleDevice bleDevice = ((ControlActivity) getActivity()).getBleDevice();
       final BluetoothGattCharacteristic characteristic = ((ControlActivity) getActivity()).getCharacteristic();
        final int charaProp = ((ControlActivity) getActivity()).getCharaProp();
        final String characteristicserviceuuid="0000ffe0-0000-1000-8000-00805f9b34fb";
        final String characteristicuuid="0000ffe1-0000-1000-8000-00805f9b34fb";
        String child = characteristicuuid + String.valueOf(charaProp);


            View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_characteric_operation, null);
            view.setTag(bleDevice.getKey() + characteristicuuid + charaProp);
            LinearLayout layout_add = (LinearLayout) view.findViewById(R.id.layout_add);
            final TextView txt_title = (TextView) view.findViewById(R.id.txt_title);
           // txt_title.setText(String.valueOf(characteristicuuid + getActivity().getString(R.string.data_changed)));
            txt_title.setText("数据展示区：");
            final TextView txt = (TextView) view.findViewById(R.id.txt);
            txt.setMovementMethod(ScrollingMovementMethod.getInstance());
            Log.e("characteristiservice","0000ffe0-0000-1000-8000-00805f9b34fb");
            Log.e("characteristicgetUuid","0000ffe1-0000-1000-8000-00805f9b34fb");

            //启动车辆
            Button btnread = (Button)v.findViewById(R.id.button4);
            btnread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // String hex = et.getText().toString();
                    String hex = "adfa";
                    if (TextUtils.isEmpty(hex)) {
                        return;
                    }
                    BleManager.getInstance().write(
                            bleDevice,
                            characteristicserviceuuid,
                            characteristicuuid,
                            HexUtil.hexStringToBytes(hex),
                            new BleWriteCallback() {

                                @Override
                                public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.e("发送成功","0000ffe0-0000-1000-8000-00805f9b34fb");
                                            addText(txt, "写入成功 : " + HexUtil.formatHexString(justWrite, true));
                                        }
                                    });
                                }

                                @Override
                                public void onWriteFailure(final BleException exception) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.e("发送失败","0000ffe0-0000-1000-8000-00805f9b34fb");
                                            addText(txt, exception.toString());
                                        }
                                    });
                                }
                            });
                }
            });

                        BleManager.getInstance().notify(
                                bleDevice,
                                characteristicserviceuuid,
                                characteristicuuid,
                                new BleNotifyCallback() {

                                    @Override
                                    public void onNotifySuccess() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                addText(txt, "唤醒成功，等待接收！！！");
                                            }
                                        });
                                    }

                                    @Override
                                    public void onNotifyFailure(final BleException exception) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                addText(txt, exception.toString());
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCharacteristicChanged(final byte[] data) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Log.e("收到通知",data[0]+"*"+data[1]);
                                                addText(txt, "收到数据,: "+ HexUtil.formatHexString(data, true));
                                            }
                                        });
                                    }
                                });

    }

    private class ResultAdapter extends BaseAdapter {

        private Context context;
        private final List<BluetoothGattService> bluetoothGattServices;

        ResultAdapter(Context context) {
            this.context = context;
            bluetoothGattServices = new ArrayList<>();
        }

        void addResult(BluetoothGattService service) {

            if(service.getUuid().toString().equals("0000ffe0-0000-1000-8000-00805f9b34fb")){
                bluetoothGattServices.add(service);
            }
        }

        void clear() {
            bluetoothGattServices.clear();
        }

        @Override
        public int getCount() {
            return bluetoothGattServices.size();
        }

        @Override
        public BluetoothGattService getItem(int position) {
            if (position > bluetoothGattServices.size())
                return null;
            return bluetoothGattServices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = View.inflate(context, R.layout.adapter_service, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);
                holder.txt_uuid = (TextView) convertView.findViewById(R.id.txt_uuid);
                holder.txt_type = (TextView) convertView.findViewById(R.id.txt_type);
            }

            BluetoothGattService service = bluetoothGattServices.get(position);
            String uuid = service.getUuid().toString();
                holder.txt_title.setText(String.valueOf(getActivity().getString(R.string.service) + "(" + position + ")"));
                Log.e("我選中服務的UUID",uuid+"*"+position);
                holder.txt_uuid.setText(uuid);
                holder.txt_type.setText(getActivity().getString(R.string.type));
            return convertView;
        }


        class ViewHolder {
            TextView txt_title;
            TextView txt_uuid;
            TextView txt_type;
        }
    }
    private void runOnUiThread(Runnable runnable) {
        if (isAdded() && getActivity() != null)
            getActivity().runOnUiThread(runnable);
    }

    private void addText(TextView textView, String content) {
        textView.append(content);
        textView.append("\n");
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > textView.getHeight()) {
            textView.scrollTo(0, offset - textView.getHeight());
        }
    }
}
