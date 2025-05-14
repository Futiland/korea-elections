import { apiGet } from './common';
import { ElectionItem } from '../types/election';
import { ListResponseInfinity } from '../types/common';

export const getElectionList = (size: number = 10) =>
	apiGet<ListResponseInfinity<ElectionItem>>('/election/v1', { size });
