# MUSINSA WATCHER!


### :house:[HomePage](https://www.musinsa.info)

## :pencil:Writing
#### [캐시 서버에 장애가 생긴다면?](https://jgrammer.tistory.com/entry/%EB%AC%B4%EC%8B%A0%EC%82%AC-watcher-%EC%BA%90%EC%8B%9C-%EC%84%9C%EB%B2%84%EC%97%90-%EC%9E%A5%EC%95%A0%EA%B0%80-%EC%83%9D%EA%B8%B4%EB%8B%A4%EB%A9%B4)
#### [페이징 성능 개선기](https://jgrammer.tistory.com/entry/%EB%AC%B4%EC%8B%A0%EC%82%AC-%EC%8A%A4%ED%86%A0%EC%96%B4-watcher-%ED%8E%98%EC%9D%B4%EC%A7%95-%EC%BF%BC%EB%A6%AC-%EC%84%B1%EB%8A%A5-%EA%B0%9C%EC%84%A0%EA%B8%B0?category=948604)
#### [3번의 CI/CD 도입기](https://jgrammer.tistory.com/entry/%EB%AC%B4%EC%8B%A0%EC%82%AC-%EC%8A%A4%ED%86%A0%EC%96%B4-watcher-CICD-%EB%8F%84%EC%9E%85%EA%B8%B0?category=948604)


## 소개
무신사 스토어 가격 변동 파악 웹사이트입니다.  
매일 크롤링을 통해 인기 랭킹 아이템을 수집하고 가격 비교를 제공합니다.

## 프로젝트 구조
![image](https://user-images.githubusercontent.com/52908154/115143171-af2aae00-a080-11eb-8c0c-90a3e80ef10f.png)

## Crawling
![image](https://user-images.githubusercontent.com/52908154/115143050-fbc1b980-a07f-11eb-9e3f-2262a5615bc0.png)

github action의 cron을 사용하여 크롤링 자동화를 구성하였습니다. 데이터가 업데이트 되는 순간이 캐시의 유효성이 만료되는 시기이므로 크롤링 과정에서 cache는 초기화되고 db에 데이터가 저장됩니다. 또한, 페이징 성능 저하를 방지하기위해 통계 데이터를 미리 집계하여 실행하고 저장하도록 하였습니다. 작업의 결과는 slack으로 전송해 매일 정상적으로 데이터가 수집되는지 확인할 수 있습니다.

## **DevOps**
급증하는 트래픽에도 대응할 수 있도록 컨테이너 오케스트레이션 툴로 NCP kubernetes service를 사용했습니다.
![image](https://user-images.githubusercontent.com/52908154/103759808-9cebad80-5057-11eb-8e24-d907bbb20e4e.png)

<img src="https://user-images.githubusercontent.com/52908154/103761935-d245ca80-505a-11eb-9e68-2657ab38973f.png" width=50%>

## Test
Backend **테스트 커버리지 80%이상**을 목표로 잡고 있습니다. jacoco를 도입하여 평균 라인 커버리지 80%, 브랜치 커버리지 80%을 넘지 못하면 빌드가 실패하도록 하여 테스트 코드를 신경쓰도록 개발했습니다.

**현재 coverage**  
line coverage : 85%   
branch coverage : 84%

## Tech Stack
<img src="https://user-images.githubusercontent.com/52908154/104377761-4c4de600-556a-11eb-98ea-2ab133e5bacc.png" width=70%>


## Service
![ezgif com-gif-maker (4)](https://user-images.githubusercontent.com/52908154/105810112-b7c59800-5fed-11eb-87c6-d863bd77ac67.gif)

