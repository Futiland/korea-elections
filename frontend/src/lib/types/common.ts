// list
export type ListResponse<T> = {
	page: number;
	size: number;
	total: number;
	lastPage: boolean;
	totalPage: number;
	content: Array<T>;
};

export type ListResponseInfinity<T> = {
	page: number;
	size: number;
	nextCursor: number;
	content: Array<T>;
};

export type PageParam = {
	size?: number;
	page?: number;
};
