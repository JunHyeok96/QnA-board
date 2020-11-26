package com.board.web.controller;

import com.board.service.S3Service;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Profile("!test")
@Slf4j
@RequiredArgsConstructor
@RestController
public class S3Controller {

  private final S3Service s3Uploader;

  @Value(value = "${aws.s3.url}")
  private String s3Url;

  HttpClient client = HttpClientBuilder.create().build();

  @PostMapping("/upload")
  public String upload(@RequestParam("data") MultipartFile multipartFile) throws IOException {
    String imgName = s3Uploader.upload(multipartFile, "static");
    return "/download?src=".concat(imgName);
  }

  @GetMapping("/download")
  public void download(@RequestParam String src, HttpServletResponse response) throws IOException {
    HttpGet request = new HttpGet(s3Url + src);
    try {
      HttpResponse s3Response = client.execute(request);
      try (InputStream in = s3Response.getEntity().getContent();
          OutputStream out = response.getOutputStream();) {
        int readCount = 0;
        byte[] buffer = new byte[1024];
        while ((readCount = in.read(buffer)) != -1) {
          out.write(buffer, 0, readCount);
        }
      } catch (IOException e) {
        log.error(e.toString());
      }
    } catch (IOException e) {
      log.error(e.toString());
      throw new IOException(e);
    }
  }
}
