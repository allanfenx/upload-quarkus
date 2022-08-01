package br.com.upload.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import br.com.upload.entity.FileObject;
import br.com.upload.entity.FormData;
import br.com.upload.entity.Profile;
import br.com.upload.repository.ProfileRepository;
import br.com.upload.views.ProfileS3View;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ApplicationScoped
public class S3Service {

    @ConfigProperty(name = "bucket.name")
    String bucketName;

    @Inject
    ProfileRepository repository;

    @Inject
    S3Client s3;

    public ProfileS3View listS3() {
        List<Profile> profiles = repository.listAll();

        ListObjectsRequest listRequest = ListObjectsRequest.builder().bucket(bucketName).build();

        List<FileObject> listObjectsRequest = s3.listObjects(listRequest).contents().stream()
                .map(FileObject::from)
                .collect(Collectors.toList());

        ProfileS3View profileS3View = new ProfileS3View(profiles, listObjectsRequest);

        return profileS3View;
    }

    public ProfileS3View findOne(Long id) {

        Optional<Profile> profileOp = repository.findByIdOptional(id);

        if (profileOp.isEmpty()) {
            throw new RuntimeException("File not found");
        }

        Profile profile = profileOp.get();

        ListObjectsRequest listRequest = ListObjectsRequest.builder().bucket(bucketName).build();

        List<FileObject> listObjectsRequest = s3.listObjects(listRequest).contents().stream()
                .map(FileObject::from)
                .filter(item -> item.getObjectKey().equals(profile.getKeyName()))
                .collect(Collectors.toList());

        ProfileS3View profileS3View = new ProfileS3View(profile, listObjectsRequest.get(0));

        return profileS3View;
    }

    @Transactional
    public Profile sendS3(FormData data) {

        List<String> mimetype = Arrays.asList("image/jpg", "image/jpeg", "image/gif", "image/png");

        if (!mimetype.contains(data.getFile().contentType())) {
            throw new RuntimeException("File not suported");
        }

        if (data.getFile().size() > 1024 * 1024 * 4) {
            throw new RuntimeException("File much large");
        }

        Profile profile = new Profile();

        String fileName = UUID.randomUUID() + "-" + data.getFile().fileName();

        profile.setOriginalName(data.getFile().fileName());

        profile.setKeyName(fileName);

        profile.setMimetype(data.getFile().contentType());

        profile.setFilesize(data.getFile().size());

        profile.setCreated_at(new Date());

        repository.persist(profile);

        PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucketName)
                .key(fileName)
                .contentType(data.getFile().contentType())
                .build();

        s3.putObject(objectRequest, RequestBody.fromFile(data.getFile().filePath()));

        return profile;
    }

    @Transactional
    public void removeS3(Long id) {

        Optional<Profile> profileOp = repository.findByIdOptional(id);

        if (profileOp.isEmpty()) {
            throw new RuntimeException("File not found");
        }

        Profile profile = profileOp.get();

        repository.delete(profile);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName)
                .key(profile.getKeyName()).build();

        s3.deleteObject(deleteObjectRequest);
    }
}
