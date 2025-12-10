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

export const getPublicPolls = (size: number, nextCursor?: string) =>
	apiGet<PublicPollResponse>(
		'/rest/poll/v1/public',
		// nextCursor가 있으면 nextCursor를 포함한 객체를 반환, 없으면 size만 포함한 객체를 반환
		nextCursor ? { size, nextCursor } : { size }
	);

export const submitPublicPoll = (
	pollId: number,
	optionIds: number[] | number,
	responseType: QuestionType
) => {
	const payload: {
		responseType: QuestionType;
		scoreValue?: number;
		optionIds?: number[] | number;
	} = { responseType };
	// responseType에 따른 payload 설정
	if (responseType === 'SCORE') {
		payload.scoreValue = optionIds as number;
	} else {
		payload.optionIds = optionIds;
	}
	return apiPost<PublicPollSubmitResponse>(
		`/rest/poll/v1/${pollId}/response`,
		payload
	);
};

export const getPublicPollResult = (pollId: number) =>
	apiGet<PublicPollResultResponse>(`/rest/poll/v1/${pollId}/result`);

export const getPoll = (pollId: number) =>
	apiGet<PollResponse>(`/rest/poll/v1/${pollId}`);

// 내가 만든 투표 목록
export const getMyPolls = (params: PageParam) =>
	apiGet<MyPollResponse>('/rest/poll/v1/my', params);

// 내가 참여한 모두의 투표
export const getMyParticipatedPublicPolls = (params: PageParam) =>
	apiGet<MyPollResponse>('/rest/poll/v1/public/response/my', params);

// 내가 참여한 여론 조사
export const getMyParticipatedOpinionPolls = (params: PageParam) =>
	apiGet<MyPollResponse>('/rest/poll/v1/system/response/my', params);
