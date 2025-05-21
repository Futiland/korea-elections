import { apiGet } from './common';
import { CandidateResponse } from '../types/election';

export const getCandidateList = (electionId: number = 2) =>
	apiGet<CandidateResponse>(`/election/v1/${electionId}/vote`);
