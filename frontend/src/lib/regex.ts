// eslint-disable-next-line no-useless-escape

// 이메일
export const REG_EMAIL = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

// 휴대폰 번호 (010-xxxx-xxxx, - 없이도 가능)
export const REG_PHONE = /^01[016789]-?\d{3,4}-?\d{4}$/;

// 비밀번호 (최소 8자, 영문/숫자/특수문자 혼합)
export const REG_PASSWORD =
	/^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/;

// 한글(이름)
export const REG_KOREAN_NAME = /^[가-힣]{2,6}$/;

// 영문 이름
export const REG_ENGLISH_NAME = /^[A-Za-z]{2,20}$/;

// 숫자만
export const REG_NUMBER = /^\d+$/;
