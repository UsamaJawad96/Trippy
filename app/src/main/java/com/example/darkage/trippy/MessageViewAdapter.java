package com.example.darkage.trippy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageViewAdapter extends RecyclerView.Adapter{
    ArrayList<MessageClass> messageData;
    String dn;

    public class ViewHolderOutgoing extends RecyclerView.ViewHolder{
        public TextView messageTxt;
        public ImageView pic;
        public ViewHolderOutgoing(View itemView) {
            super(itemView);
            messageTxt=itemView.findViewById(R.id.senderText1);
            pic=itemView.findViewById(R.id.senderPicture1);
        }
    }

    public class ViewHolderIncoming extends RecyclerView.ViewHolder{
        public TextView senderName,messageTxt;
        public ImageView pic;

        public ViewHolderIncoming(View itemView) {
            super(itemView);
            senderName=itemView.findViewById(R.id.senderName);
            messageTxt=itemView.findViewById(R.id.senderText);
            pic=itemView.findViewById(R.id.senderPicture);
        }
    }

    public MessageViewAdapter(ArrayList<MessageClass> m){
        messageData=m;
        dn=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }


    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if (messageData.get(position).getSender().equals(dn))
        {
            return 0;
        }
        else
            return 1;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0)
        {
            View v =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_send_layout, parent, false);

            ViewHolderOutgoing vh=new ViewHolderOutgoing(v);
            return vh;
        }

        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_recieve_layout, parent, false);

        ViewHolderIncoming vh=new ViewHolderIncoming(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case 0:
                ViewHolderOutgoing vvh=(ViewHolderOutgoing) holder;

                if (messageData.get(position).isImage)
                {
                    vvh.messageTxt.setVisibility(View.GONE);
                    vvh.pic.setVisibility(View.VISIBLE);
                    byte[] decodedString = Base64.decode(messageData.get(position).getImage64(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    vvh.pic.setImageBitmap(decodedByte);
                }
                else
                {
                    vvh.messageTxt.setText(messageData.get(position).getmText());
                }
                break;

            case 1:
                ViewHolderIncoming vh=(ViewHolderIncoming) holder;

                vh.senderName.setText(messageData.get(position).getSender());
                if (messageData.get(position).isImage)
                {
                    vh.messageTxt.setVisibility(View.GONE);
                    vh.pic.setVisibility(View.VISIBLE);
                    byte[] decodedString = Base64.decode(messageData.get(position).getImage64(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    vh.pic.setImageBitmap(decodedByte);
                }
                else
                {
                    vh.messageTxt.setText(messageData.get(position).getmText());
                }

                break;

        }
    }

    @Override
    public int getItemCount() {
            if (messageData==null)
                return 0;
            return messageData.size();
    }
}
