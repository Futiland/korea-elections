import { apiGet, apiPost } from './common';
import {
	LoginData,
	LoginResponse,
	UserInfo,
	SignupData,
	SignupResponse,
} from '../types/account';

export const login = (data: LoginData) =>
	apiPost<LoginResponse>('/rest/account/v1/signin', data);

export const getUserInfo = () =>
	apiGet<UserInfo>('/rest/account/v1/info/profile');

export const signup = (data: SignupData) =>
	apiPost<SignupResponse>('/rest/account/v1/signup', data);
