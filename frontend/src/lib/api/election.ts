import { apiGet, apiPost } from './common';
import {
	CandidateResponse,
	ElectionResponse,
	MyVotedCandidateResponse,
	ElectionResultResponse,
	ElectionResultAgesResponse,
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

// 투표 결과
export const getElectionResult = (electionId: number = 1) =>
	apiGet<ElectionResultResponse>(`/rest/election/v1/${electionId}/vote/result`);

// 투표 결과_연령별
export const getElectionResultAges = (electionId: number = 1) =>
	apiGet<ElectionResultAgesResponse>(
		`/rest/election/v1/${electionId}/vote/result?resultType=AGE`
	);
