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
	name: string;
	phoneNumber: string;
	password: string;
	gender: string;
	birthDate: Date;
	ci: string;
};

export type SignupResponse = {
	code: string;
	message: string;
	data: {
		id: number;
		createdAt: Date;
	};
};
