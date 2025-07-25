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
	terms: boolean;
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

export type SignupStopperResponse = {
	code: string;
	message: string;
	data: {
		status: 'ACTIVE' | 'INACTIVE';
		message: string;
	};
};

export type ChangePasswordData = {
	password: string;
	verificationId: string;
};

export type ChangePasswordResponse = {
	code: string;
	message: string;
	data: {
		token: string;
	};
};
