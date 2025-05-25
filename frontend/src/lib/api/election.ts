import { apiGet, apiPost } from './common';
import {
	CandidateResponse,
	ElectionResponse,
	MyVotedCandidateResponse,
} from '../types/election';

// 후보자 리스트
export const getCandidateList = (electionId: number = 1) =>
	apiGet<CandidateResponse>(`/rest/election/v1/${electionId}/vote`);

// 투표하기
export const election = (electionId: number, candidateId: number) =>
	apiPost<ElectionResponse>(`/rest/election/v1/${electionId}/vote`, {
		candidateId,
	});

// 내가 투표한 후보자 정보
export const getMyVotedCandidate = (electionId: number = 1) =>
	apiGet<MyVotedCandidateResponse>(`/rest/election/v1/${electionId}/vote/mine`);
