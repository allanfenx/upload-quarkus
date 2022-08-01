package br.com.upload.entity;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public class FormData {

    @RestForm("file")
    private FileUpload file;

    public FileUpload getFile() {
        return file;
    }
}
