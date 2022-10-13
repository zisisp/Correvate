package com.vercator.correvate.multifileapi;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@Slf4j
public class APIController {

    int BUFFER_SIZE=4096;


    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello!");
    }

    /**
     * API endpoint reads file  form-data with the param "file". Reads the files, zips them and returns a response with the zipped files.
     * @param files The files to upload
     * @param response this is added by Spring
     * @return Returns a zip of the files that was submited in the POST
     */
    @PostMapping(path = "/zip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StreamingResponseBody> postRequest(@RequestParam("file") MultipartFile[] files,
    HttpServletResponse response) {
        StreamingResponseBody streamResponseBody = zipFiles(files, response);
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=files.zip");
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "0");
        return ResponseEntity.ok(streamResponseBody);
    }

    private StreamingResponseBody zipFiles(MultipartFile[] files, HttpServletResponse response) {
        return out -> {
            final ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
            ZipEntry zipEntry = null;
            InputStream inputStream = null;
            try {
                for (MultipartFile file : files) {
                    zipEntry = new ZipEntry(Objects.requireNonNull(file.getOriginalFilename()));
                    inputStream = file.getInputStream();
                    zipOutputStream.putNextEntry(zipEntry);
                    byte[] bytes = new byte[BUFFER_SIZE];
                    int length;
                    while ((length = inputStream.read(bytes)) >= 0) {
                        zipOutputStream.write(bytes, 0, length);
                    }
                }
                // set zip size in response
                response.setContentLength((int) (zipEntry != null ? zipEntry.getSize() : 0));
            } catch (IOException e) {
                log.error("Exception while reading and streaming data {} ", e.toString());
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                zipOutputStream.close();
            }
        };
    }

}
