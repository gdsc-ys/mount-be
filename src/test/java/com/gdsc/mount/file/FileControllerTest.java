package com.gdsc.mount.file;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdsc.mount.file.dto.FileDeleteRequest;
import com.gdsc.mount.file.dto.FileDownloadRequest;
import com.gdsc.mount.member.domain.Member;
import com.gdsc.mount.member.repository.MemberRepository;
import com.gdsc.mount.metadata.repository.MetadataRepository;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureDataMongo
@AutoConfigureMockMvc
public class FileControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MetadataRepository metadataRepository;

    @Qualifier("webApplicationContext")
    @Autowired
    ResourceLoader loader;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void init() {
        String username = "becooq81";
        memberRepository.save(new Member(username));
    }

    // 파일 업로드
    @Test
    @DisplayName("파일 업로드")
    public void upload_file() throws Exception {
        MockMultipartFile multipartFile1 = new MockMultipartFile("file", "file", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/file/upload")
                        .file(multipartFile1)
                        .param("username", "becooq81")
                        .param("path", "/hi/")
        ).andExpect(status().is2xxSuccessful());
    }

    // 파일 다운로드
    @Test
    @DisplayName("파일 다운로드")
    public void download_file() throws Exception {
        MockMultipartFile multipartFile1 = new MockMultipartFile("file", "file3", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/file/upload")
                        .file(multipartFile1)
                        .param("username", "becooq81")
                        .param("path", "/hello/")
        ).andExpect(status().is2xxSuccessful());

        FileDownloadRequest fileDownloadRequest = downloadFileRequest();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/file/download")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(fileDownloadRequest));

        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().isOk());
    }

    // delete file test
    @Test
    @DisplayName("파일 삭제")
    public void delete_file() throws Exception {
        MockMultipartFile multipartFile1 = new MockMultipartFile("file", "file2", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/file/upload")
                        .file(multipartFile1)
                        .param("username", "becooq81")
                        .param("path", "/hello/")
        ).andExpect(status().is2xxSuccessful());

        FileDeleteRequest fileDeleteRequest = deleteFileRequest();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/file/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(fileDeleteRequest));

        Assertions.assertFalse(metadataRepository.existsByPathWithFile(fileDeleteRequest.getPath()));
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        resultActions.andExpect(status().is2xxSuccessful());
    }

    private FileDeleteRequest deleteFileRequest() {
        return new FileDeleteRequest(
                "becooq81",
                "/hello/",
                "file2"
        );
    }

    private FileDownloadRequest downloadFileRequest() {
        return new FileDownloadRequest(
                "becooq81",
                "/hello/",
                "file3"
        );
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
