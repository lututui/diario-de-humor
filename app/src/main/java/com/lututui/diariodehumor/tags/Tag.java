package com.lututui.diariodehumor.tags;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Tag implements Parcelable {
    private final String nome;
    private final int cor;

    public Tag(String nome, int cor) {
        this.nome = nome;
        this.cor = cor;
    }

    public static final Creator<Tag> CREATOR = new Creator<>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            var nome = in.readString();
            var cor = in.readInt();

            return new Tag(nome, cor);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    public String getNome() {
        return nome;
    }

    public int getCor() {
        return cor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeInt(cor);
    }
}
