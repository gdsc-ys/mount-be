package gdsc.be.mount.storage.controller;

import gdsc.be.mount.global.common.response.SuccessResponse;
import gdsc.be.mount.storage.dto.response.FileDownloadResponse;
import gdsc.be.mount.storage.dto.response.FileUploadResponse;
import gdsc.be.mount.storage.service.FileService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@Slf4j
@Validated
public class FileController {

    private final FileService fileService;

    /**
     * 파일 업로드
     * @param userName
     * @param file
     * @return
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<FileUploadResponse>> uploadFile(@RequestParam("user") @NotBlank String userName,
                                                                         @RequestParam("file") MultipartFile file) {
        FileUploadResponse data = fileService.uploadFile(file, userName);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(SuccessResponse.of(data));
    }

    /**
     * 파일 삭제 기능
     * @param fileId
     * @param userName
     * @return
     */
    @DeleteMapping(value = "/{fileId}/{userName}")
    public ResponseEntity<SuccessResponse<Long>> deleteFile(@PathVariable Long fileId, @PathVariable String userName) {

        Long deletedFileId = fileService.deleteFile(fileId, userName);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(SuccessResponse.of(deletedFileId));
    }

    /**
     * 파일 다운로드 기능
     * @param fileId
     * @param userName
     * @return
     */
    @GetMapping(value = "/download/{fileId}/{userName}")
    public ResponseEntity<UrlResource> downloadFile(@PathVariable Long fileId, @PathVariable String userName) {

        FileDownloadResponse content = fileService.downloadFile(fileId, userName);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, content.contentDisposition())
                .body(content.urlResource());
    }

}
