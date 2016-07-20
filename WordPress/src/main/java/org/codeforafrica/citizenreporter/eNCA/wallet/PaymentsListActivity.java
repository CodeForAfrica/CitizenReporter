package org.codeforafrica.citizenreporter.eNCA.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.codeforafrica.citizenreporter.eNCA.R;
import org.codeforafrica.citizenreporter.eNCA.WordPress;
import org.codeforafrica.citizenreporter.eNCA.WordPressDB;
import org.codeforafrica.citizenreporter.eNCA.chat.ChatActivity;

import java.util.List;

public class PaymentsListActivity extends ActionBarActivity {
    ListView gridView;
    List<Payment> paymentsList;

    private PaymentsArrayAdapter adClass;

    private WordPressDB db;
    LinearLayout emptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payments_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.my_payments));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gridView = (ListView) findViewById(R.id.paymentsList);
        emptyView = (LinearLayout)findViewById(R.id.empty_view);

        db = WordPress.wpDB;

        paymentsList = db.getPayments();
        if(paymentsList.size()>0){
            emptyView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
        }


        adClass = new PaymentsArrayAdapter(this, paymentsList);
        gridView.setAdapter(adClass);

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class PaymentsArrayAdapter  extends ArrayAdapter<Payment> {
        Context context;
        private List<Payment> TextValue;
        private Payment payment;

        public PaymentsArrayAdapter(Context context, List<Payment> TextValue) {
            super(context, R.layout.payment_row, TextValue);
            this.context = context;
            this.TextValue= TextValue;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            payment = paymentsList.get(position);
            final ViewHolder holder;
            if(convertView == null)
            {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.payment_row, parent, false);
                holder.message = (TextView) convertView.findViewById(R.id.message_text);

                holder.confirmLayout = (LinearLayout) convertView.findViewById(R.id.confirm_layout);
                holder.confirmIcon = (ImageView) convertView.findViewById(R.id.confirm_icon);
                holder.confirmText = (TextView) convertView.findViewById(R.id.confirm_text);

                holder.disputeLayout = (LinearLayout) convertView.findViewById(R.id.dispute_layout);
                holder.disputeIcon = (ImageView) convertView.findViewById(R.id.dispute_icon);
                holder.disputeText = (TextView) convertView.findViewById(R.id.dispute_text);
                holder.followUpLayout = (RelativeLayout) convertView.findViewById(R.id.followup_layout);

                convertView.setTag(holder);
            }
            else
                holder = (ViewHolder) convertView.getTag();

            holder.message.setText(payment.getMessage());

            if(payment.getConfirmed().equals("1")){
                paymentConfirmed(payment, true, holder, false);
            }
            if(payment.getConfirmed().equals("-1")){
                paymentConfirmed(payment, true, holder, false);
            }

            holder.confirmLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    paymentConfirmed(payment, true, holder, true);
                }
            });

            holder.disputeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    paymentConfirmed(payment, false, holder, true);

                }
            });

            holder.followUpLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PaymentsListActivity.this, ChatActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            return convertView;

        }
        public class ViewHolder
        {
            TextView message;
            LinearLayout confirmLayout;
            LinearLayout disputeLayout;
            ImageView confirmIcon;
            ImageView disputeIcon;
            TextView disputeText;
            TextView confirmText;
            RelativeLayout followUpLayout;
        }

        @Override
        public long getItemId(int position) {
            //Unimplemented, because we aren't using Sqlite.
            return position;
        }

    }

    public void paymentConfirmed(Payment payment, boolean isConfirmed, PaymentsArrayAdapter.ViewHolder holder, boolean update){

        if(isConfirmed){
            holder.confirmIcon.setColorFilter(getApplicationContext().getResources().getColor(R.color.alert_green), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.confirmText.setText(getApplicationContext().getResources().getString(R.string.confirmed));
            holder.disputeLayout.setVisibility(View.GONE);
            holder.followUpLayout.setVisibility(View.GONE);
        }else{
            holder.disputeIcon.setColorFilter(getApplicationContext().getResources().getColor(R.color.alert_red), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.disputeText.setText(getApplicationContext().getResources().getString(R.string.disputed));
            holder.confirmLayout.setVisibility(View.GONE);
            //show follow up button: takes user to chatactivity
            holder.followUpLayout.setVisibility(View.VISIBLE);
        }

        if(update){

            String confirm;

            if(isConfirmed){
                confirm = "1";
                payment.setConfirmed("1");
                WordPress.wpDB.updatePayment(payment);
            }else{
                confirm = "0";
                payment.setConfirmed("-1");
                WordPress.wpDB.updatePayment(payment);
            }

            //send query
            new ConfirmPayment(payment.getPost(), payment.getRemoteID(), confirm).execute();
        }
    }
}