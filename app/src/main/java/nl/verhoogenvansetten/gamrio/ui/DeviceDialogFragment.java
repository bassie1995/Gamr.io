package nl.verhoogenvansetten.gamrio.ui;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nl.verhoogenvansetten.gamrio.R;
import nl.verhoogenvansetten.gamrio.util.Network;

/**
 * Created by Bas on 13-10-2016.
 * Create a DialogFragment to show found WiFi Direct devices.
 */

public class DeviceDialogFragment extends DialogFragment {
    private OnFragmentInteractionListener mListener;
    RecyclerView mRecyclerView;
    public static MyRecyclerAdapter adapter;

    public DeviceDialogFragment() {
        // Empty constructor is required.
    }

    public static DeviceDialogFragment newInstance() {
        DeviceDialogFragment fragment = new DeviceDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate layout with recycler view
        View v = inflater.inflate(R.layout.fragment_device_dialog, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.device_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // setadapter
        ArrayList<WifiP2pDevice> wifiP2pDevices = new ArrayList<>();
        WifiP2pDevice yolo = new WifiP2pDevice();
        yolo.deviceName = "Yolo";
        wifiP2pDevices.add(yolo);
        adapter = new MyRecyclerAdapter(wifiP2pDevices);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);
        // get your recycler view and populate it.

        getDialog().setTitle("Verbinden");

        return v;
    }

    public static void filterAdapter (List<WifiP2pDevice> deviceList) {
        Log.d("DEVICE DEBUG", deviceList.toString());
        adapter.setFilter(deviceList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class MyRecyclerAdapter
            extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

        private final List<WifiP2pDevice> mValues;

        MyRecyclerAdapter(List<WifiP2pDevice> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.device_list_content, parent, false);
            return new ViewHolder(view);
    }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mContentView.setText(mValues.get(position).deviceName);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "hai", Toast.LENGTH_SHORT).show();
                    WifiP2pDevice device = mValues.get(position);
                    WifiP2pConfig config = new WifiP2pConfig();
                    config.deviceAddress = device.deviceAddress;
                    config.wps.setup = WpsInfo.PBC;
                    Network.connect(config, getActivity());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        void setFilter (List<WifiP2pDevice> deviceList) {
            mValues.clear();
            mValues.addAll(deviceList); // Use actual gameList here
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.txtvDevice);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
