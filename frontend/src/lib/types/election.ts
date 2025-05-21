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
};

export type CandidateResponse = {
	code: string;
	message: string;
	data: CandidateDate[];
};
