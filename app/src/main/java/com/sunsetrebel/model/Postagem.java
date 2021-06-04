package com.sunsetrebel.model;


public class Postagem {

    private String nome;
    private String postagem;
    private String horario;
    private String event_creator_name;
    private int imagem;

    public Postagem() {
    }

    public Postagem(String nome, String postagem, String horario, String event_creator_name, int imagem) {
        this.nome = nome;
        this.postagem = postagem;
        this.horario = horario;
        this.event_creator_name = event_creator_name;
        this.imagem = imagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPostagem() {
        return postagem;
    }

    public void setPostagem(String postagem) {
        this.postagem = postagem;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String get_event_creator_name() {
        return event_creator_name;
    }

    public void set_event_creator_name(String event_creator_name) {
        this.horario = event_creator_name;
    }

    public int getImagem() {
        return imagem;
    }

    public void setImagem(int imagem) {
        this.imagem = imagem;
    }
}
