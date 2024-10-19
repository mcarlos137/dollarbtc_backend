/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dollarbtc.backend.cryptocurrency.exchange.mail;

/**
 *
 * @author conamerica23
 */
public enum MailContentTypeAttached {
    XLS("application/vnd.ms-excel"), 
    XLSX("application/vnd.ms-excel"), 
    CSV("text/csv"),  
    PNG("image/png"), 
    JPEG("image/jpeg"), 
    JPG("image/jpeg"), 
    PDF("application/pdf"), 
    TXT("text/plain"),
    HTML("text/html"),
    HTM("text/html"),
    XHTML("application/xhtml+xml"),
    CSS("text/css"),
    DOC("application/msword"),
    DOCX("application/msword"),
    PPT("application/vnd.ms-powerpoint"),
    RAR("application/x-rar-compressed"),
    ZIP("application/zip"),
    XML("application/xml"),
    JSON("application/json"),
    JS("application/javascript"),
    ICO("image/x-icon"),
    EPUB("application/epub+zip"),
    GIF("image/gif"),
    DEFAULT("text/plain");
    private final String name;

    private MailContentTypeAttached(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
