package com.example.damas.Entities;

import com.example.damas.Enums.Cor;
import com.example.damas.Enums.TipoPeca;

public class Peca {
    private Cor cor;
    private TipoPeca tipo;

    public Peca(Cor cor) {
        this.cor = cor;
        this.tipo = TipoPeca.NORMAL;
    }


    public boolean dama(){
        return this.tipo == TipoPeca.DAMA;
    }

    public void promover(){
        this.tipo = TipoPeca.DAMA;
    }

    public Cor getCor() {
        return cor;
    }

    public TipoPeca getTipo() {
        return tipo;
    }

}
