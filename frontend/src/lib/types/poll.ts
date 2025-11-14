export type QuestionType = 'SINGLE_CHOICE' | 'MULTIPLE_CHOICE' | 'SCORE';

export type PollStatus =
	| 'DRAFT'
	| 'IN_PROGRESS'
	| 'EXPIRED'
	| 'DELETED'
	| 'CANCELLED';

export type CreatePollData = {
	title: string;
	description: string;
	responseType: QuestionType;
	// startAt: Date;
	endAt: Date;
	isRevotable: boolean;
	options: {
		optionText: string;
	}[];
};

export type CreatePollResponse = {
	code: string;
	message: string;
	data: {
		id: number;
		description: string;
		responseType: QuestionType;
		startAt: Date;
		endAt: Date;
		createdAt: Date;
		status: PollStatus;
		creatorAccountId: number;
		userName: string;
		isRevotable: boolean;
		options: {
			id: number;
			optionText: string;
			optionOrder: number;
		}[];
	};
};
