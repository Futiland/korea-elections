// src/lib/date.ts

import {
	format,
	parseISO,
	isBefore,
	isAfter,
	isSameDay,
	differenceInDays,
	isWithinInterval,
	isValid,
} from 'date-fns';
import { ko } from 'date-fns/locale';

/**
 * 문자열 또는 Date → Date 객체로 변환
 * 타임존 정보가 없는 경우 로컬 타임존으로 해석
 */
export function toDate(date: string | Date): Date {
	return typeof date === 'string' ? parseISO(date) : date;
}

/** yyyy-MM-dd 형식 포맷팅 */
export function formatDate(
	date: Date | string,
	formatStr: string = 'yyyy-MM-dd'
): string {
	if (typeof date === 'string') date = parseISO(date);
	return format(date, formatStr);
}

/** 날짜가 과거인지 */
export function isPast(date: Date | string): boolean {
	if (typeof date === 'string') date = parseISO(date);
	return isBefore(date, new Date());
}

/** 날짜가 미래인지 */
export function isFuture(date: string | Date): boolean {
	return isAfter(toDate(date), new Date());
}

/** 날짜 차이 계산 오늘로부터 며칠 남았는지 (D-Day 용)*/
export function daysUntil(date: Date | string): number {
	if (typeof date === 'string') date = parseISO(date);
	return differenceInDays(date, new Date());
}

/** D-Day 형식 문자열 반환 */
export function formatDday(date: string | Date): string {
	const days = daysUntil(date);
	if (days === 0) return 'D-Day';
	return days > 0 ? `D-${days}` : `종료됨`;
}

/** 오늘 날짜_한국어 */
export function formatDateKo(date: Date | string): string {
	return format(date, 'PPP', { locale: ko });
}

/** 날짜가 주어진 범위 안에 있는지 */
export function isInRange(
	date: string | Date,
	start: Date,
	end: Date
): boolean {
	return isWithinInterval(toDate(date), { start, end });
}

/** 유효한 날짜인지 */
export function isValidDate(date: string | Date): boolean {
	return isValid(toDate(date));
}

/** datetime-local input 형식으로 변환 (yyyy-MM-ddTHH:mm) */
export function formatDateTimeLocal(
	date: Date | string,
	formatStr: string = "yyyy-MM-dd'T'HH:mm"
): string {
	if (typeof date === 'string') {
		// 타임존 정보가 없는 경우 (예: "2025-11-18 00:52" 또는 "2025-11-18T00:52")
		// 서버가 UTC로 보내는 경우이므로 UTC로 해석하여 로컬 타임존으로 변환
		if (
			!date.includes('Z') &&
			!date.includes('+') &&
			!date.match(/-\d{2}:\d{2}$/)
		) {
			// 공백을 T로 변환하고 UTC로 파싱 (Z 추가)
			const normalized = date.replace(' ', 'T');
			date = parseISO(normalized + 'Z');
		} else {
			date = parseISO(date);
		}
	}
	return format(date, formatStr);
}

/** datetime-local input 값을 Date 객체로 변환 (로컬 타임존 유지) */
export function parseDateTimeLocal(value: string): Date {
	// datetime-local 형식: "yyyy-MM-ddTHH:mm"
	// 이 값을 로컬 타임존으로 해석하여 Date 객체 생성
	const [datePart, timePart] = value.split('T');
	const [year, month, day] = datePart.split('-').map(Number);
	const [hours, minutes] = timePart.split(':').map(Number);

	// 로컬 타임존으로 Date 객체 생성 (UTC 변환 없이)
	return new Date(year, month - 1, day, hours, minutes);
}

/** Date 객체를 로컬 타임존 ISO 문자열로 변환 (서버 전송용) */
export function toLocalISOString(date: Date): string {
	// 로컬 타임존의 년, 월, 일, 시, 분, 초를 가져옴
	const year = date.getFullYear();
	const month = String(date.getMonth() + 1).padStart(2, '0');
	const day = String(date.getDate()).padStart(2, '0');
	const hours = String(date.getHours()).padStart(2, '0');
	const minutes = String(date.getMinutes()).padStart(2, '0');
	const seconds = String(date.getSeconds()).padStart(2, '0');

	// 로컬 타임존 오프셋 계산 (예: +09:00)
	const offset = -date.getTimezoneOffset();
	const offsetHours = String(Math.floor(Math.abs(offset) / 60)).padStart(
		2,
		'0'
	);
	const offsetMinutes = String(Math.abs(offset) % 60).padStart(2, '0');
	const offsetSign = offset >= 0 ? '+' : '-';

	return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}${offsetSign}${offsetHours}:${offsetMinutes}`;
}

/** 남은 시간을 표현하는 라벨 생성 (최대 7일, 24시간 미만이면 시간 단위) */
export function getRemainingTimeLabel(endAt: Date | string): string | null {
	const endDate = toDate(endAt);
	const now = new Date();
	const diffMs = endDate.getTime() - now.getTime();

	if (diffMs <= 0) {
		return null;
	}

	const diffMinutes = Math.floor(diffMs / (1000 * 60));

	if (diffMinutes < 1) {
		return '남은 시간 1분';
	}

	if (diffMinutes < 60) {
		return `남은 시간 ${diffMinutes}분`;
	}

	const diffHours = Math.floor(diffMinutes / 60);

	if (diffHours < 24) {
		return `남은 시간 ${diffHours}시간`;
	}

	const diffDays = Math.floor(diffHours / 24);

	if (diffDays > 7) {
		return null;
	}

	return `남은 시간 ${diffDays}일`;
}
