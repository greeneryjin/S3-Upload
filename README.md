# S3-Upload
aws-s3와 spring을 연동해서 사진 업로드(저장, 조회, 수정, 삭제) 구현

front-end
- react-native


사용 언어
- JAVA 8


사용 기술
- spring-boot
- spring
- Mysql
- S3
- Querydsl
- spring data jpa
- spring jpa


라이브러리
- lombok
- gradle


핵심 코드 
-> S3에 업로드될 때, UUID를 사용해서 랜덤 값으로 저장해야 사진 이름에서 중복이 발생할 확률이 적어집니다.  
```java
    private String createStoreFileName(String originalFilename) {
        // 서버에 저장하는 파일 명
        String uuid = UUID.randomUUID().toString();
        // 사진 포맷 추출
        String ext = extractExt(originalFilename);
        String storeFileName = uuid + "." + ext;
        return storeFileName;
    }
```

핵심 코드
-> 클라이언트가 올린 파일을 변환해서 올려야 파일이 깨지는 것을 막을 수 있습니다. 
```java
    private Optional<File> convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        if(convertedFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertedFile);
        }

        return Optional.empty();
    }
```

핵심 코드
-> 사진을 저장하고 다시 수정할 때는 s3에 있던 사진을 지우고 새로 저장해야 s3 요금을 조금이라도 절약할 수 있습니다. 
```java
    @Transactional
    public MyGardenDtl update(Long id, PlantChangeDto plantChangeDto, MultipartFile file) throws IOException {
        ProfileDto pic = awsService.uploadOneProfile(file, "/picture");
        MyGardenDtl myGardenDtl = findId(id);

        //기존 컨텐츠 삭제(S3에서 삭제됨)
        awsService.deleteProfile("/picture", myGardenDtl.getPic());
        myGardenDtl.changeData(plantChangeDto);
        myGardenDtl.pic(pic.getFileName());
        return myGardenDtl;
    }   
 ```
 
 Swaager
 
<img width="827" alt="사진ㅇㅇ" src="https://user-images.githubusercontent.com/87289562/217515749-45e70bcb-1c15-483e-a9bd-4656d8242b64.PNG">


완성된 페이지
<center class="half">
<img width="258" alt="1" src="https://user-images.githubusercontent.com/87289562/217515905-a8373a22-901c-4d2b-a24f-bc8df2384374.PNG">
<img width="258" alt="22" src="https://user-images.githubusercontent.com/87289562/217516360-91c27ad7-46b0-4b3c-aef6-2490dfc2cfe8.PNG">
<img width="258" alt="333" src="https://user-images.githubusercontent.com/87289562/217516399-bb7b90a2-d98e-4b65-9e23-c27af3dffbd3.PNG">
 center>
 
