import { apiGet, apiPost } from './common';
import {
	CandidateResponse,
	ElectionResponse,
	MyVotedCandidateResponse,
} from '../types/election';

export const getCandidateList = (electionId: number = 1) =>
	apiGet<CandidateResponse>(`/rest/election/v1/${electionId}/vote`);

export const election = (electionId: number, candidateId: number) =>
	apiPost<ElectionResponse>(`/rest/election/v1/${electionId}/vote`, {
		candidateId,
	});

export const getMyVotedCandidate = (electionId: number = 1) =>
	apiGet<MyVotedCandidateResponse>(`/rest/election/v1/${electionId}/vote/mine`);
