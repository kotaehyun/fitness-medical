/**
 * [공부/면접] 타입 정의 placeholder (types/index.js)
 *
 * Q. JS 프로젝트인데 types/index.js가 있는 이유는?
 * A. jsconfig.json의 paths alias(@/types 등)와 IDE JSDoc typedef 확장을 위한 자리다.
 *    현재는 TS가 아니므로 `export {}`로 모듈 스코프만 확보한다.
 *
 * Q. export {} 만 있는 파일의 효과는?
 * A. ES module로 취급되어 import/export 문법을 쓸 수 있고,
 *    추후 JSDoc @typedef 또는 .d.ts를 이 경로에 추가하기 쉽다.
 *
 * Q. Member, HealthRecord 등 도메인 타입은 어디에?
 * A. mock 데이터(members.js)와 apiService map* 함수가 de facto 스키마 역할.
 *    TypeScript 전환 시 이 파일을 .ts로 바꾸고 interface를 정의하면 된다.
 */
export {};
