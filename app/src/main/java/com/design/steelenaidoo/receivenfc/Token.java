package com.design.steelenaidoo.receivenfc;

public class Token {
    public final String responseString;
    public final Throwable error;

    public Token (String responseString,Throwable  error){
        this.responseString  = responseString;
        this.error = error;
}}
