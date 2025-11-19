import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
	return twMerge(clsx(inputs));
}

// 세 자리수 콤마 추가
export function addCommas(value: number): string {
	return value.toLocaleString('ko-KR');
}
