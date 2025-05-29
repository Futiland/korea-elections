export type LoginData = {
	phoneNumber: string;
	password: string;
};

export type LoginResponse = {
	code: string;
	message: string;
	data: {
		token: string;
	};
};

export type UserInfo = {
	code: string;
	message: string;
	data: {
		id: number;
		phoneNumber: string;
		name: string;
		createdAt: Date;
	};
};

export type SignupInputData = {
	phoneNumber: string;
	password: string;
	confirmPassword: string;
};

export type SignupRequestData = {
	phoneNumber: string;
	password: string;
	verificationId: string;
	verificationType: 'MOBILE';
};

export type SignupResponse = {
	code: string;
	message: string;
	data: {
		id: number;
		createdAt: Date;
		token: string;
	};
};
