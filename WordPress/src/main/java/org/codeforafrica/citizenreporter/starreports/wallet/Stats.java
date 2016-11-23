package org.codeforafrica.citizenreporter.starreports.wallet;

import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.BarChartView;
import com.db.chart.view.ChartView;
import com.db.chart.view.HorizontalBarChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;

import org.codeforafrica.citizenreporter.starreports.R;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by nick on 15/08/15.
 */
public class Stats extends ActionBarActivity {


    private LineChartView mChartThree;
    private ImageButton mPlayThree;
    private ImageButton mPlayTwo;
    private ImageButton mPlayOne;
    private boolean mUpdateThree;
    private String[] mLabelsThree= {"00", "01", "02", "03", "04", "05"};
    private final float[][] mValuesThree = {  {4f, 5f, 4f, 8f, 2f, 3f}};


    /** First chart */
    private BarChartView mChartOne;
    private boolean mUpdateOne;
    private String[] mLabelsOne= {"00", "01", "02", "03", "04", "05"};
    private final float [][] mValuesOne = {{9f, 7f, 5f, 4f, 10f, 9f}};


    /** Second chart */
    private HorizontalBarChartView mChartTwo;
    private boolean mUpdateTwo;
    private final String[] mLabelsTwo= {"Audio", "Video", "Images"};
    private final float [] mValuesTwo = {23f, 34f, 55f};
    private TextView mTextViewTwo;
    private TextView mTextViewMetricTwo;
    private ArrayList<String> months;
    @Override
    public void onCreate(Bundle onSavedInstance){
        super.onCreate(onSavedInstance);

        setContentView(R.layout.wallet_stats_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.stats));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get list of last months
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        months = new ArrayList<>();

        months.add(getMonthForInt(currentMonth));
        for(int i = 0; i<5; i++){
            currentMonth--;

            if(currentMonth<0){
                currentMonth += 12;
            }

            months.add(getMonthForInt(currentMonth));

        }
        Collections.reverse(months);

        mLabelsThree = months.toArray(new String[months.size()]);
        mLabelsOne = months.toArray(new String[months.size()]);

