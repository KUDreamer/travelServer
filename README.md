# 여행 경로 자동 수립 프로그램

- 본 프로젝트는 건국대학교 산학협력 프로젝트 수업의 결과물입니다.
- 본 프로그램의 목표는 사용자가 입력한 조건에 맞추어 여행 경로를 작성해주는 것이 목적입니다.
## 일정
- 2024년 3월 14일 - 2024년 6월 18일

## 구성원
- 프론트 엔드 4명
- 백엔드 1명 (본인)

## 순서도
![afsf](https://github.com/user-attachments/assets/076d618f-d361-4141-9e90-879c14970e26)

## 피그마
<img width="908" alt="스크린샷 2024-05-01 오후 4 53 43" src="https://github.com/user-attachments/assets/358c9319-ae80-46f7-a60b-d4426adf1537">
https://www.figma.com/file/aa6G3P58rvZYeNVtebawUP/산협프?type=design&t=OTGxexTaVwr9a2lJ-6

## 백엔드 API 구조
위 코드는 Spring Boot를 사용하여 Google Maps API와 연동하는 컨트롤러입니다. 각 함수의 기능을 정의하면 다음과 같습니다:

### 1. `getInfo`
- **Endpoint**: `/api/giveInfo`
- **HTTP Method**: GET
- **Description**: Google Places API를 사용하여 주어진 PLACE_ID에 대한 장소 세부 정보를 가져옵니다.
- **Parameters**: 
  - `PLACE_ID` (Query Parameter)
- **Example Request**: `http://localhost:8080/api/giveInfo?PLACE_ID=ChIJN1t_tDeuEmsRUsoyG83frY4`
- **Returns**: 장소의 세부 정보를 JSON 형식으로 반환합니다.

### 2. `searchPlaceInfo`
- **Endpoint**: `/api/searchPlaceInfo`
- **HTTP Method**: POST
- **Description**: 주어진 검색어(query)를 사용하여 장소를 검색하고, 장소의 기본 정보를 반환합니다.
- **Parameters**: 
  - `query` (Request Parameter)
- **Example Request**: `http://localhost:8080/api/searchPlaceInfo?query=중국 음식`
- **Returns**: 검색된 장소의 정보를 JSON 형식으로 반환합니다.

### 3. `searchByTextInfo`
- **Endpoint**: `/api/searchByTextInfo`
- **HTTP Method**: POST
- **Description**: 주어진 검색어(query)와 장소 유형(place_type)을 사용하여 텍스트 검색을 수행하고 결과를 반환합니다.
- **Parameters**: 
  - `query` (Request Parameter)
  - `place_type` (Request Parameter)
- **Example Request**: `http://localhost:8080/api/searchByTextInfo?query=중국집&place_type=restaurant`
- **Returns**: 검색된 장소의 정보를 JSON 형식으로 반환합니다.

### 4. `getDirectionsTrain`
- **Endpoint**: `/api/directionsTrain`
- **HTTP Method**: POST
- **Description**: 주어진 출발지(origin)와 도착지(destination) 사이의 대중교통 경로를 검색합니다.
- **Parameters**: 
  - JSON Body:
    ```json
    {
      "origin": "Humberto Delgado Airport, Portugal",
      "destination": "Basílica of Estrela, Praça da Estrela, 1200-667 Lisboa, Portugal"
    }
    ```
- **Example Request**: `http://localhost:8080/api/directionsTrain`
- **Returns**: 경로 정보를 JSON 형식으로 반환합니다.

### 5. `getPlaceInfo`
- **Endpoint**: `/api/placePhoto`
- **HTTP Method**: GET
- **Description**: 주어진 photoReference와 maxWidth를 사용하여 장소 사진을 가져옵니다.
- **Parameters**: 
  - `photoReference` (Query Parameter)
  - `maxWidth` (Query Parameter)
- **Example Request**: `http://localhost:8080/api/placePhoto?photoReference=AUGGfZnSHjsfLCLJogZWwe5Jrfwae_VUhGDiYszJmPNNHzWQG58_HwAk122fH0FMhEcs1rz6Ny9cm_KgTbSW6_7g7B3t2KntyM7F53F-ElET4Tr3EzOPpYcfoMj9nGUkl06efNLvbigBzpryktPXF0ZLBYCCm5FcW0HSuLiOkWLcEiT2BXjI&maxWidth=400`
- **Returns**: 사진 URL을 JSON 형식으로 반환합니다.

### 6. `getDirections`
- **Endpoint**: `/api/directions`
- **HTTP Method**: POST
- **Description**: 주어진 출발지(origin), 도착지(destination), 경유지(intermediates)와 여행 모드(travelMode)를 사용하여 경로를 검색합니다.
- **Parameters**: 
  - JSON Body:
    ```json
    {
      "origin": { "address": "1600 Amphitheatre Parkway, Mountain View, CA" },
      "destination": { "address": "24 Willie Mays Plaza, San Francisco, CA 94107" },
      "intermediates": [{ "address": "450 Serra Mall, Stanford, CA 94305, USA" }],
      "travelMode": "DRIVE"
    }
    ```
- **Example Request**: `http://localhost:8080/api/directions`
- **Returns**: 경로 정보를 JSON 형식으로 반환합니다.

### 7. `getDirectionsDrive`
- **Endpoint**: `/api/wayOptimization`
- **HTTP Method**: POST
- **Description**: 주어진 출발지(origin), 도착지(destination), 경유지(intermediates)를 사용하여 최적화된 경로를 검색합니다.
- **Parameters**: 
  - JSON Body:
    ```json
    {
      "origin": "Adelaide,SA",
      "destination": "Adelaide,SA",
      "intermediates": [
        "Barossa+Valley,SA",
        "Clare,SA",
        "Connawarra,SA",
        "McLaren+Vale,SA"
      ]
    }
    ```
- **Example Request**: `http://localhost:8080/api/wayOptimization`
- **Returns**: 최적화된 경로 정보를 JSON 형식으로 반환합니다.

### 8. `getDirectionsTime`
- **Endpoint**: `/api/directionsTime`
- **HTTP Method**: POST
- **Description**: 주어진 출발지(origin), 도착지(destination), 경유지(intermediates)와 여행 모드(travelMode)를 사용하여 경로와 시간을 포함한 정보를 검색합니다.
- **Parameters**: 
  - JSON Body:
    ```json
    {
      "origin": { "address": "1600 Amphitheatre Parkway, Mountain View, CA" },
      "destination": { "address": "24 Willie Mays Plaza, San Francisco, CA 94107" },
      "intermediates": [
        { "address": "450 Serra Mall, Stanford, CA 94305, USA" },
        { "address": "1836 El Camino Real, Redwood City, CA 94063" }
      ],
      "travelMode": "DRIVE"
    }
    ```
- **Example Request**: `http://localhost:8080/api/directionsTime`
- **Returns**: 경로와 시간 정보를 포함한 경로 정보를 JSON 형식으로 반환합니다.

각 함수는 Google Maps API를 호출하여 다양한 유형의 장소 정보와 경로 정보를 제공하는 RESTful 웹 서비스의 일부분입니다. `RestTemplate`을 사용하여 외부 API를 호출하고, 결과를 클라이언트에 JSON 형식으로 반환합니다.
