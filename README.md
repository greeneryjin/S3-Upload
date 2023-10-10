# S3-Upload
aws-s3와 spring을 연동해서 사진 업로드(저장, 조회, 수정, 삭제) 구현


사용 언어
```
- JAVA 8
```

사용 기술
```
- spring-boot
- spring
- Mysql
- S3
- Querydsl
- spring data jpa
- spring jpa
```

라이브러리
```
- lombok
- gradle
```

핵심 코드     
1. S3에 업로드될 때, UUID를 사용해서 랜덤 값으로 저장해야 사진 이름이 중복되는 것을 막을 수 있습니다.  
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


2. 클라이언트가 올린 파일을 변환해서 올려야 파일이 손상되는 것을 막을 수 있습니다. 
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


3. 사진을 수정할 때는 s3에 있던 기존 사진을 삭제한 후, 다시 업로드하는 방식입니다. 
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
 ![슬라이드1 PNG](https://user-images.githubusercontent.com/87289562/217513134-2030abd0-f06b-4c2d-9410-c5503a2ce3b5.png)
