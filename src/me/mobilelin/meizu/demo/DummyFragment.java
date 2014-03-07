package me.mobilelin.meizu.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DummyFragment extends Fragment {

	private int mPosition;
	private TextView mTextView;

	public static DummyFragment newInstance(int position) {

		DummyFragment f = new DummyFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		f.setArguments(bundle);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getArguments().getInt("position");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_dummy, null);
		mTextView = (TextView) rootView.findViewById(R.id.tv_dummy);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mTextView.setText("Pager " + mPosition);
	}
}
