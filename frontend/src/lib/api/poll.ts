import { apiGet, apiPost } from './common';
import {
	CreatePollData,
	CreatePollResponse,
	PublicPollResponse,
	PublicPollResultResponse,
	PublicPollSubmitResponse,
	QuestionType,
	PollResponse,
	MyPollResponse,
} from '../types/poll';
import { PageParam } from '../types/common';

export const createPoll = (data: CreatePollData) =>
	apiPost<CreatePollResponse>('/rest/poll/v1/public', data);

type PollListType = 'public' | 'system';

export const getPolls = (
	type: PollListType,
	size: number,
	nextCursor?: string,
	keyword?: string,
	status?: string,
	sort?: string
) => {
	const endpoint =
		type === 'public' ? '/rest/poll/v1/public' : '/rest/poll/v1/system';

	const params: Record<string, string | number> = { size };

	if (nextCursor) {
		params.nextCursor = nextCursor;
	}

	if (keyword) params.keyword = keyword;
	if (status) params.status = status;
	if (sort) params.sort = sort;

	return apiGet<PublicPollResponse>(endpoint, params);
};

// 편의를 위한 별칭 함수들
// 모두의 투표 목록 - 커서 기반 페이지네이션
export const getPublicPolls = (
	size: number,
	nextCursor?: string,
	keyword?: string,
	status?: string,
	sort?: string
) => getPolls('public', size, nextCursor, keyword, status, sort);

// 여론 조사(시스템 폴) 목록 - 커서 기반 페이지네이션
export const getOpinionPolls = (
	size: number,
	nextCursor?: string,
	keyword?: string,
	status?: string,
	sort?: string
) => getPolls('system', size, nextCursor, keyword, status, sort);

export const getPoll = (pollId: number) =>
	apiGet<PollResponse>(`/rest/poll/v1/detail/${pollId}`);

export const submitPublicPoll = (
	pollId: number,
	optionIds: number[] | number,
	responseType: QuestionType
) => {
	const basePayload = { responseType };

	const payload = (() => {
		switch (responseType) {
			case 'SCORE':
				return {
					...basePayload,
					scoreValue: optionIds as number,
				};
			case 'SINGLE_CHOICE':
				return {
					...basePayload,
					optionId: optionIds as number,
				};
			case 'MULTIPLE_CHOICE':
				return {
					...basePayload,
					optionIds: optionIds as number[],
				};
		}
	})();

	return apiPost<PublicPollSubmitResponse>(
		`/rest/poll/v1/${pollId}/response`,
		payload
	);
};

export const getPublicPollResult = (pollId: number) =>
	apiGet<PublicPollResultResponse>(`/rest/poll/v1/${pollId}/result`);

// 내가 만든 투표 목록
export const getMyPolls = (params: PageParam) =>
	apiGet<MyPollResponse>('/rest/poll/v1/my', params);

// 내가 참여한 모두의 투표
export const getMyParticipatedPublicPolls = (params: PageParam) =>
	apiGet<MyPollResponse>('/rest/poll/v1/public/response/my', params);

// 내가 참여한 여론 조사
export const getMyParticipatedOpinionPolls = (params: PageParam) =>
	apiGet<MyPollResponse>('/rest/poll/v1/system/response/my', params);
