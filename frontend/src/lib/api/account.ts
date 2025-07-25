import { apiGet, apiPost } from './common';
import {
	LoginData,
	LoginResponse,
	UserInfo,
	SignupRequestData,
	SignupResponse,
	SignupStopperResponse,
	ChangePasswordData,
	ChangePasswordResponse,
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
	apiGet<ChangePasswordResponse>('/rest/account/v1/change-password', data);
