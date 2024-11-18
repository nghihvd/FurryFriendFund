package org.example.furryfriendfund.controllers;


import org.springframework.core.io.Resource;
import org.example.furryfriendfund.respone.BaseResponse;
import org.example.furryfriendfund.respone.ResponseUtils;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@RestController
@RequestMapping("/images")
public class ImageController {

    @GetMapping("/{filename}")
    public ResponseEntity<?> getImage(@PathVariable String filename) {
        try{
            Path staticPath = Paths.get("static");
            Path imagePath = Paths.get("images");
            Path path = staticPath.resolve(imagePath)
                    .resolve(Objects.requireNonNull(filename));
            Resource resource =  new UrlResource(path.toUri());
            MediaType mediaType;
            String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            if(resource.exists() && resource.isReadable()) {
                switch (extension){
                    case "jpg":
                    case "jpeg":
                        mediaType = MediaType.IMAGE_JPEG;
                        break;
                    case "png":
                        mediaType = MediaType.IMAGE_PNG;
                        break;
                    case "gif":
                        mediaType = MediaType.IMAGE_GIF;
                        break;
                    default:
                        mediaType = MediaType.APPLICATION_OCTET_STREAM; // Nếu không xác định được

                }
                return ResponseEntity.ok().contentType(mediaType).header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"").body(resource);
            }

            return ResponseUtils.createErrorRespone("not found",null, HttpStatus.NOT_FOUND);
        } catch (MalformedURLException e) {
            return ResponseUtils.createErrorRespone("error",null, HttpStatus.BAD_REQUEST);
        }



    }
}
