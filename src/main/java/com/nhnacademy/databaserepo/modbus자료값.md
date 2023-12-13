# 모드버스 표

수석님꺼  
site, 브랜치, 장소, 센서 ID 등등이 있음

브랜치와 사이트를 추가해야 하는가?


## 온도 정보(temperature)

| 레지스터 주소 | devEUI | 센서 이름(사람용) | 타입 | 값 | TransactionID | UnitID |
| --- | --- | --- | --- | --- | --- | --- |
| 101 | 24e124128c067999 | 강의실A(AM107-067999) | temperature | 20.0 | null | null |
| 103 | 24e124785c389818 | 강의실A(EM320-TH-389818) | temperature | 20.0 | null | null |
| 105  | 24e124785c421885 | 강의실A(EM320-TH-421885) | temperature | 20.0 | null | null |
| 107 | 24e124126d152969 | 강의실A(EM500-CO2-152969) | temperature | 20.0 | null | null |
| 109 | 24e124128c140101 | 강의실B(AM107-140101) | temperature | 20.0 | null | null |
| 111 | 24e124785c389010 | 로비(EM320-389010) | temperature | 20.0 | null | null |
| 113 | 24e124136d151368 | 서버실(EM300-TH-151368) | temperature | 20.0 | null | null |
| 115 | 24e124136d151547 | 창고(EM300-151547) | temperature | 20.0 | null | null |
| 117 | 24e124126d152919 | 사무실(EM500-CO2-152919) | temperature | 20.0 | null | null |
| 119 | 24e124136d151485 | 페어룸(EM300-151485) | temperature | 20.0 | null | null |
| 121 | 24e124126c457594 | 냉장고(EM500-PT100-457594) | temperature | 3.0 | null | null |

## 습도 정보(humidity)

| 레지스터 주소 | devEUI | 센서 이름(사람용) | 타입 | 값 | TransactionId | UnitID |
| --- | --- | --- | --- | --- | --- | --- |
| 201 | 24e124128c067999 | 강의실A(AM107-067999) | humidity | 0.0 | null | null |
| 203 | 24e124785c389818 | 강의실A(EM320-TH-389818) | humidity | 0.0 | null | null |
| 205 | 24e124785c421885 | 강의실A(EM320-TH-421885) | humidity | 0.0 | null | null |
| 207 | 24e124126d152969 | 강의실A(EM500-CO2-152969) | humidity | 0.0 | null | null |
| 209 | 24e124128c140101 | 강의실B(AM107-140101) | humidity | 0.0 | null | null |
| 211 | 24e124785c389010 | 로비(EM320-389010) | humidity | 0.0 | null | null |
| 213 | 24e124136d151368 | 서버실(EM300-TH-151368) | humidity | 0.0 | null | null |
| 215 | 24e124136d151547 | 창고(EM300-151547) | humidity | 0.0 | null | null |
| 217 | 24e124126d152919 | 사무실(EM500-CO2-152919) | humidity | 0.0 | null | null |
| 219 | 24e124136d151485 | 페어룸(EM300-151485) | humidity | 0.0 | null | null |

## CO2 정보(co2, concentration)

| 레지스터 주소 | devEUI | 센서이름(사람용) | 타입 | 값 | TransactionId | UnitID |
| ---| --- | --- | --- | --- | --- | --- |
| 301 | 24e124128c067999 | 강의실A(AM107-067999) | co2 | 400.0 | null | null |
| 303 | 24e124126d152969 | 강의실A(EM500-CO2-152969) | concentration | 400 | null | null |
| 305 | 24e124743c210238 | 강의실B(AM107-140101) | co2 | 400.0 | null | null |
| 307 | 24e124126d152919 | 사무실(EM500-CO2-152919) | concentration | 400.0 | null | null |

## 대기질(tvoc)

| 레지스터 주소 | devEUI | 센서이름(사람용) | 타입 | 값 | TransactionId | UnitID |
| --- | --- | --- | --- | --- | --- | --- |
| 401 | 24e124128c067999 | 강의실A(AM107-067999) | tvoc | 0.0 | null | null |
| 403 | 24e124743c210238 | 강의실B(AM107-140101) | tvoc | 0.0 | null | null |

## 평균소음(leq)

| 레지스터 주소 | devEUI | 센서이름(사람용) | 타입 | 값 | TransactionId | UnitID |
| --- | --- | --- | --- | --- | --- | --- |
| 501 | 24e124743d012324 | 강의실A(WS302-012324) | leq | 0.0 | null | null |
| 503 | 24e124743c210238 | 강의실B(WS302-210238) | leq | 0.0 | null | null |

## 최대소음(lmax)

| 레지스터 주소 | devEUI | 센서이름(사람용) | 타입 | 값 | TransactionId | UnitID |
| ---| --- | --- | --- | --- | --- | --- |
| 601 | 24e124743d012324 | 강의실A(WS302-012324) | lmax | 0.0 | null | null |
| 603 | 24e124743c210238 | 강의실B(WS302-210238) | lmax | 0.0 | null | null |