export type ElectionItem = {
	id: string;
	title: string;
	description: string;
	status: 'ACTIVE' | 'INACTIVE' | 'COMPLETED';
	startDate: string;
	endDate: string;
	createdAt: string;
};
