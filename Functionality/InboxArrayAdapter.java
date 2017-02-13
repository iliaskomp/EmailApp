package com.iliaskomp.emailapp.Functionality;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.iliaskomp.emailapp.Data.EmailForInbox;
import com.iliaskomp.emailapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by elias on 11/02/17.
 */

public class InboxArrayAdapter extends ArrayAdapter<EmailForInbox> {
    private static final int MESSAGE_SUBSTRING_LENGTH = 35;

    private Context mContext;
    private List<EmailForInbox> mEmails;

    public InboxArrayAdapter(Context context, List<EmailForInbox> emails) {
        super(context, R.layout.list_item_email, emails);
        mContext = context;
        mEmails = emails;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.list_item_email, parent, false);

        TextView senderView = (TextView) itemView.findViewById(R.id.textViewSender);
        TextView dateView = (TextView) itemView.findViewById(R.id.textViewDate);
        TextView subjectView = (TextView) itemView.findViewById(R.id.textViewSubject);
        TextView messageView = (TextView) itemView.findViewById(R.id.textViewMessage);

        String messageString = mEmails.get(position).getMessage();

        senderView.setText(mEmails.get(position).getSender());
        dateView.setText(formatDateForInbox(mEmails.get(position).getSentDate()));
        subjectView.setText(mEmails.get(position).getSubject());
        messageView.setText(messageString.length() >= MESSAGE_SUBSTRING_LENGTH
                ? messageString.substring(0, MESSAGE_SUBSTRING_LENGTH) : messageString);

        return itemView;
    }

    private String formatDateForInbox(Date date) {
        String dateString = date.toString();
        SimpleDateFormat spf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

        try {
            Date newDate=spf.parse(dateString);
            spf= new SimpleDateFormat("dd MMM yyyy");
            dateString = spf.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }
}
