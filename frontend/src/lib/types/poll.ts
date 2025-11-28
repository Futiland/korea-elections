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
	endAt: Date | string; // Date 객체 또는 ISO 문자열
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

export type PublicPollData = {
	id: number;
	title: string;
	description: string;
	responseType: QuestionType;
	status: PollStatus;
	startAt: Date | string; // 서버에서 UTC ISO 문자열로 받음
	endAt: Date | string; // 서버에서 UTC ISO 문자열로 받음
	createdAt: Date | string; // 서버에서 UTC ISO 문자열로 받음
	userName: string;
	isRevotable: boolean;
	responseCount: number;
	options: OptionData[];
};

export type OptionData = {
	id: number;
	optionText: string;
	optionOrder: number;
};

export type PublicPollResponse = {
	code: string;
	message: string;
	data: {
		content: PublicPollData[];
		nextCursor: string;
	};
};

export type PublicPollSubmitResponse = {
	code: string;
	message: string;
	data: number;
};

export type OptionResultsDate = {
	optionId: number;
	optionText: string;
	voteCount: number;
	percentage: number;
};

export type PublicPollResultResponse = {
	pollId: number;
	responseType: QuestionType;
	totalResponseCount: number;
	optionResults: OptionResultsDate[];
	scoreResult: {
		averageScore: 0.1;
		minScore: 1073741824;
		maxScore: 1073741824;
		scoreDistribution: {
			[id: number]: number;
		};
	};
};