        // Init third chart
        mUpdateThree = true;
        mChartThree = (LineChartView) findViewById(R.id.linechart3);
        mPlayThree = (ImageButton) findViewById(R.id.play3);
        mPlayThree.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mUpdateThree)
                    updateLineChart(2, mChartThree, mPlayThree);
                else
                    dismissLineChart(2, mChartThree, mPlayThree);
                mUpdateThree = !mUpdateThree;
            }
        });


        // Init first chart
        mUpdateOne = true;
        mChartOne = (BarChartView) findViewById(R.id.barchart1);
        mPlayOne = (ImageButton) findViewById(R.id.play1);
        mPlayOne.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mUpdateOne)
                    updateChart(0, mChartOne, mPlayOne);
                else {
                    dismissChart(0, mChartOne, mPlayOne);
                }
                mUpdateOne = !mUpdateOne;
                dismissChart(0, mChartOne, mPlayOne);
            }
        });

        // Init second chart
        mUpdateTwo = true;
        mChartTwo = (HorizontalBarChartView) findViewById(R.id.barchart2);
        mPlayTwo = (ImageButton) findViewById(R.id.play2);
        mPlayTwo.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mUpdateTwo)
                    updateChart(1, mChartTwo, mPlayTwo);
                else
                    dismissChart(1, mChartTwo, mPlayTwo);
                mUpdateTwo = !mUpdateTwo;
            }
        });
        mTextViewTwo = (TextView) findViewById(R.id.value);
        mTextViewMetricTwo = (TextView) findViewById(R.id.metric);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            mTextViewTwo.setAlpha(0);
            mTextViewMetricTwo.setAlpha(0);
        }else{
            mTextViewTwo.setVisibility(View.INVISIBLE);
            mTextViewMetricTwo.setVisibility(View.INVISIBLE);
        }

        mTextViewTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    mTextViewTwo.animate().alpha(0).setDuration(100);
                    mTextViewMetricTwo.animate().alpha(0).setDuration(100);
                }
            }
        });

        showLineChart(2, mChartThree, mPlayThree);
        showChart(0, mChartOne, mPlayOne);
        showChart(1, mChartTwo, mPlayTwo);
    }

    /**
     * Show a CardView chart
     * @param tag   Tag specifying which chart should be dismissed
     * @param chart   Chart view
     * @param btn    Play button
     */
    private void showLineChart(final int tag, final LineChartView chart, final ImageButton btn){
        dismissLinePlay(btn);
        Runnable action =  new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        showLinePlay(btn);
                    }
                }, 500);
            }
        };

        switch(tag){
            case 0:
            case 1:
            case 2:
                produceLineThree(chart, action); break;
            default:
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Update the values of a CardView chart
     * @param tag   Tag specifying which chart should be dismissed
     * @param chart   Chart view
     * @param btn    Play button
     */
    private void updateLineChart(final int tag, final LineChartView chart, ImageButton btn){

        dismissLinePlay(btn);

        switch(tag){
            case 0:
            case 1:
            case 2:
                updateLineThree(chart); break;
            default:
        }
    }


    /**
     * Dismiss a CardView chart
     * @param tag   Tag specifying which chart should be dismissed
     * @param chart   Chart view
     * @param btn    Play button
     */
    private void dismissLineChart(final int tag, final LineChartView chart, final ImageButton btn){

        dismissLinePlay(btn);

        Runnable action =  new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        showLinePlay(btn);
                        showLineChart(tag, chart, btn);
                    }
                }, 500);
            }
        };

        switch(tag){
            case 0:
            case 1:
            case 2:
                dismissLineThree(chart, action); break;
            default:
        }
    }


    /**
     * Show CardView play button
     * @param btn    Play button
     */
    private void showLinePlay(ImageButton btn){
        btn.setEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            btn.animate().alpha(1).scaleX(1).scaleY(1);
        else
            btn.setVisibility(View.VISIBLE);
    }


    /**
     * Dismiss CardView play button
     * @param btn    Play button
     */
    private void dismissLinePlay(ImageButton btn){
        btn.setEnabled(false);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            btn.animate().alpha(0).scaleX(0).scaleY(0);
        else
            btn.setVisibility(View.INVISIBLE);
    }

    public void produceLineThree(LineChartView chart, Runnable action){

        Tooltip tip = new Tooltip(Stats.this, R.layout.linechart_three_tooltip, R.id.value);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f));

            tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0),
                    PropertyValuesHolder.ofFloat(View.SCALE_X,0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y,0f));
        }

        chart.setTooltips(tip);

        LineSet dataset = new LineSet(mLabelsThree, mValuesThree[0]);
        dataset.setColor(Color.parseColor("#FF58C674"))
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(Color.parseColor("#FF58C674"))
                .setDotsColor(Color.parseColor("#eef1f6"));
        chart.addData(dataset);

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#308E9196"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(1f));

        chart.setBorderSpacing(1)
                .setAxisBorderValues(0, 10, 1)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor("#FF8E9196"))
                .setXAxis(false)
                .setYAxis(false)
                .setStep(2)
                .setBorderSpacing(Tools.fromDpToPx(5))
                .setGrid(ChartView.GridType.VERTICAL, gridPaint);

        Animation anim = new Animation().setEndAction(action);

        chart.show(anim);
    }

    public void updateLineThree(LineChartView chart){
        chart.dismissAllTooltips();
        chart.updateValues(0, mValuesThree[0]);
        chart.notifyDataUpdate();
    }

    public static void dismissLineThree(LineChartView chart, Runnable action){
        chart.dismissAllTooltips();
        chart.dismiss(new Animation().setEndAction(action));
    }

    /**
     * Show a CardView chart
     * @param tag   Tag specifying which chart should be dismissed
     * @param chart   Chart view
     * @param btn    Play button
     */
    private void showChart(final int tag, final ChartView chart, final ImageButton btn){
        dismissPlay(btn);
        Runnable action =  new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        showPlay(btn);
                    }
                }, 500);
            }
        };

        switch(tag) {
            case 0:
                produceOne(chart, action); break;
            case 1:
                produceTwo(chart, action); break;
            case 2:
            default:
        }
    }


    /**
     * Update the values of a CardView chart
     * @param tag   Tag specifying which chart should be dismissed
     * @param chart   Chart view
     * @param btn    Play button
     */
    private void updateChart(final int tag, final ChartView chart, ImageButton btn){

        dismissPlay(btn);

        switch(tag){
            case 0:
                updateOne(chart); break;
            case 1:
                updateTwo(chart); break;
            case 2:
            default:
        }
    }


    /**
     * Dismiss a CardView chart
     * @param tag   Tag specifying which chart should be dismissed
     * @param chart   Chart view
     * @param btn    Play button
     */
    private void dismissChart(final int tag, final ChartView chart, final ImageButton btn){

        dismissPlay(btn);

        Runnable action =  new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        showPlay(btn);
                        showChart(tag, chart, btn);
                    }
                }, 500);
            }
        };

        switch(tag){
            case 0:
                dismissOne(chart, action); break;
            case 1:
                dismissTwo(chart, action); break;
            case 2:
            default:
        }
    }


    /**
     * Show CardView play button
     * @param btn    Play button
     */
    private void showPlay(ImageButton btn){
        btn.setEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            btn.animate().alpha(1).scaleX(1).scaleY(1);
        else
            btn.setVisibility(View.VISIBLE);
    }


    /**
     * Dismiss CardView play button
     * @param btn    Play button
     */
    private void dismissPlay(ImageButton btn){
        btn.setEnabled(false);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            btn.animate().alpha(0).scaleX(0).scaleY(0);
        else
            btn.setVisibility(View.INVISIBLE);
    }



    /**
     *
     * Chart 1
     *
     */

    public void produceOne(ChartView chart, Runnable action){
        BarChartView barChart = (BarChartView) chart;

        barChart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                System.out.println("OnClick "+rect.left);
            }
        });

        Tooltip tooltip = new Tooltip(Stats.this, R.layout.barchart_one_tooltip);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            tooltip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
            tooltip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0));
        }
        barChart.setTooltips(tooltip);

        BarSet barSet = new BarSet(mLabelsOne, mValuesOne[0]);
        barSet.setColor(Color.parseColor("#a8896c"));
        barChart.addData(barSet);

        barChart.setSetSpacing(Tools.fromDpToPx(-15));
        barChart.setBarSpacing(Tools.fromDpToPx(35));
        barChart.setRoundCorners(Tools.fromDpToPx(2));

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#8986705C"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        barChart.setBorderSpacing(5)
                .setAxisBorderValues(0, 10, 2)
                .setGrid(BarChartView.GridType.FULL, gridPaint)
                .setYAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYLabels(YController.LabelPosition.NONE)
                .setLabelsColor(Color.parseColor("#86705c"))
                .setAxisColor(Color.parseColor("#86705c"));

        int[] order = {2, 1, 3, 0, 4, 5};
        final Runnable auxAction = action;
        Runnable chartOneAction = new Runnable() {
            @Override
            public void run() {
                showTooltipOne();
                auxAction.run();
            }
        };
        barChart.show(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(chartOneAction))
        //.show()
        ;
    }

    public void updateOne(ChartView chart){

        dismissTooltipOne();
        float [][]newValues = {{8.5f, 6.5f, 4.5f, 3.5f, 9f, 12f}};
        chart.updateValues(0, newValues[0]);
        chart.updateValues(1, newValues[1]);
        chart.notifyDataUpdate();
    }

    public void dismissOne(ChartView chart, Runnable action){

        dismissTooltipOne();
        int[] order = {0, 4, 1, 3, 2, 5};
        chart.dismiss(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(action));
    }


    private void showTooltipOne(){

        ArrayList<ArrayList<Rect>> areas = new ArrayList<>();
        areas.add(mChartOne.getEntriesArea(0));

        for(int i = 0; i < areas.size(); i++) {
            for (int j = 0; j < areas.get(i).size(); j++) {

                Tooltip tooltip = new Tooltip(Stats.this, R.layout.barchart_one_tooltip, R.id.value);
                tooltip.prepare(areas.get(i).get(j), mValuesOne[i][j]);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    tooltip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
                    tooltip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0));
                }
                mChartOne.showTooltip(tooltip, true);
            }
        }

    }


    private void dismissTooltipOne(){
        mChartOne.dismissAllTooltips();
    }


    /**
     *
     * Chart 2
     *
     */

    public void produceTwo(ChartView chart, Runnable action){
        HorizontalBarChartView horChart = (HorizontalBarChartView) chart;

        Tooltip tip = new Tooltip(Stats.this, R.layout.barchart_two_tooltip);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
            tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0));
        }

        horChart.setTooltips(tip);


        horChart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                mTextViewTwo.setText(Integer.toString((int) mValuesTwo[entryIndex]));
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    mTextViewTwo.animate().alpha(1).setDuration(200);
                    mTextViewMetricTwo.animate().alpha(1).setDuration(200);
                }else{
                    mTextViewTwo.setVisibility(View.VISIBLE);
                    mTextViewMetricTwo.setVisibility(View.VISIBLE);
                }
            }
        });

        horChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    mTextViewTwo.animate().alpha(0).setDuration(100);
                    mTextViewMetricTwo.animate().alpha(0).setDuration(100);
                }else{
                    mTextViewTwo.setVisibility(View.INVISIBLE);
                    mTextViewMetricTwo.setVisibility(View.INVISIBLE);
                }
            }
        });


        BarSet barSet = new BarSet();
        Bar bar;
        for(int i = 0; i < mLabelsTwo.length; i++){
            bar = new Bar(mLabelsTwo[i], mValuesTwo[i]);
            if(i == mLabelsTwo.length - 1 )
                bar.setColor(Color.parseColor("#b26657"));
            else if (i == 0)
                bar.setColor(Color.parseColor("#998d6e"));
            else
                bar.setColor(Color.parseColor("#506a6e"));
            barSet.addBar(bar);
        }
        horChart.addData(barSet);
        horChart.setBarSpacing(Tools.fromDpToPx(5));

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#aab6b2ac"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        horChart.setBorderSpacing(0)
                .setAxisBorderValues(0, 100, 5)
                .setGrid(HorizontalBarChartView.GridType.FULL, gridPaint)
                .setXAxis(false)
                .setYAxis(false)
                .setLabelsColor(Color.parseColor("#FF8E8A84"))
                .setXLabels(XController.LabelPosition.NONE);

        int[] order = { 2, 1, 0};
        horChart.show(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(action))
        ;
    }

    public void updateTwo(ChartView chart){

        chart.dismissAllTooltips();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            mTextViewTwo.animate().alpha(0).setDuration(100);
            mTextViewMetricTwo.animate().alpha(0).setDuration(100);
        }else{
            mTextViewTwo.setVisibility(View.INVISIBLE);
            mTextViewMetricTwo.setVisibility(View.INVISIBLE);
        }

        float[] valuesTwoOne = {48f, 63f, 94f};
        chart.updateValues(0, valuesTwoOne);
        chart.notifyDataUpdate();
    }

    public void dismissTwo(ChartView chart, Runnable action){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            mTextViewTwo.animate().alpha(0).setDuration(100);
            mTextViewMetricTwo.animate().alpha(0).setDuration(100);
        }else{
            mTextViewTwo.setVisibility(View.INVISIBLE);
            mTextViewMetricTwo.setVisibility(View.INVISIBLE);
        }

        chart.dismissAllTooltips();

        int[] order = {0, 1, 2};
        chart.dismiss(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(action));
    }

    String getMonthForInt(int num) {
        String month = "Jan";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

}
