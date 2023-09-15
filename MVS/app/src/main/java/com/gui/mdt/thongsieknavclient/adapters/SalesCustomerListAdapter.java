package com.gui.mdt.thongsieknavclient.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.mdt.thongsieknavclient.R;
import com.gui.mdt.thongsieknavclient.datamodel.Customer;
import com.gui.mdt.thongsieknavclient.dbhandler.CustomerDbHandler;
import com.gui.mdt.thongsieknavclient.ui.SalesCustomerDetailActivity;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by User on 7/20/2017.
 */

public class SalesCustomerListAdapter extends RecyclerView.Adapter<SalesCustomerListAdapter.ItemViewHolder> {

    private List<Customer> customerList;

    private int rowLayout;
    private Bundle extras;
    private Boolean IsMso = false, IsMvs = false;
    private String formName="",details="";
    private Activity activity;

    @Override
    public SalesCustomerListAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        final SalesCustomerListAdapter.ItemViewHolder viewHolder = new SalesCustomerListAdapter.ItemViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                Customer customerObject = customerList.get(position);
                String objAsJson = customerObject.toJson();

                if (extras != null) {
                    if (extras.containsKey("IsFromMso")) {
                        IsMso = extras.getBoolean("IsFromMso");
                    }
                    if (extras.containsKey("IsFromMvs")) {
                        IsMvs = extras.getBoolean("IsFromMvs");
                    }
                    if(extras.containsKey("formName"))
                    {
                        formName=extras.getString("formName");
                        if(extras.containsKey("details"))
                        {
                            details=extras.getString("details");
                        }
                    }
                }

                if (IsMso == true)
                {
                    Intent intent = new Intent(activity, SalesCustomerDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("_customerDetailObject", objAsJson);
                    intent.putExtra("IsPopupNeeded", true);
                    activity.startActivity(intent);
                }
                else if (IsMvs == true)
                {
                    Intent intent = new Intent(activity, SalesCustomerDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("_customerDetailObject", objAsJson);
                    intent.putExtra(activity.getResources().getString(R.string.intent_extra_form_name), formName);
                    activity.startActivity(intent);
                }
                else if(formName.equals(activity.getResources().getString(R.string.form_name_mso_sales_order)) && details.equals(activity.getResources().getString(R.string.intent_extra_add_customer))) //From MsoSalesOrderActivity -> Add Customer
                {
                    if(customerObject.isBlocked())
                    {
                        Toast.makeText(activity, activity.getResources().getString(R.string.message_customer_blocked), Toast.LENGTH_SHORT).show();
                    }
                    else 
                    {
                        if(validateDueDate(customerObject.getCode()))
                        {
                            Intent intent = new Intent();
                            intent.putExtra(activity.getResources().getString(R.string.intent_extra_details),activity.getResources().getString(R.string.intent_extra_add_customer));
                            intent.putExtra(activity.getResources().getString(R.string.customer_json_obj),objAsJson);
                            activity.setResult(RESULT_OK, intent);
                            activity.finish();
                        }
                        else
                        {
                            Toast.makeText(activity.getApplicationContext(), "Overdue invoice(s) found. Not allowed to add.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                else if(formName.equals("MsoPayment") && details.equals("AddCustomer")) //From MsoPaymentActivity -> Add Customer
                {
                        Intent intent = new Intent();
                        intent.putExtra("details","AddCustomer");
                        intent.putExtra("_customerDetailObject",objAsJson);
                        activity.setResult(RESULT_OK, intent);
                        activity.finish();
                }
                else if(formName.equals("MvsHome") && details.equals("AddCustomer")){

                    if(customerObject.isBlocked())
                    {
                        Toast.makeText(activity, activity.getResources().getString(R.string.message_customer_blocked), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent();
                        intent.putExtra(activity.getResources().getString(R.string.intent_extra_details),activity.getResources().getString(R.string.intent_extra_add_customer));
                        intent.putExtra(activity.getResources().getString(R.string.customer_json_obj),objAsJson);
                        activity.setResult(RESULT_OK, intent);
                        activity.finish();
                    }
                }
                else if(formName.equals(activity.getResources().getString(R.string.form_name_mvs_stock_Request)) && details.equals(activity.getResources().getString(R.string.intent_extra_add_customer))) // From MvsStockRequest -> AddNewCustomer
                {
                    if(customerObject.isBlocked())
                    {
                        Toast.makeText(activity, activity.getResources().getString(R.string.message_customer_blocked), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent();
                        intent.putExtra(activity.getResources().getString(R.string.intent_extra_details),activity.getResources().getString(R.string.intent_extra_add_customer));
                        intent.putExtra(activity.getResources().getString(R.string.customer_json_obj),objAsJson);
                        activity.setResult(RESULT_OK, intent);
                        activity.finish();
                    }
                }
                else if(formName.equals(activity.getResources().getString(R.string.form_name_mvs_sales_order)) && details.equals(activity.getResources().getString(R.string.intent_extra_add_customer))) // From MvsSalesOrder -> AddNewCustomer
                {
                    if(customerObject.isBlocked())
                    {
                        Toast.makeText(activity, activity.getResources().getString(R.string.message_customer_blocked), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent intent = new Intent();
                        intent.putExtra(activity.getResources().getString(R.string.intent_extra_details),activity.getResources().getString(R.string.intent_extra_add_customer));
                        intent.putExtra(activity.getResources().getString(R.string.customer_json_obj),objAsJson);
                        activity.setResult(RESULT_OK, intent);
                        activity.finish();
                    }
                }
                else //lds
                {
                    Intent intent = new Intent(activity, SalesCustomerDetailActivity.class);
                    intent.putExtra("IsPopupNeeded", true);
                    intent.putExtra("_customerDetailObject", objAsJson);
                    intent.putExtra("module",activity.getResources().getString(R.string.module_lds));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                }
            }
        });
        return viewHolder;
    }
    public boolean validateDueDate(String cusCode)
    {
        boolean isValidDueDate = true;
        CustomerDbHandler cusDb = new CustomerDbHandler(activity.getApplication());
        cusDb.open();

        isValidDueDate = cusDb.validateCustomerDueDate(cusCode,false);
        cusDb.close();

        return isValidDueDate;
    }

    @Override
    public void onBindViewHolder(SalesCustomerListAdapter.ItemViewHolder holder, int position) {
        String cusCode = "Code: " + customerList.get(position).getCode();
        holder.tvCustomerName.setText(customerList.get(position).getName());
        holder.tvCustomerCode.setText(cusCode);
        holder.tvAddress.setText(customerList.get(position).getAddress());

        holder.tvCustomerBalance.setText(
                "Balance: " +  String.format( "%.2f", customerList.get(position).getBalance() ));
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvCustomerName, tvCustomerCode, tvAddress,tvCustomerBalance;

        public ItemViewHolder(View v) {
            super(v);
            tvCustomerName = (TextView) v.findViewById(R.id.tvCustomerName);
            tvCustomerCode = (TextView) v.findViewById(R.id.tvCustomerCode);
            tvAddress = (TextView) v.findViewById(R.id.tvAddress);
            tvCustomerBalance = (TextView)v.findViewById(R.id.tvCustomerBalance);
        }
    }

    public SalesCustomerListAdapter(List<Customer> customers, Bundle extra_, int rowLayout,Activity activity_) {
        this.customerList = customers;
        this.rowLayout = rowLayout;
        this.extras = extra_;
        this.activity=activity_;
    }
}