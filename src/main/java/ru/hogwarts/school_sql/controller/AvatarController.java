package ru.hogwarts.school_sql.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school_sql.model.Avatar;
import ru.hogwarts.school_sql.service.AvatarService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    private final AvatarService avatarService;
    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long studentId, @RequestParam MultipartFile avatar) throws IOException {
        avatarService.uploadAvatar(studentId, avatar);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/avatar-from-db")
    public ResponseEntity<byte[]> downloadAvatarFromDb(@PathVariable Long id){
        final Avatar avatar = avatarService.findAvatar(id);
        if (avatar == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        return ResponseEntity.ok().headers(headers).body(avatar.getData());
    }

    @GetMapping(value = "/{id}/avatar-from-disc")
    public ResponseEntity<byte[]> downloadAvatarFromDisc(@PathVariable Long id) throws IOException {
        final Avatar avatar = avatarService.findAvatar(id);
        if (avatar == null) {
            return ResponseEntity.notFound().build();
        }
        final byte[] data = avatarService.getAvatarFromDisc(avatar);
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        return ResponseEntity.ok().headers(headers).body(data);
    }

    @GetMapping
    public List<Avatar> getAllAvatars(@RequestParam int pageNumber, @RequestParam int pageSize) {
        return avatarService.getAllAvatars(pageNumber, pageSize);
    }
}
