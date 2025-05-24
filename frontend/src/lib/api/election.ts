import { apiGet, apiPost } from './common';
import { CandidateResponse, ElectionResponse } from '../types/election';

export const getCandidateList = (electionId: number = 1) =>
	apiGet<CandidateResponse>(`/rest/election/v1/${electionId}/vote`);

export const election = (electionId: number, candidateId: number) =>
	apiPost<ElectionResponse>(`/rest/election/v1/${electionId}/vote`, {
		candidateId,
	});
