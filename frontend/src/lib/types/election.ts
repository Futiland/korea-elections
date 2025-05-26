export type ElectionItem = {
	id: string;
	title: string;
	description: string;
	status: 'ACTIVE' | 'INACTIVE';
	startDate: string;
	endDate: string;
	createdAt: string;
};

export type CandidateDate = {
	electionId: number;
	name: string;
	number: number;
	description: string;
	status: 'ACTIVE' | 'INACTIVE';
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

export type MyVotedCandidateResponse = {
	code: string;
	message: string;
	data:
		| {
				voteId: number;
				electionId: number;
				selectedCandidateId: number;
				createdAt: Date;
				updatedAt: Date;
		  }
		| null
		| undefined
		| {};
};

export type ElectionResultResponse = {
	code: string;
	message: string;
	data: {
		electionId: number;
		results: {
			id: number;
			number: number;
			name: string;
			party: string;
			description: string;
			voteCount: number;
		}[];
		totalVoteCount: number;
	};
};
