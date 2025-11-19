import { apiGet, apiPost } from './common';
import {
	CreatePollData,
	CreatePollResponse,
	PublicPollResponse,
} from '../types/poll';

export const createPoll = (data: CreatePollData) =>
	apiPost<CreatePollResponse>('/rest/poll/v1/public', data);

export const getPublicPolls = (size: number, nextCursor?: string) =>
	apiGet<PublicPollResponse>(
		'/rest/poll/v1/public',
		// nextCursor가 있으면 nextCursor를 포함한 객체를 반환, 없으면 size만 포함한 객체를 반환
		nextCursor ? { size, nextCursor } : { size }
	);
