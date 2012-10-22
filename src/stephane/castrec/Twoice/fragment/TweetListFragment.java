
package stephane.castrec.Twoice.fragment;

import java.util.List;

import stephane.castrec.Twoice.TwoiceActivity.TweetListFragmentInterface;
import stephane.castrec.Twoice.adapter.TweetAdapter;
import twitter4j.ResponseList;
import twitter4j.Status;
import android.app.ListFragment;
import android.os.Bundle;
import android.widget.BaseAdapter;

public class TweetListFragment extends ListFragment implements TweetListFragmentInterface{

	private BaseAdapter mAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void setListContent(List<Status> statuses) {
		if(statuses == null || statuses.isEmpty())
			return;
		mAdapter = new TweetAdapter(this.getActivity(), statuses);
		setListAdapter(mAdapter);		
	}

	@Override
	public void updateListContent(List<Status> statuses) {
		if(statuses == null || statuses.isEmpty())
			return;
		if(mAdapter == null || this.getListAdapter() == null || this.getListAdapter().isEmpty()){
			mAdapter = new TweetAdapter(this.getActivity(), statuses);
			setListAdapter(mAdapter);
		}
		//update list in adapters
		((TweetAdapter)mAdapter).updateList(statuses);
		//notify adapters that list has been changed
		mAdapter.notifyDataSetChanged();

	}     
}
