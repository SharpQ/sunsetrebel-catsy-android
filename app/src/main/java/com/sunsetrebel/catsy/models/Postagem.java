package com.sunsetrebel.catsy.models;


public class Postagem {

    private String nome;
    private String postagem;
    private String horario;
    private String event_description;
    private String event_creator_name;
    private int event_creator_photo;
    private int imagem;

    public Postagem() {
    }

    public Postagem(String nome, String postagem, String horario, String event_description, String event_creator_name, int imagem, int event_creator_photo) {
        this.nome = nome;
        this.postagem = postagem;
        this.horario = horario;
        this.event_description = event_description;
        this.event_creator_name = event_creator_name;
        this.event_creator_photo = event_creator_photo;
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

    public String get_event_description() {
        return event_description;
    }

    public void set_event_description(String event_description) {
        this.event_description = event_description;
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

    public int get_event_creator_photo() {
        return event_creator_photo;
    }

    public void set_event_creator_photo(int event_creator_photo) {
        this.event_creator_photo = event_creator_photo;
    }
}
