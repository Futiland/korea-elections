import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
	return twMerge(clsx(inputs));
}

// 세 자리수 콤마 추가
export function addCommas(value: number): string {
	return value.toLocaleString('ko-KR');
}

// 투표 참여 메세지
export function getParticipationMessage(
	status: string | undefined,
	responseCount: number | undefined
): string {
	const isExpired = status === 'EXPIRED';
	const hasParticipants = !!responseCount && responseCount > 0;

	if (isExpired) {
		return hasParticipants
			? `총 ${addCommas(responseCount)}명이 참여했습니다.`
			: '아쉽게도 참여자가 없었습니다. 😢';
	}

	return hasParticipants
		? `지금까지 ${addCommas(responseCount)}명이 참여했어요!`
		: '첫 번째 참여자가 되어 주세요!';
}
