# AIwiz

![newmagician](https://github.com/user-attachments/assets/1fef5507-98e6-4ab4-884a-c06ca83ddfca){: width="100" height="100"}

AIwiz는 AI 기술을 활용하여 사진 선택만으로 사용자가 원하는 AI이미지를 생성시켜주는 서비스입니다. 

## 목차

- [특징](#특징)
- [기술 스택](#기술-스택)
    - [사용한 라이브러리](#사용한-라이브러리)
- [API 정보](#api-정보)
- [스크린샷](#스크린샷)


## 특징

- **도움말 다이얼로그**: 도움말 다이얼로그를 통해 상세한 사용 지침을 제공합니다.
- **상세 페이지 제공**: 이미지를 누르면 상세 설명과 함께 다운로드, 좋아요 기능을 이용할 수 있습니다.
- **사진 갤러리**: 3x3 그리드 레이아웃으로 좋아요한 사진을 쉽게 탐색하고 선택할 수 있습니다.
- **반응형 UI**: 현대적이고 사용자 친화적인 인터페이스를 제공하는 Material Design 원칙에 기반한 UI 설계.
- **AI 이미지 생성**: 사진 선택만으로 사용자만의 ai이미지를 생성할 수 있습니다.
- **에러 처리**: 네트워크 문제나 API 오류 시 유용한 메시지를 통해 사용자에게 정보를 제공합니다.

## 기술 스택

AIwiz는 다양한 최신 기술과 라이브러리를 활용하여 개발되었습니다.

### 사용한 라이브러리

- **UI 컴포넌트**
    - [AppCompat](https://developer.android.com/jetpack/androidx/releases/appcompat): 안드로이드 UI 컴포넌트의 이전 버전과 호환성을 제공합니다.
    - [Material Components](https://material.io/develop/android): Material Design 컴포넌트(예: TabLayout, 버튼 등)를 구현합니다.
    - [Activity KTX](https://developer.android.com/jetpack/androidx/releases/activity): 액티비티를 더 쉽게 개발할 수 있도록 Kotlin 확장 기능을 제공합니다.
    - [ConstraintLayout](https://developer.android.com/training/constraint-layout): 유연하고 효율적인 UI 레이아웃을 가능하게 합니다.
    - [RecyclerView](https://developer.android.com/jetpack/androidx/releases/recyclerview): 스크롤 가능한 리스트를 표시하는 데 사용됩니다.
    - [CardView](https://developer.android.com/jetpack/androidx/releases/cardview): 둥근 모서리와 그림자가 있는 카드 레이아웃 컨테이너를 제공합니다.
    - [ViewPager2](https://developer.android.com/jetpack/androidx/releases/viewpager2): 서로 다른 프래그먼트나 뷰 간을 스와이프할 수 있게 합니다.
    - [Core Splashscreen](https://developer.android.com/guide/topics/ui/splash-screen): 앱 실행 시 스플래시 화면을 표시하는 SplashScreen API를 구현합니다.

- **네트워킹**
    - [Retrofit](https://square.github.io/retrofit/): 네트워크 요청을 수행하기 위한 타입-세이프한 HTTP 클라이언트입니다.
    - [Gson Converter](https://github.com/square/retrofit/tree/master/retrofit-converters/gson): JSON 데이터를 Java/Kotlin 객체로 변환하고 그 반대로 변환합니다.
    - [OkHttp Logging Interceptor](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor): 디버깅 목적으로 HTTP 요청과 응답 데이터를 로깅합니다.

- **이미지 로딩**
    - [Glide](https://github.com/bumptech/glide): 효율적인 이미지 로딩과 캐싱을 위한 라이브러리입니다.
    - [Glide Compiler](https://github.com/bumptech/glide/tree/master/generator): 커스텀 모델 로더를 위한 Glide의 API 생성을 지원합니다.

- **데이터베이스**
    - [Room](https://developer.android.com/jetpack/androidx/releases/room): SQLite 위에 추상화 계층을 제공하여 데이터베이스 관리를 용이하게 합니다.

- **테스팅**
    - [JUnit](https://junit.org/junit5/): 단위 테스트를 작성하고 실행하기 위한 프레임워크입니다.
    - [AndroidX JUnit](https://developer.android.com/jetpack/androidx/releases/test): Android-specific 테스트를 위해 JUnit을 확장한 라이브러리입니다.
    - [Espresso Core](https://developer.android.com/training/testing/espresso): 사용자 상호작용을 시뮬레이션하여 UI 테스트를 작성하는 데 사용됩니다.

## API 정보

AIwiz는 다음과 같은 외부 API를 활용하여 기능을 구현합니다.

- **HuggingFace API**
    - **목적**: AI를 활용한 이미지 생성을 지원합니다.
    - **엔드포인트**: `https://api.huggingface.co/generate-image`
    - **요청 방식**: POST
    - **요청 데이터**:
      ```json
      {
        "prompt": "생성할 이미지에 대한 설명",
        "n": 1,
        "size": "512x512"
      }
      ```
    - **응답 데이터**:
      ```json
      {
        "generatedImages": ["https://images.generated.com/image123.png"]
      }
      ```
    - **인증 방식**: Bearer 토큰을 헤더에 포함하여 인증합니다.

- **Unsplash API**
    - **목적**: 사진 검색 기능을 제공합니다.
    - **엔드포인트**: `https://api.unsplash.com/search/photos`
    - **요청 방식**: GET
    - **요청 파라미터**:
        - `query`: 검색어
        - `page`: 페이지 번호
        - `per_page`: 페이지당 결과 수
    - **응답 데이터**:
      ```json
      {
        "results": [
          {
            "id": "photo1",
            "imageUrl": "https://images.unsplash.com/photo1",
            "description": "사진 설명"
          }
          
        ]
      }
      ```
    - **인증 방식**: Access Key를 헤더에 포함하여 인증합니다.

    HelpPagerAdapter --> HelpTabFragment : contains

### 스크린샷
-초기 화면
![Screenshot_20241218_023807](https://github.com/user-attachments/assets/d471311e-1ab9-43c4-bccf-40de13a04958){: width="100" height="100"}

-사진 검색 화면
![Screenshot_20241218_024304](https://github.com/user-attachments/assets/549a4587-4763-4932-b12d-af4f6895859c){: width="100" height="100"}

-생성된 AI 이미지
![Screenshot_20241218_024413](https://github.com/user-attachments/assets/a4aeafb3-43a2-4be9-8c76-15b8a62661b7){: width="100" height="100"}

