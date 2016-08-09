package org.codeforafrica.citizenreporter.eNCA.ui.posts.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.expandablelayout.library.ExpandableLayoutListView;

import org.codeforafrica.citizenreporter.eNCA.R;
import org.codeforafrica.citizenreporter.eNCA.WordPress;
import org.codeforafrica.citizenreporter.eNCA.models.Post;
import org.codeforafrica.citizenreporter.eNCA.models.PostLocation;
import org.codeforafrica.citizenreporter.eNCA.ui.posts.Question;
import org.wordpress.android.util.EditTextUtils;
import org.wordpress.android.util.GeocoderUtils;
import org.wordpress.android.util.helpers.LocationHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;



public class GuideArrayAdapter extends ArrayAdapter<Question> implements 
        View.OnClickListener, TextView.OnEditorActionListener {

    Activity mActivity;
    Context mContext;
    int layoutResourceId;
    Question data[] = null;
    Post post;
    ExpandableLayoutListView listView;
    Dialog summaryDialog;
    Button enableLocation;
    Button submitButton;

    public GuideArrayAdapter(Context mContext, Activity mActivity, int layoutResourceId, Question[] data, Post _post, ExpandableLayoutListView _listView) {
        super(mActivity, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mActivity = mActivity;
        this.data = data;
        this.post = _post;
        this.listView = _listView;
        this.mContext = mContext;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        QuestionHolder holder = null;
        if(row == null)
        {
            LayoutInflater inflater = ((ActionBarActivity)mActivity).getLayoutInflater();
            row = inflater.inflate(R.layout.view_row, parent, false);

            holder = new QuestionHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.header_text);
            holder.txtContent = (TextView)row.findViewById(R.id.content_text);
            holder.dateOptions = (TextView)row.findViewById(R.id.dateOption);
            //if post is local draft change prompt
            if(!post.isLocalDraft()){
                holder.txtContent.setText("");
            }

            holder.datePicker = (TextView)row.findViewById(R.id.pick_date);

            holder.filledButton = (ImageView)row.findViewById(R.id.filledButton);

            row.setTag(holder);
        }
        else
        {
            holder = (QuestionHolder)row.getTag();
        }

        final Question question = data[position];
        holder.txtTitle.setText(question.title);
        //check for defaults
        if(question.answer!=null){
            if(!question.answer.equals("")){
                holder.txtContent.setText(question.answer);
                holder.filledButton.setColorFilter(mActivity.getResources().getColor(R.color.alert_green), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }
        final QuestionHolder finalHolder = holder;
        holder.txtContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(post.isLocalDraft()) {
                    listView.smoothScrollToPosition(position);

                    if (position == 3) {
                        showLocationDialog(finalHolder, finalHolder.txtContent, finalHolder.filledButton, question, position);
                    } else {
                        showCreateSummaryDialog(finalHolder, finalHolder.txtContent, finalHolder.filledButton, question, position);
                    }
                }
            }
        });

        //if when, show datepicker
        if(position == 4){
            if(post.getQwhen_date()!=null){
                holder.datePicker.setText(post.getQwhen_date());
            }

            holder.datePicker.setVisibility(View.VISIBLE);
            holder.dateOptions.setVisibility(View.VISIBLE);
            holder.datePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(post.isLocalDraft())
                        datePickerDialog.show();
                }
            });
            setDateTimeField(holder.datePicker, finalHolder.filledButton);
        }else{

        }

        return row;
    }

    private DatePickerDialog datePickerDialog;

    private void setDateTimeField(final TextView datePicker, final ImageView filledButton) {
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String pickedDate = dateFormatter.format(newDate.getTime());
                datePicker.setText(pickedDate);

                filledButton.setColorFilter(mActivity.getResources().getColor(R.color.alert_green), android.graphics.PorterDuff.Mode.MULTIPLY);

                post.setQwhen_date(pickedDate);
                WordPress.wpDB.updatePost(post);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    public void showLocationDialog(final QuestionHolder holder, final TextView displaySummary, final ImageView filledButton, final Question question, final int selectedItem){
        summaryDialog = new Dialog(mActivity);
        summaryDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        summaryDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        summaryDialog.setContentView(R.layout.location_fragment);
        summaryDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        submitButton = (Button)summaryDialog.findViewById(R.id.submitButton);
        submitButton.setEnabled(false);

        summaryDialog.findViewById(R.id.closeDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                summaryDialog.dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String summary = mLocationEditText.getText().toString();

                if (summary.trim().length() > 0) {
                    question.setAnswer(summary);
                    holder.answer = summary;

                    displaySummary.setText(summary);
                    filledButton.setColorFilter(mActivity.getResources().getColor(R.color.alert_green), android.graphics.PorterDuff.Mode.MULTIPLY);

                }

                summaryDialog.dismiss();
            }
        });

        enableLocation = (Button)summaryDialog.findViewById(R.id.enableLocation);
        enableLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mActivity.startActivity(callGPSSettingIntent);
            }
        });

        initLocation();

        summaryDialog.show();
    }

    public void showCreateSummaryDialog(final QuestionHolder holder, final TextView displaySummary, final ImageView filledButton, final Question question, final int selectedItem){
        final Dialog dialog = new Dialog(mActivity);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.fivew_fragment);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        final Button submitButton = (Button)dialog.findViewById(R.id.submitButton);
        submitButton.setEnabled(false);

        final EditText editTextSummary = (EditText)dialog.findViewById(R.id.editTextSummary);

        String summary = "";

        //find current value of summary
        summary = "" + displaySummary.getText().toString();
        //if it's not default & not empty edit editTextSummary
        if((!summary.equals("")) && (!summary.equals(mContext.getResources().getString(R.string.empty_answer)))){
            editTextSummary.setText(summary);
            submitButton.setEnabled(true);
        }else{
            summary = "";
        }

        editTextSummary.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String newSummary = "" + editTextSummary.getText().toString();
                if (newSummary.length() > 0) {
                    submitButton.setEnabled(true);
                } else {
                    submitButton.setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        dialog.findViewById(R.id.closeDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String summary = editTextSummary.getText().toString();

                if (summary.trim().length() > 0) {
                    question.setAnswer(summary);
                    holder.answer = summary;

                    displaySummary.setText(summary);
                    filledButton.setColorFilter(mActivity.getResources().getColor(R.color.alert_green), android.graphics.PorterDuff.Mode.MULTIPLY);
                    switch(selectedItem){
                        case 0:
                            post.setTitle(summary);
                            break;
                        case 1:
                            post.setQwhy(summary);
                            break;
                        case 2:
                            post.setKeywords(summary);
                            break;
                        case 3:
                            //Handled on showLocation dialog :post.setQwhere(summary);
                            break;
                        case 4:
                            post.setQwhen(summary);
                            break;
                        case 5:
                            post.setQhow(summary);
                            break;
                    }
                    WordPress.wpDB.updatePost(post);
                } else {
                    displaySummary.setText("");
                    filledButton.setColorFilter(mActivity.getResources().getColor(R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);

                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    static class QuestionHolder
    {
        TextView txtTitle;
        TextView txtContent;
        TextView dateOptions;

        ImageView filledButton;
        String answer;

        TextView datePicker;
    }




    /**
     * Location methods
     */

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.locationText) {
            viewLocation();
        } else if (id == R.id.updateLocation) {
            showLocationSearch();
        } else if (id == R.id.removeLocation) {
            removeLocation();
            showLocationAdd();
        } else if (id == R.id.addLocation) {
            showLocationSearch();
        } else if (id == R.id.searchLocation) {
            searchLocation();
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return false;
    }

    private static enum LocationStatus {NONE, FOUND, NOT_FOUND, SEARCHING}
    /*
     * retrieves and displays the friendly address for a lat/long location
     */
    private class GetAddressTask extends AsyncTask<Double, Void, Address> {
        double latitude;
        double longitude;

        @Override
        protected void onPreExecute() {
            setLocationStatus(LocationStatus.SEARCHING);
            showLocationView();
        }

        @Override
        protected Address doInBackground(Double... args) {
            // args will be the latitude, longitude to look up
            latitude = args[0];
            longitude = args[1];

            return GeocoderUtils.getAddressFromCoords(mActivity, latitude, longitude);
        }

        protected void onPostExecute(Address address) {
            setLocationStatus(LocationStatus.FOUND);
            if (address == null) {
                // show lat/long when Geocoder fails (ugly, but better than not showing anything
                // or showing an error since the location has been assigned to the post already)
                updateLocationText(Double.toString(latitude) + ", " + Double.toString(longitude));
            } else {
                String locationName = GeocoderUtils.getLocationNameFromAddress(address);
                updateLocationText(locationName);
            }
        }
    }

    private class GetCoordsTask extends AsyncTask<String, Void, Address> {
        @Override
        protected void onPreExecute() {
            setLocationStatus(LocationStatus.SEARCHING);
            showLocationView();
        }

        @Override
        protected Address doInBackground(String... args) {
            String locationName = args[0];

            return GeocoderUtils.getAddressFromLocationName(mActivity, locationName);
        }

        @Override
        protected void onPostExecute(Address address) {
            setLocationStatus(LocationStatus.FOUND);
            showLocationView();

            if (address != null) {
                double[] coordinates = GeocoderUtils.getCoordsFromAddress(address);
                setLocation(coordinates[0], coordinates[1]);

                String locationName = GeocoderUtils.getLocationNameFromAddress(address);
                updateLocationText(locationName);
            } else {
                showLocationNotAvailableError();
                showLocationSearch();
            }
        }
    }

    private LocationHelper.LocationResult locationResult = new LocationHelper.LocationResult() {
        @Override
        public void gotLocation(final Location location) {
            if (mActivity == null)
                return;
            // note that location will be null when requesting location fails
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    setLocation(location);
                }
            });
        }
    };

    private View mLocationAddSection;
    private View mLocationSearchSection;
    private View mLocationViewSection;
    private TextView mLocationText;
    private EditText mLocationEditText;
    private Button mButtonSearchLocation;
    private PostLocation mPostLocation;
    private LocationHelper mLocationHelper;
    
    private TextWatcher mLocationEditTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            String buttonText;
            if (s.length() > 0) {
                submitButton.setEnabled(true);
                buttonText = mContext.getResources().getString(R.string.search_location);
            } else {
                submitButton.setEnabled(false);
                buttonText = mContext.getResources().getString(R.string.search_current_location);
            }
            mButtonSearchLocation.setText(buttonText);
        }
    };


    /*
     * called when activity is created to initialize the location provider, show views related
     * to location if enabled for this blog, and retrieve the current location if necessary
     */
    private void initLocation() {
        // show the location views if a provider was found and this is a post on a blog that has location enabled
        if (hasLocationProvider() && post.supportsLocation()) {
            enableLocation.setVisibility(View.GONE);

            View locationRootView = ((ViewStub) summaryDialog.findViewById(R.id.stub_post_location_settings)).inflate();

            mLocationText = (TextView) locationRootView.findViewById(R.id.locationText);
            mLocationText.setOnClickListener(this);

            mLocationAddSection = locationRootView.findViewById(R.id.sectionLocationAdd);
            mLocationSearchSection = locationRootView.findViewById(R.id.sectionLocationSearch);
            mLocationViewSection = locationRootView.findViewById(R.id.sectionLocationView);

            Button addLocation = (Button) locationRootView.findViewById(R.id.addLocation);
            addLocation.setOnClickListener(this);

            mButtonSearchLocation = (Button) locationRootView.findViewById(R.id.searchLocation);
            mButtonSearchLocation.setOnClickListener(this);

            mLocationEditText = (EditText) locationRootView.findViewById(R.id.searchLocationText);
            mLocationEditText.setOnEditorActionListener(this);
            mLocationEditText.addTextChangedListener(mLocationEditTextWatcher);

            Button updateLocation = (Button) locationRootView.findViewById(R.id.updateLocation);
            Button removeLocation = (Button) locationRootView.findViewById(R.id.removeLocation);
            updateLocation.setOnClickListener(this);
            removeLocation.setOnClickListener(this);

            // if this post has location attached to it, look up the location address
            if (post.hasLocation()) {
                showLocationView();

                PostLocation location = post.getLocation();
                setLocation(location.getLatitude(), location.getLongitude());

                submitButton.setEnabled(true);
            } else {
                showLocationAdd();
            }
        }else{
            enableLocation.setVisibility(View.VISIBLE);
        }
    }

    private boolean hasLocationProvider() {
        boolean hasLocationProvider = false;
        LocationManager locationManager = (LocationManager) mActivity.getSystemService(Activity.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        if (providers != null) {
            for (String providerName : providers) {
                if (providerName.equals(LocationManager.GPS_PROVIDER)
                        || providerName.equals(LocationManager.NETWORK_PROVIDER)) {
                    hasLocationProvider = true;
                }
            }
        }
        return hasLocationProvider;
    }

    private void showLocationSearch() {
        mLocationAddSection.setVisibility(View.GONE);
        mLocationSearchSection.setVisibility(View.VISIBLE);
        mLocationViewSection.setVisibility(View.GONE);

        EditTextUtils.showSoftInput(mLocationEditText);
    }

    private void showLocationAdd() {
        mLocationAddSection.setVisibility(View.VISIBLE);
        mLocationSearchSection.setVisibility(View.GONE);
        mLocationViewSection.setVisibility(View.GONE);
    }

    private void showLocationView() {
        mLocationAddSection.setVisibility(View.GONE);
        mLocationSearchSection.setVisibility(View.GONE);
        mLocationViewSection.setVisibility(View.VISIBLE);
    }

    private void searchLocation() {
        EditTextUtils.hideSoftInput(mLocationEditText);
        String location = EditTextUtils.getText(mLocationEditText);

        removeLocation();

        if (location.isEmpty()) {
            fetchCurrentLocation();
        } else {
            new GetCoordsTask().execute(location);
        }
    }

    /*
     * get the current location
     */
    private void fetchCurrentLocation() {
        if (mLocationHelper == null) {
            mLocationHelper = new LocationHelper();
        }
        boolean canGetLocation = mLocationHelper.getLocation(mActivity, locationResult);

        if (canGetLocation) {
            setLocationStatus(LocationStatus.SEARCHING);
            showLocationView();
        } else {
            setLocation(null);
            showLocationNotAvailableError();
            showLocationAdd();
        }
    }

    /*
     * called when location is retrieved/updated for this post - looks up the address to
     * display for the lat/long
     */
    private void setLocation(Location location) {
        if (location != null) {
            setLocation(location.getLatitude(), location.getLongitude());
        } else {
            updateLocationText(mContext.getString(R.string.location_not_found));
            setLocationStatus(LocationStatus.NOT_FOUND);
        }
    }

    private void setLocation(double latitude, double longitude) {
        mPostLocation = new PostLocation(latitude, longitude);
        new GetAddressTask().execute(mPostLocation.getLatitude(), mPostLocation.getLongitude());
    }

    private void removeLocation() {
        mPostLocation = null;
        post.unsetLocation();

        updateLocationText("");
        setLocationStatus(LocationStatus.NONE);
    }

    private void viewLocation() {
        if (mPostLocation != null && mPostLocation.isValid()) {
            String uri = "geo:" + mPostLocation.getLatitude() + "," + mPostLocation.getLongitude();
            mActivity.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
        } else {
            showLocationNotAvailableError();
            showLocationAdd();
        }
    }

    private void showLocationNotAvailableError() {
        Toast.makeText(mContext, mContext.getResources().getText(R.string.location_not_found), Toast.LENGTH_SHORT).show();
    }

    private void updateLocationText(String locationName) {
        mLocationText.setText(locationName);
        mLocationEditText.setText(locationName);

        post.setStringLocation(locationName);
        WordPress.wpDB.updatePost(post);
    }

    /*
     * changes the left drawable on the location text to match the passed status
     */

    private void setLocationStatus(LocationStatus status) {


        // animate location text when searching
        if (status == LocationStatus.SEARCHING) {
            updateLocationText(mContext.getString(R.string.loading));

            Animation aniBlink = AnimationUtils.loadAnimation(mActivity, R.anim.blink);
            if (aniBlink != null) {
                mLocationText.startAnimation(aniBlink);
            }
        } else {
            mLocationText.clearAnimation();
        }

        final int drawableId;
        switch (status) {
            case FOUND:
                drawableId = R.drawable.ic_action_location_found;
                break;
            case NOT_FOUND:
                drawableId = R.drawable.ic_action_location_off;
                break;
            case SEARCHING:
                drawableId = R.drawable.ic_action_location_searching;
                break;
            case NONE:
                drawableId = 0;
                break;
            default:
                return;
        }

        mLocationText.setCompoundDrawablesWithIntrinsicBounds(drawableId, 0, 0, 0);
    }
}