package com.example.memoripy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.memoripy.R;
import com.example.memoripy.models.Card;

import java.util.List;

/**
 * Adaptador para mostrar las tarjetas en el GridView
 */
public class CardAdapter extends BaseAdapter {

    private final Context context;
    private final List<Card> cards;
    private final OnCardClickListener listener;
    private boolean isClickable = true;

    /**
     * Interface para manejar los clics en las tarjetas
     */
    public interface OnCardClickListener {
        void onCardClick(int position);
    }

    public CardAdapter(Context context, List<Card> cards, OnCardClickListener listener) {
        this.context = context;
        this.cards = cards;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int position) {
        return cards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.ivCard);
        final Card card = cards.get(position);

        // Establecer la imagen según el estado de la tarjeta
        if (card.isMatched()) {
            // Si la tarjeta ya ha sido emparejada, mostrar un fondo especial
            imageView.setImageResource(R.drawable.ic_launcher_foreground);
            imageView.setBackgroundResource(R.color.matched_card);
        } else if (card.isFlipped()) {
            // Si la tarjeta está volteada, mostrar su imagen
            imageView.setImageResource(card.getImageId());
            imageView.setBackgroundResource(android.R.color.white);
        } else {
            // Si la tarjeta está boca abajo, mostrar el reverso
            imageView.setImageResource(R.drawable.ic_launcher_background);
            imageView.setBackgroundResource(R.color.card_back);
        }

        // Establecer el listener de clic
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickable && listener != null) {
                    listener.onCardClick(position);
                }
            }
        });

        return convertView;
    }

    /**
     * Establece si las tarjetas pueden ser clickeables
     */
    public void setClickable(boolean clickable) {
        this.isClickable = clickable;
    }
}