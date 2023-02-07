package com.example.demo.plantDic.s3.file;

import com.example.demo.plantDic.s3.dto.ProfileDto;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
public class FileStore {

    public ProfileDto storeOneFile(MultipartFile multipartFile, String folderName) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        String url = "ㅎㅎㅎㅎㅎㅎㅎㅎㅎ" + folderName + "/" + storeFileName;
        String fileName = storeFileName;
        ProfileDto profileDto =  new ProfileDto(originalFilename, storeFileName, url, fileName);
        return profileDto;
    }

    private String createStoreFileName(String originalFilename) {
        // 서버에 저장하는 파일 명
        String uuid = UUID.randomUUID().toString();
        // 사진 포맷 추출
        String ext = extractExt(originalFilename);
        String storeFileName = uuid + "." + ext;
        return storeFileName;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
