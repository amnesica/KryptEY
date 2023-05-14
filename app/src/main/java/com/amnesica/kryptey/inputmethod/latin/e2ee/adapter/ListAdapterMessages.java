package com.amnesica.kryptey.inputmethod.latin.e2ee.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.amnesica.kryptey.inputmethod.R;
import com.amnesica.kryptey.inputmethod.signalprotocol.chat.StorageMessage;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ListAdapterMessages extends ArrayAdapter<Object> {

  private final ArrayList<Object> mListStorageMessages;
  private final String accountName;
  private static final String PATTERN_FORMAT = "dd.MM.yyyy HH:mm:ss";

  public ListAdapterMessages(
      Context context,
      int resource,
      ArrayList<Object> listStorageMessages,
      String accountName) {
    super(context, resource, listStorageMessages);
    this.mListStorageMessages = listStorageMessages;
    this.accountName = accountName;
  }

  public View getView(final int position, View convertView, ViewGroup parent) {

    final StorageMessage message = (StorageMessage) getItem(position);

    if (convertView == null) {
      LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
      convertView = layoutInflater.inflate(R.layout.e2ee_messages_element_view, null, false);
    }

    final TextView ownMessageTextView = convertView.findViewById(R.id.e2ee_own_messages_text_view_element);
    final TextView ownMessageTimestampTextView = convertView.findViewById(R.id.e2ee_own_messages_timestamp_text_view_element);

    final TextView othersMessageTextView = convertView.findViewById(R.id.e2ee_others_messages_text_view_element);
    final TextView othersMessageTimestampTextView = convertView.findViewById(R.id.e2ee_others_messages_timestamp_text_view_element);

    if (message != null && accountName != null && accountName.equals(message.getSenderUUID())) {
      ownMessageTextView.setText(message.getUnencryptedMessage());
      ownMessageTextView.setVisibility(View.VISIBLE);

      ownMessageTimestampTextView.setText(formatInstant(message.getTimestamp()));
      ownMessageTimestampTextView.setVisibility(View.VISIBLE);

      othersMessageTimestampTextView.setVisibility(View.GONE);
      othersMessageTextView.setVisibility(View.GONE);
    } else if (message != null && accountName != null && accountName.equals(message.getRecipientUUID())) {
      othersMessageTextView.setText(message.getUnencryptedMessage());
      othersMessageTextView.setVisibility(View.VISIBLE);

      othersMessageTimestampTextView.setText(formatInstant(message.getTimestamp()));
      othersMessageTimestampTextView.setVisibility(View.VISIBLE);

      ownMessageTimestampTextView.setVisibility(View.GONE);
      ownMessageTextView.setVisibility(View.GONE);
    }
    return convertView;
  }

  @Override
  public Object getItem(int position) {
    StorageMessage message = (StorageMessage) mListStorageMessages.get(position);
    return message;
  }

  private String formatInstant(Instant timestamp) {
    return DateTimeFormatter.ofPattern(PATTERN_FORMAT)
        .withZone(ZoneId.systemDefault()).format(timestamp);
  }
}
