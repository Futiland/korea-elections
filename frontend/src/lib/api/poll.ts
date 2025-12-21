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

interface GetPollsParams {
	size: number;
	nextCursor?: string;
	keyword?: string;
	status?: string;
	sort?: string;
}

const POLL_ENDPOINTS: Record<PollListType, string> = {
	public: '/rest/poll/v1/public',
	system: '/rest/poll/v1/system',
};

const getPolls = (type: PollListType, params: GetPollsParams) => {
	const endpoint = POLL_ENDPOINTS[type];
	const queryParams: Record<string, string | number> = { size: params.size };

	if (params.nextCursor) queryParams.nextCursor = params.nextCursor;
	if (params.keyword) queryParams.keyword = params.keyword;
	if (params.status) queryParams.status = params.status;
	if (params.sort) queryParams.sort = params.sort;

	return apiGet<PublicPollResponse>(endpoint, queryParams);
};

// 편의를 위한 별칭 함수들
// 모두의 투표 목록 - 커서 기반 페이지네이션
export const getPublicPolls = (params: GetPollsParams) =>
	getPolls('public', params);

// 여론 조사(시스템 폴) 목록 - 커서 기반 페이지네이션
export const getOpinionPolls = (params: GetPollsParams) =>
	getPolls('system', params);

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
