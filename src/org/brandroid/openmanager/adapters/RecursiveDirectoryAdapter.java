package org.brandroid.openmanager.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.Inflater;

import org.brandroid.openmanager.R;
import org.brandroid.utils.Logger;

import android.content.Context;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RecursiveDirectoryAdapter extends BaseAdapter
{
	private File[] mDirectories;
	private Context mContext;
	private static Hashtable<File, File[]> DirectoryCache = new Hashtable<File, File[]>();
	public static FileComparer Comparer;
	
	public RecursiveDirectoryAdapter(File parent, Context context)
	{
		mDirectories = getSubDirectories(parent);
		Logger.LogDebug("Recursing " + parent.getAbsolutePath() + " (" + (mDirectories != null ? mDirectories.length : "-1") + " directories)...");
		mContext = context;
	}
	
	public static File[] getSubDirectories(File parent)
	{
		Logger.LogDebug("Getting subdirs for " + parent.getAbsolutePath());
		if(DirectoryCache.containsKey(parent))
			return DirectoryCache.get(parent);
		ArrayList<File> dirs = new ArrayList<File>();
		if(parent.list() == null) return new File[0];
		for(String s : parent.list())
		{
			File f = new File(parent, s);
			if(!f.exists()) continue;
			if(!f.isDirectory()) continue;
			//Logger.LogDebug(s);
			dirs.add(f);
		}
		//Logger.LogDebug("Found " + dirs.size() + " Subdirs");
		//DirectoryCache.put(parent, dirs);
		File[] ret = getArray(dirs);
		Arrays.sort(ret, Comparer);
		return ret;
	}
	
	public static File[] getArray(ArrayList<File> files) {
		if(files == null) return new File[0];
		File[] ret = new File[files.size()];
		for(int i = 0; i < ret.length; i++)
			ret[i] = files.get(i);
		return ret;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return getSubDirectories(mDirectories[groupPosition])[childPosition];
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(convertView == null)
		{
			File selFile = (File)getItem(position);
			if(selFile == null) return view;
			if(selFile.list() != null && selFile.list().length > 0)
			{
				view = LayoutInflater.from(mContext).inflate(R.layout.directory_tree_list, null);
				view.setPadding(50, 4, 4, 4);
				final ListView mList = (ListView)view.findViewById(R.id.list);
				TextView mTitle = (TextView)view.findViewById(R.id.title);
				mList.setVisibility(View.GONE);
				ImageButton mPlus = (ImageButton)view.findViewById(R.id.btn_toggle);
				mPlus.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mList.setVisibility(mList.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
					}
				});
				mTitle.setText(selFile.getName());
				RecursiveDirectoryAdapter adapter = new RecursiveDirectoryAdapter(selFile, mContext);
				mList.setAdapter(adapter);
			} else {
				TextView tv = new TextView(mContext);
				tv.setText(selFile.getName());
				tv.setTextSize(20);
				tv.setPadding(40, 4, 4, 4);
				view = tv;
			}
		}
		return view;
	}

	public Object getItem(int groupPosition) {
		return mDirectories[groupPosition];
	}

	public int getCount() {
		if(mDirectories == null)
			return 0;
		return mDirectories.length;
	}

	public boolean hasStableIds() {
		return false;
	}

	public class FileComparer implements Comparator<File>
	{
		public int compare(File a, File b) 
		{
			return a.getName().compareTo(b.getName());
		}
		
	}

}
