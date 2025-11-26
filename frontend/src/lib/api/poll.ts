import { apiGet, apiPost } from './common';
import {
	CreatePollData,
	CreatePollResponse,
	PublicPollResponse,
	PublicPollSubmitResponse,
	QuestionType,
} from '../types/poll';

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
