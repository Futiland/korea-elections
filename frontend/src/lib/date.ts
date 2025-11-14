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
export function formatDateTimeLocal(date: Date | string): string {
	if (typeof date === 'string') date = parseISO(date);
	return format(date, "yyyy-MM-dd'T'HH:mm");
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
