package com.amnesica.kryptey.inputmethod.latin.e2ee.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amnesica.kryptey.inputmethod.R;
import com.amnesica.kryptey.inputmethod.signalprotocol.chat.Contact;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ListAdapterContacts extends ArrayAdapter<Object> {

  private ArrayList<Object> mContacts;
  private ListAdapterContactInterface mListener;

  public ListAdapterContacts(
      Context context,
      int resource,
      ArrayList<Object> contacts) {
    super(context, resource, contacts);
    this.mContacts = contacts;
  }

  public View getView(final int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
      convertView = layoutInflater.inflate(R.layout.e2ee_contact_list_element_view, null, false);
    }

    final Contact contact = (Contact) getItem(position);

    final TextView firstNameTextView = convertView.findViewById(R.id.e2ee_contact_first_name_element);
    firstNameTextView.setText(contact.getFirstName());
    firstNameTextView.setOnClickListener(v -> mListener.selectContact(contact));

    final TextView lastNameTextView = convertView.findViewById(R.id.e2ee_contact_last_name_element);
    lastNameTextView.setText(contact.getLastName());
    lastNameTextView.setOnClickListener(v -> mListener.selectContact(contact));

    final ImageButton deleteContactButton = convertView.findViewById(R.id.e2ee_contact_button_delete_contact);
    deleteContactButton.setOnClickListener(v -> mListener.removeContact(contact));

    final ImageButton verifiedContactButton = convertView.findViewById(R.id.e2ee_verify_contact_verified_button);
    final ImageButton unverifiedContactButton = convertView.findViewById(R.id.e2ee_verify_contact_unverified_button);
    if (contact.isVerified()) {
      verifiedContactButton.setOnClickListener(v -> mListener.verifyContact(contact));
      verifiedContactButton.setVisibility(View.VISIBLE);
      unverifiedContactButton.setVisibility(View.INVISIBLE);
    } else {
      unverifiedContactButton.setOnClickListener(v -> mListener.verifyContact(contact));
      unverifiedContactButton.setVisibility(View.VISIBLE);
      verifiedContactButton.setVisibility(View.INVISIBLE);
    }

    return convertView;
  }

  @Override
  public Object getItem(int position) {
    Contact contact = null;
    try {
      contact = (Contact) mContacts.get(position);
    } catch (ClassCastException e) {
      LinkedHashMap linkedHashMap = (LinkedHashMap) mContacts.get(position);
      return new Contact((String) linkedHashMap.get("firstName"),
          (String) linkedHashMap.get("lastName"),
          (String) linkedHashMap.get("signalProtocolAddressName"),
          (Integer) linkedHashMap.get("deviceId"),
          (Boolean) linkedHashMap.get("verified"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return contact;
  }

  public void setListener(final ListAdapterContactInterface listener) {
    mListener = listener;
  }

  public interface ListAdapterContactInterface {
    void selectContact(Contact contact);

    void removeContact(Contact contact);

    void verifyContact(Contact contact);
  }
}
