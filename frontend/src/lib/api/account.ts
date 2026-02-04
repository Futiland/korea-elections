import { apiGet, apiPost, apiDelete } from './common';
import {
	LoginData,
	LoginResponse,
	UserInfo,
	SignupRequestData,
	SignupResponse,
	SignupStopperResponse,
	ChangePasswordData,
	ChangePasswordResponse,
	DeleteAccountResponse,
	StatsResponse,
	SocialLoginData,
	SocialLoginResponse,
} from '../types/account';


export const login = (data: LoginData) =>
	apiPost<LoginResponse>('/rest/account/v1/signin', data);

export const getUserInfo = () =>
	apiGet<UserInfo>('/rest/account/v1/info/profile');

export const signup = (data: SignupRequestData) =>
	apiPost<SignupResponse>('/rest/account/v1/signup', data);

export const signupStopper = () =>
	apiGet<SignupStopperResponse>('/rest/account/v1/stopper');

export const changePassword = (data: ChangePasswordData) =>
	apiPost<ChangePasswordResponse>('/rest/account/v1/change-password', data);

// 회원 탈퇴
export const deleteAccount = () =>
	apiDelete<DeleteAccountResponse>('/rest/account/v1/me');

export const getStats = () =>
	apiGet<StatsResponse>('/rest/account/v1/info/stats');

// 소셜 로그인 
export const socialLogin = (data: SocialLoginData) => {
	const url = `/rest/account/v1/oauth/${data.provider}/login`;
	const frontendRedirectUrl = `${process.env.NEXT_PUBLIC_BASE_URL}${data.redirectUri}`;
	// const frontendRedirectUrl = `http://localhost:3000/${data.redirectUri}`;

	return apiGet<SocialLoginResponse>(url, {frontendRedirectUrl});
}

