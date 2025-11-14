import { apiGet, apiPost } from './common';
import { CreatePollData, CreatePollResponse } from '../types/poll';

export const createPoll = (data: CreatePollData) =>
	apiPost<CreatePollResponse>('/rest/poll/v1/public', data);
