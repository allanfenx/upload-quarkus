package br.com.upload.views;

import java.util.List;

import br.com.upload.entity.FileObject;
import br.com.upload.entity.Profile;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class ProfileS3View {

    private List<Profile> profiles;

    private List<FileObject> fileObjects;

    private Profile profile;

    private FileObject fileObject;

    public ProfileS3View(List<Profile> profiles, List<FileObject> fileObjects) {
        this.profiles = profiles;
        this.fileObjects = fileObjects;
    }

    public ProfileS3View(Profile profile, FileObject fileObject) {
        this.profile = profile;
        this.fileObject = fileObject;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public List<FileObject> getFileObjects() {
        return fileObjects;
    }

    public FileObject getFileObject() {
        return fileObject;
    }

    public Profile getProfile() {
        return profile;
    }

}
