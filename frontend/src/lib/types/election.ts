export type ElectionItem = {
	id: string;
	title: string;
	description: string;
	status: 'ACTIVE' | 'INACTIVE' | 'COMPLETED';
	startDate: string;
	endDate: string;
	createdAt: string;
};

export type CandidateDate = {
	electionId: number;
	name: string;
	number: number;
	description: string;
	status: 'ACTIVE';
	createdAt: Date;
	id: number;
	deletedAt: Date | null;
	party: string;
};

export type CandidateResponse = {
	code: string;
	message: string;
	data: CandidateDate[];
};

export type ElectionResponse = {
	code: string;
	message: string;
	data: ElectionItem[];
};
