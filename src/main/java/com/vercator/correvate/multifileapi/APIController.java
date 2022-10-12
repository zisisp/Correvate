package com.vercator.correvate.multifileapi;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@Slf4j
public class APIController {

    int BUFFER_SIZE=4096;


    /**
     * API endpoint reads file  form-data with the param "file"
     * @param model
     * @param files
     * @param response
     * @return
     */
    @RequestMapping(path = "/zipFiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, method = RequestMethod.POST)
    public ResponseEntity<StreamingResponseBody> zipFiles(@ModelAttribute Model model, @RequestParam("file") MultipartFile[] files,
    HttpServletResponse response) {
        StreamingResponseBody streamResponseBody = zipFiles(files, response);
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=files.zip");
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "0");
        return ResponseEntity.ok(streamResponseBody);
    }

    private StreamingResponseBody zipFiles(MultipartFile[] files, HttpServletResponse response) {
        StreamingResponseBody streamResponseBody = out -> {
            final ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
            ZipEntry zipEntry = null;
            InputStream inputStream = null;
            try {
                for (MultipartFile file : files) {
                    zipEntry = new ZipEntry(file.getName());
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
                log.error("Exception while reading and streaming data {} ", e);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                zipOutputStream.close();
            }
        };
        return streamResponseBody;
    }

}
