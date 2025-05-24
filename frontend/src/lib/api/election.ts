import { apiGet, apiPost } from './common';
import { CandidateResponse, ElectionResponse } from '../types/election';

export const getCandidateList = (electionId: number = 1) =>
	apiGet<CandidateResponse>(`/election/v1/${electionId}/vote`);

export const election = (electionId: number, candidateId: number) =>
	apiPost<ElectionResponse>(`/election/v1/${electionId}/vote`, {
		candidateId,
	});
